    /*
        @Author : 김정진
        @Date : 2022-02-09
    */

function inputCheck(){

    // 선택한 시간, 날짜 유효성 검사
    var beginTime = $("#beginTime").val();
    var endTime = $("#endTime").val();
    var date = $("#selectedDate").val();

    if(date == "" || date == null){
        $("#dateMsg").html("날짜가 선택되지 않았습니다.");
        return false;
    } else {
        $("#dateMsg").html("");
    }

    if(beginTime == "" || beginTime == null){
        console.log("시작하는 시간을 선택해주세요");
        return false;
    }

    if(endTime == "" || endTime == null){
        console.log("종료하는 시간을 선택해주세요");
        return false;
    }

    if(endTime < beginTime){
        console.log("시간 시간 : " + beginTime);
        console.log("종료 시간 : " + endTime);
        console.log("종료시간이 시작시간보다 빠를 수 없습니다. ");
        return false;
    }

}
