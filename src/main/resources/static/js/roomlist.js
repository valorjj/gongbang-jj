
// [ADMIN 권한] 클래스 삭제
function rdelete(roomNo){
    $.ajax({
        url:"/admin/delete",
        data:{"roomNo":roomNo},
        success:function(data){
            if(data==1){
                location.href ="/admin/adminlist";
            }
        }
    });
}

// [ADMIN 권한] 클래스 상태변경
function activeupdate(roomNo, active) {
    $.ajax({
        url:"/admin/activeupdate",
        data:{"roomNo":roomNo, "active":active},
            success:function(data){
            if(data==1){
                location.href ="/admin/adminlist";
            } else {
                $("#activemsg").html("현재 동일한 상태입니다.");
            }
        }
    });
}

// [ADMIN 권한] 클래스 전체 조회
function adminSearch(){

    var keyword = $("#adminSearch").val();
    var category = $("#adminCategory").val();
    var local = $("#adminLocal").val();

    keyword = isEmpty(keyword, -1);
    category = isEmpty(category, -1);
    local = isEmpty(local, -1);

    // 1. 검색 X 지역 X 카테고리 X
    if(keyword == -1 && category == -1 && local == -1) {
        $.ajax({
            url: "/admin/adminTableAll",
            success: function(data){
                // 1. 기존에 존재하는 html 코드 제거
                $("#table-section").empty();
                $("#pagination-section").empty();
                // 2. 전체 목록을 #table-section 태그에 뿌려줘야한다.
                $("#table-section").append(data);
            }
        });
    }
    // 2. 검색, 지역, 카테고리 셋중 하나라도 있는 경우
    // 아직 미완성이라 빌드할 때는 주석처리 해야합니다.
    else {
        $.ajax({
            url: "/admin/adminTableBySearch",
            data: {"keyword" : keyword, "category" : category, "local" : local},
            success: function(data){
                // 1. 기존에 존재하는 html 코드 제거
                $("#table-section").empty();
                $("#pagination-section").empty();
                $("#table-section").append(data);
            }
        });
    }
}

function isEmpty(str, defaultStr){
    if(typeof str == "undefined" || str == null || str == ""){
        str = defaultStr;
    }
    return str;
}
