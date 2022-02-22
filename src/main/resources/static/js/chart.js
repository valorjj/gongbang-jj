function chartChange(){

    var chart_week = $("#chart_week").val(); // 해당 아이디의 값 가져오기
    var chart_month = $("#chart_month").val(); // 해당 아이디의 값 가져오기
    var chart_year = $("#chart_year").val(); // 해당 아이디의 값 가져오기
     var memberNo = $("#memberNo").val(); // 해당 아이디의 값 가져오기
    alert(memberNo);
    alert(chart_week);
    alert(chart_month);
    alert(chart_year);

    $.ajax({
        url : "/chart/myChart.json" ,
        data : { "chart_year" : chart_year ,"chart_month" : chart_month , "chart_week" : chart_week, "memberNo":memberNo  },
        success : function( jsonObject ){

            var keyval = [ ];   // 모든 키를 저장하는 배열
            var valueval = [ ]; // 모든 값을 저장하는 배열

            var keys = Object.keys( jsonObject );   // Object.keys( json변수명 ) : 모든 키 호출
            for( var i =0 ; i<keys.length; i++ ){   // 키 개수 만큼 반복
               keyval[i] = keys[i];   // i번째 키 저장
               valueval[i] = jsonObject[ keyval[i] ]; // i번째 값 저장
            }

               /* 차트 만들기 chart.js */

            // 주단위
            var context1 = document.getElementById('myChart1').getContext('2d');
            var myChart1 = new Chart(context1, {
                type: 'bar', // 차트의 형태
                data: { // 차트에 들어갈 데이터
                    labels: keyval ,
                    datasets: [
                        { //데이터
                            label: '주 결제량 통계', //차트 제목
                            fill: false, // line 형태일 때, 선 안쪽을 채우는지 안채우는지
                            data:  valueval,
                            backgroundColor: [
                                //색상
                                'rgba(255, 99, 132, 0.2)',
                                'rgba(54, 162, 235, 0.2)',
                                'rgba(255, 206, 86, 0.2)',
                                'rgba(75, 192, 192, 0.2)',
                                'rgba(255, 159, 64, 0.2)'
                            ],
                            borderColor: [
                                //경계선 색상
                                'rgba(255, 99, 132, 1)',
                                'rgba(54, 162, 235, 1)',
                                'rgba(255, 206, 86, 1)',
                                'rgba(75, 192, 192, 1)',
                                'rgba(255, 159, 64, 1)'
                            ],
                            borderWidth: 1 //경계선 굵기
                        }
                    ]
                },
                options: {
                    scales: {
                        yAxes: [
                            {
                                ticks: {
                                    beginAtZero: true
                                }
                            }
                        ]
                    }
                }
            });

               }
        });
}


