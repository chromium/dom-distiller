function sendRequest(url, callback) {
  var xhr = new XMLHttpRequest();
  xhr.onreadystatechange = function() {
    if (xhr.readyState == 4) {
      callback((xhr.status == 200) || (xhr.status == 304), xhr.responseText);
    }
  }
  xhr.onerror = function() {
    callback(false, undefined);
  }
  xhr.open("GET", url, true);
  xhr.send();
}

// These will hold the contents of the respective files for injecting into the
// inspected window.
var extract, domdistiller;

sendRequest("extract.js", function(success, val) {
  if (success) extract = val;
  updateButtonReady();
});
sendRequest("domdistiller.js", function(success, val) {
  if (success) domdistiller = val;
  updateButtonReady();
});

var button
var extracting = false;

function updateButtonReady() {
  if (!button) return;
  var ready = (extract !== undefined) && (domdistiller !== undefined) && !extracting;
  button.disabled = !ready;
}

window.onload = function() {
  button = document.getElementById("button");
  updateButtonReady();
  button.onclick = function() {
    extracting = true;
    updateButtonReady();

    chrome.devtools.inspectedWindow.eval(domdistiller);
    chrome.devtools.inspectedWindow.eval(extract, function(res, err) {
      extracting = false;
      updateButtonReady();
    });
  }
}
