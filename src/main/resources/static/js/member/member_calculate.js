

$("#member-calculate-search-btn").click(function(){
    var memberSearch = $("#member-calculate-search").val();
    alert(memberSearch);
    $.ajax({
        url: "/member/calculateSearch",
        data: { "keyword" : memberSearch },
        method: "GET",
        success: function(data){
            alert(data);
            $("#table-section").empty();
            $("#table-section").append(data);
        }
    });
});