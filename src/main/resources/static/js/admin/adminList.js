$("ul.tabs li").click(function() {

    var tabId = $(this).attr("data-tab");

    $("ul.tabs li").removeClass("current");
    $(".tab-content").removeClass("current");

    $(this).addClass("current");
    $("#" + tabId).addClass("current");

});

// 버튼 클릭 이벤트 - 카테고리 선택
function adminCategorySelectBtn() {

    $(".admin-chart-content").remove();
    var adminHTML = "<div class='admin-chart-content'>";
    adminHTML += "<div id='chartdiv'> </div>";
    adminHTML += "<div>";
    $(".admin-chart-wrapper").html(adminHTML);

    $.ajax({
        crossDomain: true,
        url: "/admin/roomJSON",
        contentType: "application/json; charset=utf-8",
        method: "GET",
        dataType: "json",
        async: false,
        success: function(data) {

            $(".admin-chart-wrapper").prepend("<div> <span>카테고리 별</span> 개설 현황 (단위 : 카테고리) </div>");

            // 카테고리 11개
            var category1 = 0;
            var category2 = 0;
            var category3 = 0;
            var category4 = 0;
            var category5 = 0;
            var category6 = 0;
            var category7 = 0;
            var category8 = 0;
            var category9 = 0;
            var category10 = 0;
            var category11 = 0;

            var bundleData = $(data.history).map(function(i, history) {
                // 1. 공방 카테고리
                var category = history.category;
                switch (category) {
                    case "핸드메이드":
                        category1++;
                        break;
                    case "쿠킹":
                        category2++;
                        break;
                    case "플라워":
                        category3++;
                        break;
                    case "드로잉":
                        category4++;
                        break;
                    case "음악":
                        category5++;
                        break;
                    case "필라테스":
                        category6++;
                        break;
                    case "스포츠":
                        category7++;
                        break;
                    case "뷰티":
                        category8++;
                        break;
                    case "애완동물":
                        category9++;
                        break;
                    case "체험":
                        category10++;
                        break;
                    case "자기계발":
                        category11++;
                        break;
                }
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
                var cursor = chart.set("cursor", am5xy.XYCursor.new(root, {}));
                cursor.lineY.set("visible", false);


                // Create axes
                // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
                var xRenderer = am5xy.AxisRendererX.new(root, { minGridDistance: 30 });
                xRenderer.labels.template.setAll({
                    rotation: -90,
                    centerY: am5.p50,
                    centerX: am5.p100,
                    paddingRight: 15
                });

                var xAxis = chart.xAxes.push(am5xy.CategoryAxis.new(root, {
                    maxDeviation: 0.3,
                    categoryField: "category",
                    renderer: xRenderer,
                    tooltip: am5.Tooltip.new(root, {})
                }));

                var yAxis = chart.yAxes.push(am5xy.ValueAxis.new(root, {
                    maxDeviation: 0.3,
                    renderer: am5xy.AxisRendererY.new(root, {})
                }));


                // Create series
                // https://www.amcharts.com/docs/v5/charts/xy-chart/series/
                var series = chart.series.push(am5xy.ColumnSeries.new(root, {
                    name: "Series 1",
                    xAxis: xAxis,
                    yAxis: yAxis,
                    valueYField: "value",
                    sequencedInterpolation: true,
                    categoryXField: "category",
                    tooltip: am5.Tooltip.new(root, {
                        labelText: "{valueY}"
                    })
                }));

                series.columns.template.setAll({ cornerRadiusTL: 5, cornerRadiusTR: 5 });
                series.columns.template.adapters.add("fill", (fill, target) => {
                    return chart.get("colors").getIndex(series.columns.indexOf(target));
                });

                series.columns.template.adapters.add("stroke", (stroke, target) => {
                    return chart.get("colors").getIndex(series.columns.indexOf(target));
                });

                // Set data
                var data = [{
                    category: "핸드메이드",
                    value: category1
                }, {
                    category: "쿠킹",
                    value: category2
                }, {
                    category: "플라워",
                    value: category3
                }, {
                    category: "드로잉",
                    value: category4
                }, {
                    category: "음악",
                    value: category5
                }, {
                    category: "필라테스",
                    value: category6
                }, {
                    category: "스포츠",
                    value: category7
                }, {
                    category: "뷰티",
                    value: category8
                }, {
                    category: "애완동물",
                    value: category9
                }, {
                    category: "체험",
                    value: category10
                }, {
                    category: "자기계발",
                    value: category11
                }];

                xAxis.data.setAll(data);
                series.data.setAll(data);

                // Make stuff animate on load
                // https://www.amcharts.com/docs/v5/concepts/animations/
                series.appear(1000);
                chart.appear(1000, 100);

            }); // end am5.ready()
        }
    });
}

