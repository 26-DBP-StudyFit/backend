/*
 * 월간 캘린더 (서버에서 날짜 계산을 하지 않고 브라우저에서 그린다)
 *
 * JSP 쪽 사용법:
 *   <div id="cal"></div>
 *   <ul id="cal-events" hidden>
 *     <li data-date="2026-11-14">스터디</li>      yyyy-MM-dd 와 표시할 문구
 *   </ul>
 *   <script src="${ctx}/js/calendar.js"></script>
 *   <script>StudyFitCalendar.init('cal', 'cal-events', { chips: true });</script>
 *
 * 옵션 chips: true  → 일정 날짜 칸에 문구 칩 표시 (팀 메인)
 *        chips: false → 점(•)만 표시 (홈)  [기본값]
 */
var StudyFitCalendar = (function () {
  function pad(n) { return n < 10 ? '0' + n : '' + n; }
  function esc(s) { return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/"/g, '&quot;'); }

  function init(calId, eventsId, opts) {
    opts = opts || {};
    var root = document.getElementById(calId);
    if (!root) return;
    var events = {};
    var list = document.getElementById(eventsId);
    if (list) {
      Array.prototype.forEach.call(list.querySelectorAll('li[data-date]'), function (li) {
        var d = li.getAttribute('data-date');
        (events[d] = events[d] || []).push(li.textContent.trim());
      });
    }
    var now = new Date();
    var todayKey = now.getFullYear() + '-' + pad(now.getMonth() + 1) + '-' + pad(now.getDate());
    var year = now.getFullYear();
    var month = now.getMonth();

    function render() {
      var first = new Date(year, month, 1).getDay();
      var last = new Date(year, month + 1, 0).getDate();
      var html = '<div class="cal-head"><button type="button" data-move="-1" aria-label="이전 달">&lt;</button>' +
        '<span>' + year + '년 ' + (month + 1) + '월</span>' +
        '<button type="button" data-move="1" aria-label="다음 달">&gt;</button></div>' +
        '<div class="cal-grid dow">' + ['일', '월', '화', '수', '목', '금', '토'].map(function (d) { return '<span>' + d + '</span>'; }).join('') + '</div>' +
        '<div class="cal-grid">';
      var i, cls = opts.chips ? 'cal-cell tall' : 'cal-cell';
      for (i = 0; i < first; i++) html += '<div class="' + cls + '"></div>';
      for (var day = 1; day <= last; day++) {
        var key = year + '-' + pad(month + 1) + '-' + pad(day);
        var ev = events[key];
        html += '<div class="' + cls + '">';
        html += key === todayKey ? '<span class="today">' + day + '</span>' : '<span>' + day + '</span>';
        if (ev) html += opts.chips ? '<div class="ev" title="' + esc(ev.join(', ')) + '">' + esc(ev[0]) + '</div>' : '<span class="dot" title="' + esc(ev.join(', ')) + '"></span>';
        html += '</div>';
      }
      html += '</div>';
      root.className = 'cal';
      root.innerHTML = html;
      Array.prototype.forEach.call(root.querySelectorAll('[data-move]'), function (b) {
        b.addEventListener('click', function () {
          month += parseInt(b.getAttribute('data-move'), 10);
          if (month < 0) { month = 11; year--; }
          if (month > 11) { month = 0; year++; }
          render();
        });
      });
    }
    render();
  }
  return { init: init };
})();
