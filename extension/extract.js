// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

var options = {};
console.profile("Extraction");
var res = com.dom_distiller.DomDistiller.applyWithOptions(options);
console.profileEnd();

