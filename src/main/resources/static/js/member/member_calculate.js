

$("#member-calculate-search-btn").click(function(){
    var memberSearch = $("#member-calculate-search").val();
    $.ajax({
        url: "/member/calculateSearch",
        data: { "keyword" : memberSearch },
        method: "GET",
        success: function(data){
            alert(data);
            $(".member-table-section").empty();
            $(".member-table-section").append(data);
        }
    });
});