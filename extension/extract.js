// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

var options = {};
console.profile("Extraction");
var res = org.chromium.distiller.DomDistiller.applyWithOptions(options);
console.profileEnd("Extraction");
console.log(res)