// 버튼 클릭 이벤트 - 지역 선택
function adminLocalSelectBtn() {

    $(".admin-chart-content").remove();
    var adminHTML = "<div class='admin-chart-content'>";
    adminHTML += "<div id='chartdiv'> </div>";
    adminHTML += "<div>";
    $(".admin-chart-wrapper").html(adminHTML);
    $.ajax({
        crossDomain: true,
        url: "/admin/roomJSON",
        contentType: "application/json; charset=utf-8",
        method: "GET",
        dataType: "json",
        async: false,
        success: function(data) {

            $(".admin-chart-wrapper").prepend("<div> <span>지역 별</span>  개설 현황 (단위 : 지역) </div>");

            // 지역 서울, 경기, 인천, 부산, 대구
            var local1 = 0;
            var local2 = 0;
            var local3 = 0;
            var local4 = 0;
            var local5 = 0;

            var bundleData = $(data.history).map(function(i, history) {
                // 1. 공방 지역
                var local = history.local;
                switch (local) {
                    case "서울":
                        local1++;
                        break;
                    case "경기":
                        local2++;
                        break;
                    case "인천":
                        local3++;
                        break;
                    case "부산":
                        local4++;
                        break;
                    case "대구":
                        local5++;
                        break;
                }
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
                var cursor = chart.set("cursor", am5xy.XYCursor.new(root, {}));
                cursor.lineY.set("visible", false);

                // Create axes
                // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
                var xRenderer = am5xy.AxisRendererX.new(root, { minGridDistance: 30 });
                xRenderer.labels.template.setAll({
                    rotation: -90,
                    centerY: am5.p50,
                    centerX: am5.p100,
                    paddingRight: 15
                });

                var xAxis = chart.xAxes.push(am5xy.CategoryAxis.new(root, {
                    maxDeviation: 0.3,
                    categoryField: "local",
                    renderer: xRenderer,
                    tooltip: am5.Tooltip.new(root, {})
                }));

                var yAxis = chart.yAxes.push(am5xy.ValueAxis.new(root, {
                    maxDeviation: 0.3,
                    renderer: am5xy.AxisRendererY.new(root, {})
                }));


                // Create series
                // https://www.amcharts.com/docs/v5/charts/xy-chart/series/
                var series = chart.series.push(am5xy.ColumnSeries.new(root, {
                    name: "Series 1",
                    xAxis: xAxis,
                    yAxis: yAxis,
                    valueYField: "value",
                    sequencedInterpolation: true,
                    categoryXField: "local",
                    tooltip: am5.Tooltip.new(root, {
                        labelText: "{valueY}"
                    })
                }));

                series.columns.template.setAll({ cornerRadiusTL: 5, cornerRadiusTR: 5 });
                series.columns.template.adapters.add("fill", (fill, target) => {
                    return chart.get("colors").getIndex(series.columns.indexOf(target));
                });

                series.columns.template.adapters.add("stroke", (stroke, target) => {
                    return chart.get("colors").getIndex(series.columns.indexOf(target));
                });

                // Set data
                var data = [{
                    local: "서울",
                    value: local1
                }, {
                    local: "경기",
                    value: local2
                }, {
                    local: "인천",
                    value: local3
                }, {
                    local: "부산",
                    value: local4
                }, {
                    local: "대구",
                    value: local5
                }];

                xAxis.data.setAll(data);
                series.data.setAll(data);

                // Make stuff animate on load
                // https://www.amcharts.com/docs/v5/concepts/animations/
                series.appear(1000);
                chart.appear(1000, 100);
            }); // end am5.ready()
        }
    });
}

