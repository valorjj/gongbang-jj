/*
    @Author: 김정진
    @Param name : 이름
    @Note : 이 함수를 작성한 의도
*/
function mdelete(){

    var passwordconfirm = $("#passwordconfirm").val();

    $.ajax({
        url: "/member/mdelete" ,
        data : { "passwordconfirm" : passwordconfirm } ,
        success : function(data){
                if( data == 1 ){
                alert("회원탈퇴 성공!!")
                    location.href="/member/logout";
                }else{
                alert("회원탈퇴 실패..")
                    $("#deletemsg").html("[회원탈퇴실패] 비밀번호가 다릅니다.");
                    $("#deletemsg").css('color', 'red');
                }
        }
    });
}