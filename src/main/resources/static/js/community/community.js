
$(function(){

    // 카테고리 생성 이벤트
    $("#createNewCategoryBtn").on("click", function(e) {
        // 입력된 카테고리 이름
        var category = $("#newCategory").val();
        $.ajax({
            // 컨트롤러와 연결
            url: "/category/newCategoryController",
            // 카테고리 이름을 전달
            data: {"category" : category},
            // get 방식으로 전달
            method: "GET",
            success: function(data){
                if(data==1){
                    // 등록 성공 시 마지막 뒷 부분에 div 추가
                    // $(".sidebar-container").last().append("<div class='sidebar-category' id='category'"++>" + category + "</div>");
                    location.reload();
                } else {
                    alert("오류발생. 관리자에게 문의하세요");
                    location.href = "/community/list";
                }
            }
        });
    });

    // 새로운 게시판 생성 이벤트
    $("#createNewBoardBtn").on("click", function(){
        var boardName = $("#newBoard").val(); // 입력받은 게시판 이름
        var categoryName = $("#category-select").val(); // 선택한 카테고리
        $.ajax({
            url: "/board/newBoardController",
            data: {"boardName" : boardName, "categoryName" : categoryName},
            method: "GET",
            success:function(data){
                // 1. 특정 지역 안에 묶여야한다.
                if(data == 1){
                    location.reload();
                }
            }
        });
    });

});


// 사이드 바에서 게시판 클릭 시 탭 이동 이벤트
// boardNo 에 해당하는 게시판 리스트 화면을 .content-area 에 추가한다.
function showBoardContent(boardNo){
    // alert(boardNo); // js 작동 확인
    // 1. boardNo 를 ajax 통해 컨트롤러로 넘긴다.
    // 2. 컨트롤러에서 db 를 조회해서, html 에 model 로 뿌린다.
    // 3. model 값을 받은 html 을 원하는 div 태그에 append 시킨다.
    $.ajax({
        url: "/community/postListController",
        data: {"boardNo" : boardNo},
        method: "GET",
        success: function(data){
            // 기존에 있던 내용을 지운다.
            $(".content-area").empty();
            $(".content-area").append(data);
        }
    });
}

function createPost(boardNo){

    $.ajax({
        url: "/community/createPost",
        data: {"boardNo" : boardNo},
        method: "GET",
        success: function(data){
            // 기존에 있던 내용을 지운다.
            $(".content-area").empty();
            $(".content-area").append(data);
        }
    });

}

// ## 게시글 작성 ##
// @Author: 김정진
// @Date : 2022-02-19
function createNewPost(boardNo){
    // 1. 각 게시물은 boardNo 에 종속된다.
    $.ajax({
        url: "/community/createPost",
        data: {"boardNo" : boardNo},
        success: function(data){
            $(".content-area").empty();
            $(".content-area").append(data);
        }
    });
}

// 게시글 작성 시 '취소' 누르고 뒤로가기
function goToPostList(boardNo){
    $.ajax({
        url: "/community/postListController",
        data: {"boardNo" : boardNo},
        method: "GET",
        success: function(data){
            $(".content-area").empty();
            $(".content-area").append(data);
        }
    });
}
// 작성된 게시물 상세 페이지 출력하기
function goToPostView(postNo, boardNo){
    $.ajax({
        url: "/community/viewPost",
        data: {"postNo" : postNo, "boardNo" : boardNo},
        method: "GET",
        success: function(data){
            $(".content-area").empty();
            $(".content-area").append(data);
        }
    })

}