
/*
@Author : 김정진
@Date : 2022-02-09~
@Note :
    1. calendar2 와 달리 calendar3 에서는 로그인된 회원의 전체 예약 내역을 출력해야합니다.
    2. roomNo 가 아닌 memberNo 받고 History, TimeTable, Room 에 접근해야 합니다.
    3.
*/

$(document).ready(function() {
    getTimeTable();
});


// memberNo 는 로그인 세션에서 받습니다.
function daySelect(year , month , day){
    var date = year + "-" + (month + 1) + "-" + day;
    // 세션에 저장되어 있는 memberNo 를 활용해서 History List 를 불러옵니다.

    // 날짜 클릭 시, 기존에 출력이 되어있던 영역을 숨겨야합니다.
    $(".reservation-content").hide();

    $.ajax({
        url: "/member/memberHistoryJSON", // RoomEntity 를 JSON 형태로 받아온다.
        data: {"date" : date},
        method: "GET",
        async: false,
        contentType: "application/json",
        success: function(data){
            // 기존에 있던 데이터를 지운다.
            $(".reservation-content").empty();
            $(".reservation-date-select").empty();
            $(".reservation-date-select").append(data);
        }
    });
}

// 특정 날짜, 특정 시간 클래스를 신청한다.
function registerClass(roomNo, beginTime, endTime, roomDate){
    var classTime = beginTime + "," + endTime;
    $.ajax({
        url: "/member/registerClass",
        data: {"roomNo" : roomNo, "classTime" : classTime, "roomDate" : roomDate},
        method: "GET",
        success: function(data){
            if(data==1){
                alert("성공");
            }
        }
    });
}

/*
    달력 렌더링 할 때 필요한 정보 목록
    현재 월(초기값 : 현재 시간)
    금월 마지막일 날짜와 요일
    전월 마지막일 날짜와 요일
*/

// DB 연동해서 데이터 가져오는 함수
function getTimeTable(){
    $.ajax({
        url: "/member/reservation",
        method: "GET",
        success: function(data) {
            calendarInit(data);
        }
    });
}

// 캘린더 출력하는 함수
function calendarInit(data) {
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

        // 지난달 달력 출력
        for (var i = prevDate - prevDay + 1; i <= prevDate; i++) {
            calendar.innerHTML = calendar.innerHTML + '<div class="day prev disable day-select">' + i + '</div>';
        }

        // DB 에 등록되어 있는 값들을 불러와서 ',' 로 split 한다.
        // [날짜1, 날짜2, ... , ] 식으로 불러오므로, 마지막에는 값이 없다.

        var dataSplit = data.split(",");
        // 이번달 달력 출력하는 반복문
        for (var i = 1; i <= nextDate; i++) {
            var flag = false;
            // id : YYYY-MM-DD
            let dayId = currentYear + "-" + (currentMonth + 1) + "-" + i;
            let count = dataSplit.length;
            // while 문 안에서 카운트
            // 강좌가 개설되었으면, j++ 시키며 다음 날짜부터 검색한다.
            let j = 0;
            while( j < count ){
                if(dayId == dataSplit[j]){
                    calendar.innerHTML = calendar.innerHTML + '<div style="color: orange;" onclick="daySelect(' + currentYear + ',' + currentMonth + ',' + i + ')" class="day current day-select active" id="'+dayId+'">' + i + '</div>';
                    j = j + 1;
                    flag = true;
                    break;
                } else {

                    j = j + 1;
                }
            }
            if(flag == true){
            } else {
                calendar.innerHTML = calendar.innerHTML + '<div style="color: gray;" class="day current" id="'+dayId+'">' + i + '</div>';
            }


        }

        // 다음달 달력 출력
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
}

