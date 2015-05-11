// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

function onTabsUpdated(tabId, changeInfo, tab) {
  chrome.pageAction.show(tabId);
}

function onPageActionClicked(tab) {
  chrome.tabs.executeScript({ file: "domdistiller.js" }, function() {
    chrome.tabs.executeScript({ file: "extract.js" }, function() {
      chrome.tabs.executeScript({ file: "preview.js" });
    });
  });
}

chrome.tabs.onUpdated.addListener(onTabsUpdated);
chrome.pageAction.onClicked.addListener(onPageActionClicked);
