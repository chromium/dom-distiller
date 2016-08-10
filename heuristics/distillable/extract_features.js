return (function() {
  function hasOGArticle() {
    var elems = document.head.querySelectorAll(
        'meta[property="og:type"],meta[name="og:type"]');
    for (var i in elems) {
      if (elems[i].content && elems[i].content.toUpperCase() == 'ARTICLE') {
        return true;
      }
    }
    return false;
  }

  function isVisible(e) {
    var bounds = e.getBoundingClientRect()
    var style = window.getComputedStyle(e);
    return !(
      (bounds.height == 0 && bounds.width == 0) ||
      style.display == "none" ||
      style.visibility == "hidden" ||
      style.opacity == 0
    )
  }

  function countVisible(nodes) {
    var count = 0;
    for (var i = 0; i < nodes.length; i++) {
      var node = nodes[i];
      if (!isVisible(node)) {
        continue;
      }
      count++;
    }
    return count;
  }

  var unlikelyCandidates = /banner|combx|comment|community|disqus|extra|foot|header|menu|related|remark|rss|share|shoutbox|sidebar|skyscraper|sponsor|ad-break|agegate|pagination|pager|popup/i;
  var okMaybeItsACandidate = /and|article|body|column|main|shadow/i;

  function mozScore() {
    return _mozScore(true, 0.5, 140, true, 1e100);
  }

  function _mozScore(trim, power, cut, excludeLi, saturate) {
    var score = 0;

    var nodes = document.querySelectorAll('p,pre')
    for (var i = 0; i < nodes.length; i++) {
      var node = nodes[i];
      if (!isVisible(node)) {
        continue;
      }
      var matchString = node.className + " " + node.id;
      if (unlikelyCandidates.test(matchString) &&
           !okMaybeItsACandidate.test(matchString)) {
        continue;
      }

      if (excludeLi && node.matches && node.matches("li p")) {
        continue;
      }

      var textContent = node.textContent;
      if (trim) textContent = textContent.trim();
      var textContentLength = textContent.length;
      textContentLength = Math.min(saturate, textContentLength)
      if (textContentLength < cut) {
        continue;
      }

      score += Math.pow(textContentLength - cut, power);
    }
    return score;
  }

  var body = document.body;
  var features = {
     'opengraph': hasOGArticle(),
     'url': document.location.href,
     'title': document.title,
     'numElements': body.querySelectorAll('*').length,
     'numAnchors': body.querySelectorAll('a').length,
     'numForms': body.querySelectorAll('form').length,
     'numTextInput': body.querySelectorAll('input[type="text"]').length,
     'numPasswordInput': body.querySelectorAll('input[type="password"]').length,
     'numPPRE': body.querySelectorAll('p,pre').length,
     'innerText': body.innerText,
     'textContent': body.textContent,
     'innerHTML': body.innerHTML,
     'mozScore': Math.min(6 * Math.sqrt(1000 - 140), _mozScore(false, 0.5, 140, true, 1000)),
     'mozScoreAllSqrt': Math.min(6 * Math.sqrt(1000), _mozScore(false, 0.5, 0, true, 1000)),
     'mozScoreAllLinear': Math.min(6 * 1000, _mozScore(false, 1, 0, true, 1000)),
     'visibleElements': countVisible(body.querySelectorAll('*')),
     'visiblePPRE': countVisible(body.querySelectorAll('p,pre')),
  }
  return features;
})()
