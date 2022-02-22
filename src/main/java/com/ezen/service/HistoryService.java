package com.ezen.service;

import com.ezen.domain.dto.Chart;
import com.ezen.domain.dto.MemberDto;
import com.ezen.domain.entity.HistoryEntity;
import com.ezen.domain.entity.MemberEntity;
import com.ezen.domain.entity.RoomEntity;
import com.ezen.domain.entity.repository.ChartRepository;
import com.ezen.domain.entity.repository.MemberRepository;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
public class HistoryService {


    @Autowired
    private ChartRepository chartRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    public JSONObject historyList(int chart_year, int chart_month, int chart_week, int memberNo) {

        HttpSession session = request.getSession();
        MemberDto memberDto = (MemberDto) session.getAttribute("logindto");
        MemberEntity memberEntity = memberService.getMemberEntity(memberDto.getMemberNo());

        List<RoomEntity> roomlist = memberRepository.findById(memberNo).get().getRoomEntities();// 로그인 한 사람의 룸 리스트

        JSONObject jsonObject = new JSONObject();


        List<Chart> result = new ArrayList<>(); // 결과리스트

        for (RoomEntity room : roomlist) {
            List<HistoryEntity> historyEntitys = room.getHistoryEntities();
            // 연도
            for (HistoryEntity history : historyEntitys) {
                if (history.getTimeTableEntity().getRoomDate().split("-")[0].equals(String.valueOf(chart_year))) { // 동일한 연도
                    Chart chart = new Chart(String.valueOf(chart_year), history.getHistoryPoint());
                    // 만약에 동일한 날짜일 경우 중복체크
                    int ch = 0;
                    for (int i = 0; i < result.size(); i++) {
                        if (result.get(i).getDate().equals(chart.getDate())) {
                            ch++;
                            result.get(i).setTotalpay(result.get(i).getTotalpay() + chart.getTotalpay());
                        }
                    }
                    if (ch == 0) {
                        result.add(chart);
                    }

                } else { // 동일하지 않는 연도

                }
            }
        }
        // 월
        for (RoomEntity room : roomlist) {
            List<HistoryEntity> historyEntitys = room.getHistoryEntities();
            // 연도
            for (HistoryEntity history : historyEntitys) {
                if (history.getTimeTableEntity().getRoomDate().split("-")[0].equals(String.valueOf(chart_year)) && history.getTimeTableEntity().getRoomDate().split("-")[1].equals(String.valueOf(chart_month))) { // 동일한 연도
                    Chart chart = new Chart(String.valueOf(chart_month), history.getHistoryPoint());
                    // 만약에 동일한 날짜일 경우 중복체크
                    int ch = 0;
                    for (int i = 0; i < result.size(); i++) {
                        if (result.get(i).getDate().equals(chart.getDate())) {
                            ch++;
                            result.get(i).setTotalpay(result.get(i).getTotalpay() + chart.getTotalpay());
                        }
                    }
                    if (ch == 0) {
                        result.add(chart);
                    }

                } else { // 동일하지 않는 연도

                }
            }
        }
        // 주
        for (RoomEntity room : roomlist) {
            List<HistoryEntity> historyEntitys = room.getHistoryEntities();
            // 연도
            for (HistoryEntity history : historyEntitys) {
                if (history.getTimeTableEntity().getRoomDate().split("-")[0].equals(String.valueOf(chart_year)) && history.getTimeTableEntity().getRoomDate().split("-")[1].equals(String.valueOf(chart_month))) { // 동일한 연도
                    Chart chart = new Chart(String.valueOf(chart_month), history.getHistoryPoint());
                    // 만약에 동일한 날짜일 경우 중복체크
                    int ch = 0;
                    for (int i = 0; i < result.size(); i++) {
                        if (result.get(i).getDate().equals(chart.getDate())) {
                            ch++;
                            result.get(i).setTotalpay(result.get(i).getTotalpay() + chart.getTotalpay());
                        }
                    }
                    if (ch == 0) {
                        result.add(chart);
                    }

                } else { // 동일하지 않는 연도

                }
            }
        }


        // 결과 -> json 형으로 변환
        for (Chart chart : result) {
            jsonObject.put(chart.getDate(), chart.getTotalpay());
        }
        return jsonObject;

    }

}