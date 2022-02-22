package com.ezen.controller;

import com.ezen.service.HistoryService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping(value = "/chart")
public class ChartController {

    @Autowired
    HistoryService historyService;

    @GetMapping("/myChart.json")
    @ResponseBody
    public JSONObject mychart(@RequestParam("chart_year") int chart_year, @RequestParam("chart_month") int chart_month, @RequestParam("chart_week") int chart_week, @RequestParam("memberNo") int memberNo) {
        JSONObject jsonObject = historyService.historyList(chart_year, chart_month, chart_week, memberNo);
        return jsonObject;
    }

}