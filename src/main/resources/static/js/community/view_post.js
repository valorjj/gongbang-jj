// 댓글 등록 이벤트
// 필요한 정보 :
    // 1. postNo
    // 2. memberNo
    // 3. memberId
function createNewReply(postNo){
    // 1. 입력된 댓글과 postNo 를 인수로 넘긴다.
    var contentId = "#newReplyContent" + postNo;
    var content = $(contentId).val();
    var memberId = $("#postLoginMemberId").val();
    $.ajax({
        url: "/community/newPostReply",
        data: {"postNo" : postNo, "content" : content},
        success: function(data){

            $(contentId).val("");
            $(".post-reply-content-wrapper").empty();
            $(".post-reply-area").empty();
            $(".post-reply-area").append(data);


                // 2. 댓글 등록 성공
//                var postHTML = "<div class='post-reply-content' id='post-reply-content-container"+postNo+"'>";
//                postHTML += "<div class='col-3'>";
//                postHTML += "<span> 작성자 </span>";
//                postHTML += "<div>" + memberId + "</div>";
//                postHTML += "</div>";
//                postHTML += "<div class='col-9'>";
//                postHTML += "<div>" + content + "</div>";
//                postHTML += "</div>";
//                postHTML += "</div>";
//                postHTML += "<div class='col-12'>";
//                postHTML += "<button class='add-reply-child' id='child"+postNo+"' data-tab='"+postNo+"'>";
//                postHTML += "답글달기</button>";
//                postHTML += "<button> 수정 </button>";
//                postHTML += "<button> 삭제 </button>";
//                postHTML += "</div>";
//                postHTML += "</div>";
//
//                var $parentWrapperId= "#post-reply-wrapper" + postNo;
//                $($parentWrapperId).insertAfter(postHTML);
//                $(contentId).val("");

        }
    });
}

$(function(){

    $(".child-cancel-btn").on("click", function(e){
        var cancelBtnId = e.target.id;
        var cancelBtnIdSelector = "#child-section" + cancelBtnId;
        $(".reply-child-section").hide();
    });

    $(".add-reply-child").on("click", function(){
        var postNo = $("#postNo").val();
        // 1. 해당 부모 댓글 번호를 변수에 저장합니다.
        var replyNo = $(this).attr("data-tab");
        // 2. 부모 댓글 아래에 대댓글을 작성할 수 있는 영역을 생성합니다.
        var childId = '#child-section' + replyNo;
        var btnId = '#child-btn' + replyNo;
        var contentId = '#child-content' + replyNo;
        $(childId).toggle("fade");
        // 3. 대댓글 등록 버튼 클릭 시
        $(btnId).on("click", function(){
            var content = $(contentId).val();
            $.ajax({
                url: "/community/childReply",
                method: "GET",
                data: {"replyNo" : replyNo, "content" : content, "postNo" : postNo},
                success: function(data){
                    // 1. 자식 댓글 등록 후 부모 댓글을 찾아서 그 아래에 출력한다.
                    if(data == "1"){
                        // #child-container{postReplyNo} 에 등록해야한다.
                        var replyHTML = "<div>";
                        replyHTML += "<div class='reply-child-content'>";
                        replyHTML += "<div class='reply-child'>" + content + "</div>";
                        replyHTML += "</div>";
                        replyHTML += "</div>";

                        var wrapperId = "#reply-child-wrapper" + replyNo;
                        $(wrapperId).append(replyHTML);

                        // 대댓글 입력 후 입력 창 지우기
                        var $inputId = "#child-content" + replyNo;
                        $($inputId).val("");

                        var $childSectionId = "#child-section" + replyNo;

                        $($childSectionId).hide();
                    }
                }
            });
        });
    });
});

// 취소 버튼