// 버튼 클릭 이벤트 - 날짜 선택
function adminSelectBtn() {

    $(".admin-chart-content").remove();
    var adminHTML = "<div class='admin-chart-content'>";
    adminHTML += "<div id='chartdiv'> </div>";
    adminHTML += "<div>";
    $(".admin-chart-wrapper").html(adminHTML);


    var year = $("#admin-chart-year").val();
    var month = $("#admin-chart-month").val();
    var day = $("#admin-chart-day").val();
    year = isEmpty(year, -1);
    month = isEmpty(month, -1);
    day = isEmpty(day, -1);

    // 어떤 값이 선택되었는지에 따라서 각각 다른 차트를 출력해야한다.

    // 1. 아무것도 선택하지 않은 경우
    // 1. 전체 데이터를 올해 기준으로 '달 별' 데이터를 출력한다.
    // 2. 출력되는 데이터는 개설된 강좌 현황입니다.
    if (year == "-1" && month == "-1" && day == "-1") {
        $.ajax({
            crossDomain: true,
            url: "/admin/roomJSON",
            contentType: "application/json; charset=utf-8",
            method: "GET",
            dataType: "json",
            async: false,
            success: function(data) {

                $(".admin-chart-wrapper").prepend("<div> <span>공방 개설</span> 현황 (단위 : 달) </div>");
                console.log(JSON.stringify(data));

                // 달별 데이터
                var jan = 0;
                var feb = 0;
                var mar = 0;
                var apr = 0;
                var may = 0;
                var jun = 0;
                var jul = 0;
                var aug = 0;
                var sep = 0;
                var oct = 0;
                var nov = 0;
                var dec = 0;

                var bundleData = $(data.history).map(function(i, history) {
                    // 1. 강의 개설 날짜
                    var date = history.date;
                    console.log(date);

                    // 2. 강의 개설 '월'
                    // 1. 각각 월에 값을 더해서 차트로 출력하기 위함
                    var month = date.split("-")[1];
                    console.log(month);

                    switch (month) {
                        case "1":
                            jan = jan + 1;
                            break;
                        case "2":
                            feb = feb + 1;
                            break;
                        case "3":
                            mar = mar + 1;
                            break;
                        case "4":
                            apr = apr + 1;
                            break;
                        case "5":
                            may = may + 1;
                            break;
                        case "6":
                            jun = jun + 1;
                            break;
                        case "7":
                            jul = jul + 1;
                            break;
                        case "8":
                            aug = aug + 1;
                            break;
                        case "9":
                            sep = sep + 1;
                            break;
                        case "10":
                            oct = oct + 1;
                            break;
                        case "11":
                            nov = nov + 1;
                            break;
                        case "12":
                            dec = dec + 1;
                            break;
                    }
                    var category = history.category;
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
                    var cursor = chart.set("cursor", am5xy.XYCursor.new(root, {}));
                    cursor.lineY.set("visible", false);


                    // Create axes
                    // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
                    var xRenderer = am5xy.AxisRendererX.new(root, { minGridDistance: 30 });
                    xRenderer.labels.template.setAll({
                        rotation: -90,
                        centerY: am5.p50,
                        centerX: am5.p100,
                        paddingRight: 15
                    });

                    var xAxis = chart.xAxes.push(am5xy.CategoryAxis.new(root, {
                        maxDeviation: 0.3,
                        categoryField: "month",
                        renderer: xRenderer,
                        tooltip: am5.Tooltip.new(root, {})
                    }));

                    var yAxis = chart.yAxes.push(am5xy.ValueAxis.new(root, {
                        maxDeviation: 0.3,
                        renderer: am5xy.AxisRendererY.new(root, {})
                    }));


                    // Create series
                    // https://www.amcharts.com/docs/v5/charts/xy-chart/series/
                    var series = chart.series.push(am5xy.ColumnSeries.new(root, {
                        name: "Series 1",
                        xAxis: xAxis,
                        yAxis: yAxis,
                        valueYField: "value",
                        sequencedInterpolation: true,
                        categoryXField: "month",
                        tooltip: am5.Tooltip.new(root, {
                            labelText: "{valueY}"
                        })
                    }));

                    series.columns.template.setAll({ cornerRadiusTL: 5, cornerRadiusTR: 5 });
                    series.columns.template.adapters.add("fill", (fill, target) => {
                        return chart.get("colors").getIndex(series.columns.indexOf(target));
                    });

                    series.columns.template.adapters.add("stroke", (stroke, target) => {
                        return chart.get("colors").getIndex(series.columns.indexOf(target));
                    });

                    // Set data
                    var data = [{
                        month: "JAN",
                        value: jan
                    }, {
                        month: "FEB",
                        value: feb
                    }, {
                        month: "MAR",
                        value: mar
                    }, {
                        month: "APR",
                        value: apr
                    }, {
                        month: "MAY",
                        value: may
                    }, {
                        month: "JUN",
                        value: jun
                    }, {
                        month: "JUL",
                        value: jul
                    }, {
                        month: "AUG",
                        value: aug
                    }, {
                        month: "SEP",
                        value: sep
                    }, {
                        month: "OCT",
                        value: oct
                    }, {
                        month: "NOV",
                        value: nov
                    }, {
                        month: "DEC",
                        value: dec
                    }];

                    xAxis.data.setAll(data);
                    series.data.setAll(data);

                    // Make stuff animate on load
                    // https://www.amcharts.com/docs/v5/concepts/animations/
                    series.appear(1000);
                    chart.appear(1000, 100);

                }); // end am5.ready()
            }
        });
    } else {
        // 2. 연도 선택
        if (year != "-1" && month == "-1" && day == "-1") {
            $.ajax({
                crossDomain: true,
                url: "/admin/roomJSON",
                contentType: "application/json; charset=utf-8",
                method: "GET",
                dataType: "json",
                async: false,
                success: function(data) {

                    $(".admin-chart-wrapper").prepend("<div> 회원들의 공방 등록 현황 (단위 : 연) </div>");
                    var dataArray = []; // json 으로 받아온 데이터가 저장될 배열

                    // 반복문을 돌면서, json 에 저장되어있는 데이터를 dateArray 에 넣어줘야합니다.
                    // 같은 날짜라면 기존의 값에 더해줘야합니다. [현재 오류]

                    var bundleData = $(data.history).map(function(i, history) {
                        // 1. 일별 데이터를 추가합니다.
                        // 2. history.date 에 해당하는 값들 중 day 만 가져옵니다.

                        var historyDate = history.date; // 강의가 개설된 날짜
                        console.log(historyDate);
                        var historyPerson = history.person; // 신청한 인원 수
                        var historyDay = historyDate.split("-")[0] + "-" + (historyDate.split("-")[1]) + "-" + (historyDate.split("-")[2] - 1);

                        var date = new Date(historyDay);
                        date.setHours(0, 0, 0, 0);

                        am5.time.add(date, "day", 1);
                        var dateTest = date.getTime();
                        console.log("dateTest >>>> " + dateTest);
                        // js array 에 데이터 추가하는 방법
                        // 1. Array.push() : 배열의 끝에 요소 추가
                        // 2. Array.unshift() : 배열의 앞에 요소 추가
                        var historyDay = historyDate.split("-")[0] + historyDate.split("-")[1] + historyDate.split("-")[2];
                        // key : value
                        var data = { date: dateTest, value: historyPerson };
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
                        console.log(data);

                        series.data.setAll(data);

                        // Make stuff animate on load
                        // https://www.amcharts.com/docs/v5/concepts/animations/
                        series.appear(100);
                        chart.appear(100, 100);

                    }); // 연도 선택했을 때 LineChart 종료
                }

            });
        }
        if (year != "-1" && month != "-1" && day == "-1") {
            $.ajax({
                crossDomain: true,
                url: "/admin/roomJSON",
                contentType: "application/json; charset=utf-8",
                method: "GET",
                dataType: "json",
                async: false,
                success: function(data) {

                    console.log(JSON.stringify(data));

                    // 달별 데이터
                    var jan = 0;
                    var feb = 0;
                    var mar = 0;
                    var apr = 0;
                    var may = 0;
                    var jun = 0;
                    var jul = 0;
                    var aug = 0;
                    var sep = 0;
                    var oct = 0;
                    var nov = 0;
                    var dec = 0;

                    var bundleData = $(data.history).map(function(i, history) {
                        // 1. 강의 개설 날짜
                        var date = history.date;
                        console.log(date);

                        // 2. 강의 개설 '월'
                        // 1. 각각 월에 값을 더해서 차트로 출력하기 위함
                        var month = date.split("-")[1];
                        console.log(month);

                        switch (month) {
                            case "1":
                                jan = jan + 1;
                                break;
                            case "2":
                                feb = feb + 1;
                                break;
                            case "3":
                                mar = mar + 1;
                                break;
                            case "4":
                                apr = apr + 1;
                                break;
                            case "5":
                                may = may + 1;
                                break;
                            case "6":
                                jun = jun + 1;
                                break;
                            case "7":
                                jul = jul + 1;
                                break;
                            case "8":
                                aug = aug + 1;
                                break;
                            case "9":
                                sep = sep + 1;
                                break;
                            case "10":
                                oct = oct + 1;
                                break;
                            case "11":
                                nov = nov + 1;
                                break;
                            case "12":
                                dec = dec + 1;
                                break;
                        }
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
                        var cursor = chart.set("cursor", am5xy.XYCursor.new(root, {}));
                        cursor.lineY.set("visible", false);


                        // Create axes
                        // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
                        var xRenderer = am5xy.AxisRendererX.new(root, { minGridDistance: 30 });
                        xRenderer.labels.template.setAll({
                            rotation: -90,
                            centerY: am5.p50,
                            centerX: am5.p100,
                            paddingRight: 15
                        });

                        var xAxis = chart.xAxes.push(am5xy.CategoryAxis.new(root, {
                            maxDeviation: 0.3,
                            categoryField: "month",
                            renderer: xRenderer,
                            tooltip: am5.Tooltip.new(root, {})
                        }));

                        var yAxis = chart.yAxes.push(am5xy.ValueAxis.new(root, {
                            maxDeviation: 0.3,
                            renderer: am5xy.AxisRendererY.new(root, {})
                        }));


                        // Create series
                        // https://www.amcharts.com/docs/v5/charts/xy-chart/series/
                        var series = chart.series.push(am5xy.ColumnSeries.new(root, {
                            name: "Series 1",
                            xAxis: xAxis,
                            yAxis: yAxis,
                            valueYField: "value",
                            sequencedInterpolation: true,
                            categoryXField: "month",
                            tooltip: am5.Tooltip.new(root, {
                                labelText: "{valueY}"
                            })
                        }));

                        series.columns.template.setAll({ cornerRadiusTL: 5, cornerRadiusTR: 5 });
                        series.columns.template.adapters.add("fill", (fill, target) => {
                            return chart.get("colors").getIndex(series.columns.indexOf(target));
                        });

                        series.columns.template.adapters.add("stroke", (stroke, target) => {
                            return chart.get("colors").getIndex(series.columns.indexOf(target));
                        });


                        // Set data
                        var data = [{
                            month: "JAN",
                            value: jan
                        }, {
                            month: "FEB",
                            value: feb
                        }, {
                            month: "MAR",
                            value: mar
                        }, {
                            month: "APR",
                            value: apr
                        }, {
                            month: "MAY",
                            value: may
                        }, {
                            month: "JUN",
                            value: jun
                        }, {
                            month: "JUL",
                            value: jul
                        }, {
                            month: "AUG",
                            value: aug
                        }, {
                            month: "SEP",
                            value: sep
                        }, {
                            month: "OCT",
                            value: oct
                        }, {
                            month: "NOV",
                            value: nov
                        }, {
                            month: "DEC",
                            value: dec
                        }];

                        xAxis.data.setAll(data);
                        series.data.setAll(data);

                        // Make stuff animate on load
                        // https://www.amcharts.com/docs/v5/concepts/animations/
                        series.appear(1000);
                        chart.appear(1000, 100);

                    }); // end am5.ready()
                }
            });
        }
        if (year != "-1" && month != "-1" && day != "-1") {

            $(".admin-chart-wrapper").prepend("<div> 해당 날짜에 지역별 등록한 인원 수 (단위 : 1명) </div>");
            var adminSelectYear = $("#admin-chart-year").val();
            var adminSelectMonth = $("#admin-chart-month").val();
            var adminSelectDay = $("#admin-chart-day").val();

            var adminSelectDate = adminSelectYear + "-" + adminSelectMonth + "-" + adminSelectDay;

            $.ajax({
                crossDomain: true,
                url: "/admin/roomJSONDaySelect",
                contentType: "application/json; charset=utf-8",
                data: { "select-date": adminSelectDate },
                method: "GET",
                dataType: "json",
                async: false,
                success: function(data) {
                    // 시간이 없어 '지역' 별 신청인원만 출력합니다.

                    var category1 = 0;
                    var category2 = 0;
                    var category3 = 0;
                    var category4 = 0;
                    var category5 = 0;
                    var category6 = 0;
                    var category7 = 0;
                    var category8 = 0;
                    var category9 = 0;
                    var category10 = 0;
                    var category11 = 0;

                    var totalsum3 = 0; // 하루 결제된 액수

                    var bundleData2 = $(data.history).map(function(i, history) {
                        var category = history.category;
                        switch (category) {
                            case "핸드메이드":
                                category1 = category1 + history.person;
                                totalsum3 = totalsum3 + history.price;
                                break;
                            case "쿠킹":
                                category2 = category2 + history.person;
                                totalsum3 = totalsum3 + history.price;
                                break;
                            case "플라워":
                                category3 = category3 + history.person;
                                totalsum3 = totalsum3 + history.price;
                                break;
                            case "드로잉":
                                category4 = category4 + history.person;
                                totalsum3 = totalsum3 + history.price;
                                break;
                            case "음악":
                                category5 = category5 + history.person;
                                totalsum3 = totalsum3 + history.price;
                                break;
                            case "필라테스":
                                category6 = category6 + history.person;
                                totalsum3 = totalsum3 + history.price;
                                break;
                            case "스포츠":
                                category7 = category7 + history.person;
                                totalsum3 = totalsum3 + history.price;
                                break;
                            case "뷰티":
                                category8 = category8 + history.person;
                                totalsum3 = totalsum3 + history.price;
                                break;
                            case "애완동물":
                                category9 = category9 + history.person;
                                totalsum3 = totalsum3 + history.price;
                                break;
                            case "체험":
                                category10 = category10 + history.person;
                                totalsum3 = totalsum3 + history.price;
                                break;
                            case "자기계발":
                                category11 = category11 + history.person;
                                totalsum3 = totalsum3 + history.price;
                                break;

                        }
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

                        var data = [{
                                name: "핸드메이드",
                                steps: category1,
                                pictureSettings: {
                                    src: "https://cdn-icons-png.flaticon.com/512/5110/5110736.png"
                                }
                            },
                            {
                                name: "쿠킹",
                                steps: category2,
                                pictureSettings: {
                                    src: "https://www.creativefabrica.com/wp-content/uploads/2020/06/03/Cooking-Vector-Illustration-Icon-Graphics-4267218-1-1-580x435.jpg"
                                }
                            },
                            {
                                name: "플라워",
                                steps: category3,
                                pictureSettings: {
                                    src: "https://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/flower-icon.png"
                                }
                            },
                            {
                                name: "드로잉",
                                steps: category4,
                                pictureSettings: {
                                    src: "https://icons-for-free.com/iconfiles/png/512/brush+design+draw+drawing+icon-1320168140159902387.png"
                                }
                            },
                            {
                                name: "음악",
                                steps: category5,
                                pictureSettings: {
                                    src: "https://icons-for-free.com/iconfiles/png/512/music-131964753036631366.png"
                                }
                            },
                            {
                                name: "필라테스",
                                steps: category6,
                                pictureSettings: {
                                    src: "https://cdn-icons-png.flaticon.com/512/2320/2320765.png"
                                }
                            },
                            {
                                name: "스포츠",
                                steps: category7,
                                pictureSettings: {
                                    src: "https://image.emojipng.com/679/10488679.jpg"
                                }
                            },
                            {
                                name: "뷰티",
                                steps: category8,
                                pictureSettings: {
                                    src: "https://www.clipartmax.com/png/middle/196-1962163_cosmetics-beauty-icon-maquillaje-vector-png.png"
                                }
                            },

                            {
                                name: "애완동물",
                                steps: category9,
                                pictureSettings: {
                                    src: "https://cdn-icons-png.flaticon.com/512/1596/1596810.png"
                                }
                            },

                            {
                                name: "체험",
                                steps: category10,
                                pictureSettings: {
                                    src: "https://cdn.imgbin.com/24/11/3/hiking-icon-backpack-icon-adventure-icon-5QV3DDYU.jpg"
                                }
                            },

                            {
                                name: "자기계발",
                                steps: category11,
                                pictureSettings: {
                                    src: "https://cdn-icons-png.flaticon.com/512/1478/1478929.png"
                                }
                            }
                        ];

                        // Create chart
                        // https://www.amcharts.com/docs/v5/charts/xy-chart/
                        var chart = root.container.children.push(
                            am5xy.XYChart.new(root, {
                                panX: false,
                                panY: false,
                                wheelX: "none",
                                wheelY: "none",
                                paddingLeft: 50,
                                paddingRight: 40
                            })
                        );

                        // Create axes
                        // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/

                        var yRenderer = am5xy.AxisRendererY.new(root, {});
                        yRenderer.grid.template.set("visible", false);

                        var yAxis = chart.yAxes.push(
                            am5xy.CategoryAxis.new(root, {
                                categoryField: "name",
                                renderer: yRenderer,
                                paddingRight: 40
                            })
                        );

                        var xRenderer = am5xy.AxisRendererX.new(root, {});
                        xRenderer.grid.template.set("strokeDasharray", [3]);

                        var xAxis = chart.xAxes.push(
                            am5xy.ValueAxis.new(root, {
                                min: 0,
                                renderer: xRenderer
                            })
                        );

                        // Add series
                        // https://www.amcharts.com/docs/v5/charts/xy-chart/series/
                        var series = chart.series.push(
                            am5xy.ColumnSeries.new(root, {
                                name: "Income",
                                xAxis: xAxis,
                                yAxis: yAxis,
                                valueXField: "steps",
                                categoryYField: "name",
                                sequencedInterpolation: true,
                                calculateAggregates: true,
                                maskBullets: false,
                                tooltip: am5.Tooltip.new(root, {
                                    dy: -30,
                                    pointerOrientation: "vertical",
                                    labelText: "{valueX}"
                                })
                            })
                        );

                        series.columns.template.setAll({
                            strokeOpacity: 0,
                            cornerRadiusBR: 10,
                            cornerRadiusTR: 10,
                            cornerRadiusBL: 10,
                            cornerRadiusTL: 10,
                            maxHeight: 50,
                            fillOpacity: 0.8
                        });

                        var currentlyHovered;

                        series.columns.template.events.on("pointerover", function(e) {
                            handleHover(e.target.dataItem);
                        });

                        series.columns.template.events.on("pointerout", function(e) {
                            handleOut();
                        });

                        function handleHover(dataItem) {
                            if (dataItem && currentlyHovered != dataItem) {
                                handleOut();
                                currentlyHovered = dataItem;
                                var bullet = dataItem.bullets[0];
                                bullet.animate({
                                    key: "locationX",
                                    to: 1,
                                    duration: 600,
                                    easing: am5.ease.out(am5.ease.cubic)
                                });
                            }
                        }

                        function handleOut() {
                            if (currentlyHovered) {
                                var bullet = currentlyHovered.bullets[0];
                                bullet.animate({
                                    key: "locationX",
                                    to: 0,
                                    duration: 600,
                                    easing: am5.ease.out(am5.ease.cubic)
                                });
                            }
                        }


                        var circleTemplate = am5.Template.new({});

                        series.bullets.push(function(root, series, dataItem) {
                            var bulletContainer = am5.Container.new(root, {});
                            var circle = bulletContainer.children.push(
                                am5.Circle.new(
                                    root, {
                                        radius: 34
                                    },
                                    circleTemplate
                                )
                            );

                            var maskCircle = bulletContainer.children.push(
                                am5.Circle.new(root, { radius: 27 })
                            );

                            // only containers can be masked, so we add image to another container
                            var imageContainer = bulletContainer.children.push(
                                am5.Container.new(root, {
                                    mask: maskCircle
                                })
                            );

                            // not working
                            var image = imageContainer.children.push(
                                am5.Picture.new(root, {
                                    templateField: "pictureSettings",
                                    centerX: am5.p50,
                                    centerY: am5.p50,
                                    width: 60,
                                    height: 60
                                })
                            );

                            return am5.Bullet.new(root, {
                                locationX: 0,
                                sprite: bulletContainer
                            });
                        });

                        // heatrule
                        series.set("heatRules", [{
                                dataField: "valueX",
                                min: am5.color(0xe5dc36),
                                max: am5.color(0x5faa46),
                                target: series.columns.template,
                                key: "fill"
                            },
                            {
                                dataField: "valueX",
                                min: am5.color(0xe5dc36),
                                max: am5.color(0x5faa46),
                                target: circleTemplate,
                                key: "fill"
                            }
                        ]);

                        series.data.setAll(data);
                        yAxis.data.setAll(data);

                        var cursor = chart.set("cursor", am5xy.XYCursor.new(root, {}));
                        cursor.lineX.set("visible", false);
                        cursor.lineY.set("visible", false);

                        cursor.events.on("cursormoved", function() {
                            var dataItem = series.get("tooltip").dataItem;
                            if (dataItem) {
                                handleHover(dataItem)
                            } else {
                                handleOut();
                            }
                        })

                        // Make stuff animate on load
                        // https://www.amcharts.com/docs/v5/concepts/animations/
                        series.appear();
                        chart.appear(1000, 100);

                    }); // end am5.ready()
                }

            });
        }
    }
}

function isEmpty(str, defaultStr) {
    if (typeof str == "undefined" || str == null || str == "") {
        str = defaultStr;
    }
    return str;
}