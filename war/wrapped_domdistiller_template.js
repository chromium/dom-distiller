// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

// Creates a DomDistiller, applies to to the content of the page, and returns
// a DomDistillerResults as a JavaScript object/dictionary.
(function(options) {
  try {
    // The generated domdistiller.js accesses the window object only explicitly
    // via the window name. This creates a new object with the normal window
    // object as its prototype and initialize the domdistiller.js with that new
    // context so that it does not change the real window object.
    function initialize(window) {
      $$DISTILLER_JAVASCRIPT
    }
    var context = Object.create(window);
    context.setTimeout = function() {};
    context.clearTimeout = function() {};
    initialize(context);

    var distiller = context.org.chromium.distiller.DomDistiller;
    var res = distiller.applyWithOptions(options);
    return res;
  } catch (e) {
    window.console.error("Error during distillation: " + e);
    if (e.stack != undefined) window.console.error(e.stack);
  }
  return undefined;
// The OPTIONS placeholder will be replaced with the DomDistillerOptions at
// runtime.
})($$OPTIONS)
