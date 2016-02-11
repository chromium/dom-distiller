data = null;
idx = 0;

nextUpdateId = 0;

urlHolder = document.querySelector('#url-holder');
dataTable = document.querySelector('#data-table');
$dataRows = null;

baseImage = document.querySelector('#base-img');
distilledImage = document.querySelector('#distilled-img');
baseImageNext = document.querySelector('#base-img-next');
distilledImageNext = document.querySelector('#distilled-img-next');
baseImageNext2 = document.querySelector('#base-img-next2');
distilledImageNext2 = document.querySelector('#distilled-img-next2');
imageHolder = document.querySelector('#image-holder');

goodButton = document.querySelector('#good');
badButton = document.querySelector('#bad');
resetButton = document.querySelector('#reset');
poorButton = document.querySelector('#poor');
errorButton = document.querySelector('#error');

jumpCheck = document.getElementById('auto-advance');

goodCount = document.querySelector('#good-count');
badCount = document.querySelector('#bad-count');
poorCount = document.querySelector('#poor-count');
errorCount = document.querySelector('#error-count');

function changeIndex(newIndex) {
  var oldIndex = idx;
  idx = Math.max(0, Math.min(data.length - 1, newIndex));
  showImage(idx);
  $dataRows.eq(oldIndex).removeClass('active');
  var $currentRow = $dataRows.eq(idx);
  $currentRow.addClass('active');
  $currentRow[0].scrollIntoViewIfNeeded();
  urlHolder.scrollIntoViewIfNeeded();
  $('.current-position').text(idx + 1);
  window.location.hash = data[idx]['index'];
}

function updateCount() {
  goodCount.innerText = document.querySelectorAll('tr[class*="good"]').length
  badCount.innerText = document.querySelectorAll('tr[class*="bad"]').length
  poorCount.innerText = document.querySelectorAll('tr[class*="poor"]').length
  errorCount.innerText = document.querySelectorAll('tr[class*="error"]').length
}

function recordValue(v) {
  var e = data[idx];
  e['good'] = v;
  updateTableRow(idx);
  sendUpdate(e, function() {
    changeIndex(nextIndex(idx));
  });
  updateCount();
}

function recordGood() {
  recordValue(1);
}

function recordBad() {
  recordValue(0);
}

function recordError() {
  recordValue(-1);
}

function resetEntry() {
  var e = data[idx];
  delete e['good']
  updateTableRow(idx);
  sendUpdate(e, function() {
  });
}

function sendMessage(type, message, callback) {
  $.ajax({
    'type': type,
    'url': '/message',
    'contentType': 'application/json',
    'processData': false,
    'data': JSON.stringify(message),
    'error': function(response, status) {
      console.log('wtf response: ', response, status);
    },
    'success': function(response) {
      console.log('response: ', response);
      callback(response);
    },
    'dataType': 'json',
  });
}

function sendUpdate(e, callback) {
  sendMessage(
      'POST',
      {
        'action': 'update',
        'data': e,
      },
      callback);
}

function back() {
  changeIndex(idx - 1);
}

function skip() {
  changeIndex(idx + 1);
}

poorButton.onclick = function() { recordValue(2); }
goodButton.onclick = recordGood
badButton.onclick = recordBad
resetButton.onclick = resetEntry
errorButton.onclick = recordError
$('.right-caret').click(skip);
$('.left-caret').click(back);

