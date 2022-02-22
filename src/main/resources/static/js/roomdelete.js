function roomdeletemodal(roomNo) {
    $("#roomNo").val(roomNo);
    $("#roomdelete").modal("show");
}

function roomdelete(){
    var roomNo = $("#roomNo").val();

    $.ajax({
        url: "/room/roomdelete",
        method: "Post",
        data: {"roomNo" : roomNo},
        success: function(data){
            if(data == 1) {
                alert("삭제 성공");
                location.reload();
            }else{
                alert("삭제 실패");
            }
        }
    });
}
