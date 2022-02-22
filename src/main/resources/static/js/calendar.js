/*
@Author : 김정진
@Date : 2022-02-06 ~ 2022-02-09
@Note :
    1. 클릭 이벤트를 통해서 날짜만 전달하는 함수입니다.
    2. calendar2.js 는 DB에서 데이터를 넘겨주고 이벤트를 할당하는 js 입니다.
    3. renderCalender 에 인수를 넘겨주며 하나로 통합해서 사용할 수도 있습니다.
    4. 다만 클래스 날짜 등록 페이지에서는 심플한 달력만을 출력하기 위해 굳이 구분해서 사용한다는 점을 기록해둡니다.
*/

$(document).ready(function() {
    calendarInit();
});

function test(year , month , day){
    var date = year + "-" + (month + 1) + "-" + day;
    var selectedDate = new Date(date);
    $("#selectedDate").val(date);
    $("#roomDate").val(date);
}

/*
    달력 렌더링 할 때 필요한 정보 목록
    현재 월(초기값 : 현재 시간)
    금월 마지막일 날짜와 요일
    전월 마지막일 날짜와 요일
*/

// 캘린더를 출력하는 함수
function calendarInit() {

    // 날짜 정보 가져오기
    var date = new Date(); // 현재 날짜(로컬 기준) 가져오기
    var utc = date.getTime() + (date.getTimezoneOffset() * 60 * 1000); // uct 표준시 도출
    var kstGap = 9 * 60 * 60 * 1000; // 한국 kst 기준시간 더하기
    var today = new Date(utc + kstGap); // 한국 시간으로 date 객체 만들기(오늘)

    var thisMonth = new Date(today.getFullYear(), today.getMonth(), today.getDate());
    // 달력에서 표기하는 날짜 객체

    var currentYear = thisMonth.getFullYear(); // 달력에서 표기하는 연
    var currentMonth = thisMonth.getMonth(); // 달력에서 표기하는 월
    var currentDate = thisMonth.getDate(); // 달력에서 표기하는 일

    // kst 기준 현재시간
    // console.log(thisMonth);

    // 캘린더 렌더링
    renderCalender(thisMonth);

    function renderCalender(thisMonth) {

        // 렌더링을 위한 데이터 정리
        currentYear = thisMonth.getFullYear();
        currentMonth = thisMonth.getMonth();
        currentDate = thisMonth.getDate();

        // 이전 달의 마지막 날 날짜와 요일 구하기
        var startDay = new Date(currentYear, currentMonth, 0);
        var prevDate = startDay.getDate();
        var prevDay = startDay.getDay();

        // 이번 달의 마지막날 날짜와 요일 구하기
        var endDay = new Date(currentYear, currentMonth + 1, 0);
        var nextDate = endDay.getDate();
        var nextDay = endDay.getDay();

        // console.log(prevDate, prevDay, nextDate, nextDay);

        // 현재 월 표기
        $('.year-month').text(currentYear + '.' + (currentMonth + 1));

        // 렌더링 html 요소 생성
        calendar = document.querySelector('.dates');
        calendar.innerHTML = '';

        // YY-MM-DD 형태로 id 값을 부여한다.

        // 지난달
        for (var i = prevDate - prevDay + 1; i <= prevDate; i++) {
            calendar.innerHTML = calendar.innerHTML + '<div class="day prev disable day-select">' + i + '</div>';
        }

        // 이번달
        for (var i = 1; i <= nextDate; i++) {
            // id : 2022-02-22
            var dayId = currentYear + "-" + (currentMonth+1) + "-" + i;
            // var currentMonth = currentMonth + 1; for 문 안에서 사용하니 자꾸 숫자가 증가한다.
            // var dayId = currentYear+"".concat(',', tmp, ',', i);
            calendar.innerHTML = calendar.innerHTML + '<div onclick="test('+currentYear+','+currentMonth+','+i+')" class="day current day-select" id="'+dayId+'">' + i + '</div>';
            /*
            해당 버튼 id 를 부여해서 클릭 이벤트를 부여한다. -> 일단 실패
            */

            /*
            addEventListener 가 먹히지 않는다. 왜 안먹히는지 원인을 찾는 중인데 일단 잠정 보류
            날짜가 출력되는 html 내에 script 선언해서 테스트해봐야함
            var dayPickedId = "#" + dayId;
            var dayPicked = $(dayPickedId);
            dayPicked.addEventListener("click", () => { alert("hi"); });
            */
        }

        // 다음달
        for (var i = 1; i <= (7 - nextDay == 7 ? 0 : 7 - nextDay); i++) {
            calendar.innerHTML = calendar.innerHTML + '<div class="day next disable day-select">' + i + '</div>';
        }
        // 오늘 날짜 표기
        if (today.getMonth() == currentMonth) {
            todayDate = today.getDate();
            var currentMonthDate = document.querySelectorAll('.dates .current');
            currentMonthDate[todayDate -1].classList.add('today');
        }

    }


    // 이전달로 이동
    $('.go-prev').on('click', function() {
        thisMonth = new Date(currentYear, currentMonth - 1, 1);
        renderCalender(thisMonth);
        $(".dates").load(location.href + ".dates");
    });

    // 다음달로 이동
    $('.go-next').on('click', function() {
        thisMonth = new Date(currentYear, currentMonth + 1, 1);
        renderCalender(thisMonth);
        $(".dates").load(location.href + ".dates");
    });

    // 캘린더에 YYYY-MM-DD 데이터를 넘겨서 해당하는 day 값만 바꾸고 이벤트를 등록하는 함수




}