# Distillability Heuristics

## Goal

We would like to know whether it's useful to run DOM distiller on a page. This
signal could be used in places like browser UI. Since this test would run on all
the page navigations, it needs to be cheap to compute. Running DOM distiller to
see if the output is empty would be too slow, and whether DOM distiller returns
results isn't necessarily equivalent to whether the page should be distilled.

Considering all the constraints, we decided to train a machine learning model
that takes features from a page, and classify it. The trained AdaBoost model is
added to Chrome in http://crrev.com/1405233009/ to predict whether a page
contains an article. Another model added in http://crrev.com/1703313003 predicts
whether the article is long enough. The pipeline except for the model training
part is described below.

## URL gathering

Gather a bunch of popular URLs that are representative of sites users frequent.
Put these URLs in a file, one per line. It might make sense to start with a
short list for dry run.

## Data scrawling

Use `get_screenshots.py` to generate the screenshots of the original and
distilled web page, and extract the features by running `extract_features.js`.
You can see how it works by running the following command.

```bash
./get_screenshots.py --out out_dir --urls-file urls.txt
```

Append option `--emulate-mobile` if mobile-friendliness is important, and use
`--save-mhtml` to keep a copy in MHTML format.

If everything goes fine, run it inside xvfb. Specifying the screen resolution
makes the size of the screenshots consistent. It also prevent the Chrome window
from interrupting your work on the main monitor.

```bash
xvfb-run -a -s "-screen 0 1600x5000x24" ./get_screenshots.py --out out_dir --urls-file urls.txt
```

One entry takes about 30 seconds. Depending on the number of entries, it could
be a lengthy process. If it is interrupted, you could use option `--resume` to
continue.

```bash
xvfb-run -a -s "-screen 0 1600x5000x24" ./get_screenshots.py --out out_dir --urls-file urls.txt --resume
```

Running multiple instances concurrently is recommended if the list is long
enough. You can create a Makefile like this:

```make
ALL=$(addsuffix .target,$(shell seq 1000))

all: $(ALL)

%.target :
        xvfb-run -a -s "-screen 0 1600x5000x24" ./get_screenshots.py --out out_dir --urls-file urls.txt --resume
```

And then run `nice make -j10 -k`. Adjust the parallelism according to how beefy
your machine is. The `-k` option is essential for it to keep going.

**Tips and caveats:**

-   Use tmpfs for /tmp to avoid thrashing your disk. It would easily be IO-bound
    even if you use SSD for /tmp.
-   `atop` is useful when experimenting the parallelism.
-   For a 40-core, 64G workstation, `-j80` can keep it CPU-bound, with
    throughput of ~100 entries/minute.
-   You might need to manually kill a few stray Chrome or xvfb-run processes
    after hitting `Ctrl-C` for `make`.

A small proportion of URLs would time out, or fail for some other reasons. When
you've collected enough data, run the command again with option `--write-index`
to export data for the next stage.

```bash
./get_screenshots.py --out out_dir --urls-file urls.txt --write-index
```

## Labeling

This section is only needed for distillability model, but not for long-article
model.

Use `server.py` to serve the web site for data labeling. Human effort is around
10~20 seconds per entry.

```bash
./server.py --data-dir out_dir
```

It should print something like:

```
[21/Jan/2016:22:53:53] ENGINE Serving on 0.0.0.0:8081
```

Then visit that address in your browser.

The labels would be written to `out_dir/archive/` periodically.

## Data preparation for training

### Feature re-extraction from MHTML archive

When experimenting with feature extraction, being able to extract new features
is useful. After modifying `extract_features.js`, modify the Makefile and change
the command to:

```bash
xvfb-run -a -s "-screen 0 1600x5000x24" ./get_screenshots.py --out out_dir --urls-file urls.txt --load-mhtml --skip-distillation
```

Then rerun `nice make -j10 -k`.

### Recalculating derived features without extracting again

This section is usually optional. You might need this if you've changed how
features are derived, and want to recalculate the derived features from the raw
features, without extracting the raw features again. This can be useful because
feature re-extraction from MHTML archive can sometimes differs from the original
web page. Otherwise, if you are dealing with an older dataset where the derived
features are not calculated when scrawling, this is also necessary.

The derived features are saved to `out_dir/*.feature-derived` when scrawling
each entry. In the step with `--write-index`, `get_screenshots.py` writes the
derived features to `out_dir/feature-derived`. To save time, raw features are
not aggregated by default, but you can uncomment the line in function
`writeFeature()` to write the raw features to `out_dir/feature` as well. We can
then use `calculate_derived_features.py` to convert it to the derived features.

```bash
./calculate_derived_features.py --core out_dir/feature --out out_dir/feature-derived
```

### Sanity check

This step is optional.

`check_derived_features.py` compares the derived features between JavaScript
implementation and the native implementation in Chrome. This only works if your
Chrome is new enough to support distillability JSON dumping (with command line
argument --distillability-dev).

```
./check_derived_features.py --features out_dir/feature-derived
```

Or if you want to compare the features derived from MHTML archive, do this:

```
./check_derived_features.py --features out_dir/mfeature-derived --from-mhtml
```

When comparing the features extracted from the original page, the error rate
would be higher because the feature extractions by JS and native code are done
at different events, and the DOM could change dynamically. On the other hand,
features extracted from MHTML archive should be exactly the same. However, due
to issues like https://crbug.com/586034, MHTML is not fully offline, and the
results can be non-deterministic. Sadly there are currently no good way in
webdriver to force offline behavior. Other than that, mismatches between the two
implementations should be regarded as bugs.

`check_distilled_mhtml.py` compares the distilled content from the original page
with the distilled content from the MHTML archive.

```
./check_distilled_mhtml.py --dir out_dir
```

These two should be exactly the same. Known differences includes:

-   The original page has next page stitched.
-   In some rare cases, MHTML would fail to distill and get no data.

We still have inconsistencies that need investigation.

### Final output for training

Use `write_features_csv.py` to combine derived features with the label.

For distillability model, run:

```
./write_features_csv.py --marked $(ls -rt out_dir/archive/*|tail -n1) --features out_dir/feature-derived --out labelled
```

Or for long-article model, run:

```
./write_features_csv.py --distilled out_dir/dfeature-derived --features out_dir/feature-derived --out labelled
```

Then lots of files named `labelled-*.csv` would be created.

## Misc

After scrawling the MHTML files using `get_screenshots.py` with `--save-mhtml`
option, you can convert it to a corpus file by `gen_mhtml_corpus.py`. The
detailed usage is in the header of `gen_mhtml_corpus.py`.
