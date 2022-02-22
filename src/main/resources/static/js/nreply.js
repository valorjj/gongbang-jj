function notereply(noteNo,noteContents){ // 답변하기 클릭 이벤트
    $("#noteContents").html('문의내용 : '+ noteContents);
    $("#noteNo").val(noteNo); // input은 value로 가져와야함// 태그에 value속성이 없으면 html
    $("#notereplymodal").modal("show");
    // 읽음 처리 업데이트
    $.ajax({
        url:"/room/nreadupdate",
        data:{"noteNo": noteNo},
        success: function(data){

        }
    });
}

function notereplywrite(){

   var noteReply = $("#noteReply").val();
   var noteNo = $("#noteNo").val();

   $.ajax({
        url: "/member/notereplywrite",
        data: {"noteReply":noteReply, "noteNo":noteNo},
        success: function(data){
            if(data==1){
                alert("정상적으로 답변하셧습니다.");

                $("#notereplymodal").val(""); // 내용물 초기화
                $("#notereplymodal").modal("hide"); // 모달 종료
            }
        }
   });

}