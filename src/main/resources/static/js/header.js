function channelcheck(memberNo) {

    $.ajax({
        url: "/member/channelcheck",
        data: {"memberNo" : memberNo},
        success: function(data){
            if(data == 1) {
                alert("강사소개 작성 후 클래스 개설이 가능합니다.");
                location.href="/member/channel/"+memberNo;
            }
        }
    });
}

// 로그인 유효성 검사

function loginCheck(){

    var loginId = $("#memberId").val();
    var loginPassword = $("#memberPassword").val();

    if(loginId == null || loginId == ""){
        $("#loginBtn").attr("disabled", true);
    } else {
        $("#loginBtn").attr("disabled", false);
    }

    if(loginPassword == null || loginPassword == ""){
        $("#loginBtn").attr("disabled", true);
    } else {
        $("#loginBtn").attr("disabled", false);
    }

}