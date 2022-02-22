
function channelimgdelete(memberNo) {

    $.ajax({
        url: "/member/channelimgdelete",
        method: "POST",
        data: {"memberNo" : memberNo},
        success: function(data) {
            if(data == 1) {
                $("#test").remove();
            }
        }
    });
}