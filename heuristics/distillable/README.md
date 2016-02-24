# Distillability Heuristics

## Goal

We would like to know whether it's useful to run DOM distiller on a page. This
signal could be used in places like browser UI. Since this test would run on all
the page navigations, it needs to be cheap to compute. Running DOM distiller and
see if the output is empty would be too slow, and whether DOM distiller returns
results isn't necessarily equivalent to whether the page should be distilled.

Considering all the constraints, we decided to train a machine learning model
that takes features from a page, and classify it. The trained AdaBoost model is
added to Chrome in http://crrev.com/1405233009/. The pipeline except for the
model training part is described below.

## URL gathering

Gather a bunch of popular URLs that are representative of sites users frequent.
Put these URLs in a file, one per line. It might make sense to start with a
short list for dry run.

## Data preparation for labeling

Use `get_screenshots.py` to generate the screenshots of the original and
distilled web page, and extract the features by running `extract_features.js`.
You can see how it works by running the following command.

```bash
./get_screenshots.py --out out_dir --urls-file urls.txt
```

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

And then run `make -j20`. Adjust the parallelism according to how beefy your
machine is.

A small proportion of URLs would time out, or fail for some other reasons. When
you've collected enough data, run the command again with option `--write-index`
to export data for the next stage.

```bash
./get_screenshots.py --out out_dir --urls-file urls.txt --resume --write-index
```

## Labeling

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

In the step with `--write-index`, `get_screenshots.py` writes the extracted raw
features to `out_dir/feature`. We can use `calculate_derived_features.py` to
convert it to the final derived features.

```bash
./calculate_derived_features.py --core out_dir/feature --out derived.txt
```

Then use `write_features_csv.py` to combine with the label.

```bash
./write_features_csv.py --marked $(ls -rt out_dir/archive/*|tail -n1) --features derived.txt --out labeled
```
