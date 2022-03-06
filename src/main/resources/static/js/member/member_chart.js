
    function memberAmChart(){

        $(".member-chart-content").remove();
        var memberHTML = "<div class='member-chart-content'>";
        memberHTML += "<div id='chartdiv'> </div>";
        memberHTML += "<div>";
        $(".member-chart-wrapper").html(memberHTML);

        $.ajax({

            crossDomain: true,
            url: "/member/calculateChart",
            contentType: "application/json; charset=utf-8",
            method: "GET",
            dataType: "json",
            async: false,
            success: function(data){

                $(".member-chart-wrapper").prepend("<div> 회원들의 공방 등록 현황 (단위 : 연) </div>");
                var dataArray = []; // json 으로 받아온 데이터가 저장될 배열

                var bundleData = $(data.memberHistory).map(function(i, memberHistory) {

                    var historyDate2 = memberHistory.date; // 강의가 개설된 날짜
                    var historyPerson2 = memberHistory.person; // 신청한 인원 수
                    var historyDay2 = historyDate2.split("-")[0] + "-" + (historyDate2.split("-")[1]) + "-" + (historyDate2.split("-")[2] - 1);

                    var date2 = new Date(historyDay2);
                    date2.setHours(0, 0, 0, 0);

                    am5.time.add(date2, "day", 1);
                    var dateTest2 = date2.getTime();
                    var data = { date: dateTest2, value: historyPerson2 };
                    dataArray.push(data);

                });

                am5.ready(function() {

                    // Create root element
                    // https://www.amcharts.com/docs/v5/getting-started/#Root_element
                    var root = am5.Root.new("chartdiv");

                    // Set themes
                    // https://www.amcharts.com/docs/v5/concepts/themes/
                    root.setThemes([
                        am5themes_Animated.new(root)
                    ]);

                    // Create chart
                    // https://www.amcharts.com/docs/v5/charts/xy-chart/
                    var chart = root.container.children.push(am5xy.XYChart.new(root, {
                        panX: true,
                        panY: true,
                        wheelX: "panX",
                        wheelY: "zoomX"
                    }));


                    // Add cursor
                    // https://www.amcharts.com/docs/v5/charts/xy-chart/cursor/
                    var cursor = chart.set("cursor", am5xy.XYCursor.new(root, {
                        behavior: "none"
                    }));
                    cursor.lineY.set("visible", false);


                    // Create axes
                    // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
                    var xAxis = chart.xAxes.push(am5xy.DateAxis.new(root, {
                        maxDeviation: 0.2,
                        baseInterval: {
                            timeUnit: "day",
                            count: 1
                        },
                        renderer: am5xy.AxisRendererX.new(root, {}),
                        tooltip: am5.Tooltip.new(root, {})
                    }));

                    var yAxis = chart.yAxes.push(am5xy.ValueAxis.new(root, {
                        renderer: am5xy.AxisRendererY.new(root, {})
                    }));


                    // Add series
                    // https://www.amcharts.com/docs/v5/charts/xy-chart/series/
                    var series = chart.series.push(am5xy.LineSeries.new(root, {
                        name: "Series",
                        xAxis: xAxis,
                        yAxis: yAxis,
                        valueYField: "value",
                        valueXField: "date",
                        tooltip: am5.Tooltip.new(root, {
                            labelText: "{valueY}"
                        })
                    }));


                    // Add scrollbar
                    // https://www.amcharts.com/docs/v5/charts/xy-chart/scrollbars/
                    chart.set("scrollbarX", am5.Scrollbar.new(root, {
                        orientation: "horizontal"
                    }));


                    // Set data
                    var data = dataArray;
                    series.data.setAll(data);

                    // Make stuff animate on load
                    // https://www.amcharts.com/docs/v5/concepts/animations/
                    series.appear(100);
                    chart.appear(100, 100);

                }); // 연도 선택했을 때 LineChart 종료
            }
        });
    }