function showImage(i) {
  goodButton.disabled = false;
  badButton.disabled = false;
  var e = data[i];
  baseImage.src = '/images/' + e['screenshot'].replace(/.*\//, '');
  distilledImage.src = '/images/' + e['distilled'].replace(/.*\//, '');
  urlHolder.innerText = decodeURI(e['url']);
  urlHolder.href = e['url'];
  imageHolder.classList.remove('bad');
  imageHolder.classList.remove('good');
  imageHolder.classList.remove('error');
  imageHolder.classList.remove('poor');
  if (typeof e['good'] != 'undefined')  {
    imageHolder.classList.add(
        e['good'] == 1
          ? 'good'
          : e['good'] == -1
            ? 'error'
            : e['good'] == 2
              ? 'poor'
              : 'bad');
  }
  var n = data[i + 1];
  if (n) {
    baseImageNext.src = '/images/' + n['screenshot'].replace(/.*\//, '');
    distilledImageNext.src = '/images/' + n['distilled'].replace(/.*\//, '');
  }
  n = data[nextUnrated(i)];
  if (n) {
    baseImageNext2.src = '/images/' + n['screenshot'].replace(/.*\//, '');
    distilledImageNext2.src = '/images/' + n['distilled'].replace(/.*\//, '');
  }
}

function nextIndex(i) {
  return jumpCheck.checked ? nextUnrated(i) : (i + 1);
}

function nextUnrated(i) {
  i += 1;
  while (i < data.length) {
    var e = data[i];
    if (typeof e['good'] == 'undefined') {
      return i;
    }
    i += 1;
  }
  return -1;
}


function updateTableRow(i) {
  var row = dataTable.querySelectorAll('tr')[i];
  row.classList.remove('good');
  row.classList.remove('bad');
  row.classList.remove('error');
  row.classList.remove('poor');
  var e = data[i];
  if (typeof e['good'] != 'undefined') {
    row.classList.add(
        e['good'] == 1
          ? 'good'
          : e['good'] == -1
            ? 'error'
            : e['good'] == 2
              ? 'poor'
              : 'bad');
  }
  if (i == idx) {
    // forces ui update
    changeIndex(idx);
  }
}

function createTable() {
  var dataRows = [];
  for (var i = 0; i < data.length; i++) {
    row = document.createElement('tr');
    cell = document.createElement('td');
    row.appendChild(cell);
    cell.appendChild(document.createTextNode(decodeURI(data[i]['url'])));
    dataTable.appendChild(row);
    dataRows.push(row);
  }

  $dataRows = $(dataRows);

  $dataRows.each(function(i, r) {
    $(r).click(function() {
      console.log(i);
      // forces ui update
      changeIndex(i);
    });
  })
}

function setData(d) {
  var needsTable = data == null;
  data = d; //.slice(0, 100);
  $('.total-entries').text(data.length);
  if (needsTable) {
    createTable();
  }
  idx = getHashIndex();
  if (idx == -1) {
    idx = Math.floor(Math.random() * data.length);
  }
  for (var i = 0; i < data.length; i++) {
    updateTableRow(i);
  }
  updateCount();
}

function handleVisibilityChange() {
  if (!document.hidden) sendGetUpdates(1000);
}

function getData() {
  sendMessage(
      'POST',
      {
        'action': 'getData'
      },
      function(rsp) {
        document.addEventListener('visibilitychange', handleVisibilityChange, false);
        var data = rsp['response']['data'];
        nextUpdateId = rsp['response']['nextId'];
        setData(data);
        sendGetUpdates(1000);
      }
    );
}


getUpdatesSent = false;
function sendGetUpdates(delay) {
  if (document.hidden) return;
  if (getUpdatesSent) return;
  getUpdatesSent = true;
  setTimeout(function() { getUpdates(delay); }, delay);
}

function getUpdates(delay) {
  $.ajax({
    'type': 'GET',
    'url': '/getupdates?nextId=' + nextUpdateId,
    'contentType': 'application/json',
    'processData': false,
    'error': function(response, status) {
      console.log('xx: wtf response: ', response, status);
    },
    'success': function(rawResponse) {
      getUpdatesSent = false;
      var response = rawResponse['response'];
      nextUpdateId = response['nextId'];
      if (response['data'] != null) {
        setData(response['data']);
      } else if (response['updates'] != null) {
        updates = response['updates'];
        for (var i = 0; i < updates.length; i++) {
          var u = updates[i];
          var idx = u['index'];
          var entry = u['entry'];
          var curr = data[idx];
          if (curr['url'] != entry['url']) {
            console.error(curr, entry);
          }
          data[idx] = entry;
          updateTableRow(idx);
        }
        updateCount();
        nextUpdateId = nextUpdateId;
        delay = 1000;
      } else {
        delay = Math.min(30 * 1000, 1.5 * delay);
      }
      console.log('xx: response: ', response, delay);
      sendGetUpdates(delay);
    },
    'dataType': 'json',
  });
}

logkeys = false
$(document).ready(function(){
  getData();
  $(document).keypress(function(e){
    if (logkeys) {
      console.log(e)
    }
    switch (e.which) {
      case 93: // /
      case 47: // ]
        recordValue(2);
        break;
      case 42: // *
      case 48: // 0
        recordError();
        break;
      case 43: // +
      case 61: // =
        recordGood();
        break;
      case 45: // -
        recordBad();
        break;
    }
  });
  $(document).keydown(function(e) {
    switch (e.which) {
      case 37: // <--
      case 39: // >--
        e.preventDefault();
    }
  });
  $(document).keyup(function(e){
    switch (e.which) {
      case 37: // <--
        back();
        break;
      case 39: // >--
        skip();
        break;
    }
  });
});

function getHashIndex() {
  if (!window.location.hash) {
    return -1;
  }
  index = parseInt(window.location.hash.substr(1));
  if (isNaN(index)) {
    return -1;
  }
  if (data && parseInt(data[idx]['index']) == index) {
    return idx;
  }
  for (var i = 0; i < data.length; i++) {
    if (parseInt(data[i]['index']) == index) {
      return i;
    }
  }
  return -1;
}

$(window).on('hashchange', function () {
  hashIdx = getHashIndex();
  if (hashIdx != idx) {
    changeIndex(hashIdx);
  }
});