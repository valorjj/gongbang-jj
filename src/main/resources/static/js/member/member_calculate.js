

    $("#member-calculate-search-btn").click(function(){
        var memberSearch = $("#member-calculate-search").val();
        $.ajax({
            url: "/member/calculateSearch",
            data: { "keyword" : memberSearch },
            method: "GET",
            success: function(data){
                $(".member-table-section").empty();
                $(".member-table-section").append(data);
            }
        });
    });

    // 강의 선택 후 roomNo 이용해서 해당 클래스에 예약한 회원 목록 출력해보자
    $("#calculate-table-arrange-2").change(function(){
        var memberSearch = $("#member-calculate-search").val();
        var roomNo = $(this).val();
        $.ajax({
            url: "/member/calculateSearch2",
            data: { "keyword" : memberSearch, "roomNo" : roomNo },
            success: function(data){
                $(".member-table-section").empty();
                $(".member-table-section").append(data);
            }
        });
    });