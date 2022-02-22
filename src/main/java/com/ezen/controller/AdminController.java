package com.ezen.controller;

import com.ezen.domain.dto.MemberDto;
import com.ezen.domain.entity.HistoryEntity;
import com.ezen.domain.entity.MemberEntity;
import com.ezen.domain.entity.RoomEntity;
import com.ezen.domain.entity.TimeTableEntity;
import com.ezen.domain.entity.repository.HistoryRepository;
import com.ezen.domain.entity.repository.MemberRepository;
import com.ezen.domain.entity.repository.RoomRepository;
import com.ezen.domain.entity.repository.TimeTableRepository;
import com.ezen.service.AdminService;
import com.ezen.service.RoomService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private TimeTableRepository timeTableRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private HttpServletRequest request;

    @GetMapping("/adminTableAll")
    public String adminTable(Model model, @PageableDefault Pageable pageable) {

        HttpSession session = request.getSession();
        MemberDto loginDto = (MemberDto) session.getAttribute("logindto");
        MemberEntity memberEntity = null;
        if (loginDto != null) {
            if (memberRepository.findById(loginDto.getMemberNo()).isPresent())
                memberEntity = memberRepository.findById(loginDto.getMemberNo()).get();
            // [로그인이 되어있는 상태]
            assert memberEntity != null;
            if (memberEntity.getChannelImg() == null) {
                // [채널에 등록된 이미지가 없는 경우]
                model.addAttribute("isLoginCheck", 1);
            } else {
                model.addAttribute("isLoginCheck", 2);
            }
            model.addAttribute("memberEntity", memberEntity);
        }

        Page<RoomEntity> roomEntities = roomService.getroomlistadmin(pageable);
        model.addAttribute("roomEntities", roomEntities);
        return "admin/admin_table";
    }

    @GetMapping("/adminTableBySearch")
    public String adminTableBySearch(Model model,
                                     @PageableDefault Pageable pageable,
                                     @RequestParam("keyword") String keyword,
                                     @RequestParam("category") String category,
                                     @RequestParam("local") String local) {

        HttpSession session = request.getSession();
        MemberDto loginDto = (MemberDto) session.getAttribute("logindto");
        MemberEntity memberEntity = null;
        if (loginDto != null) {
            if (memberRepository.findById(loginDto.getMemberNo()).isPresent())
                memberEntity = memberRepository.findById(loginDto.getMemberNo()).get();
            // [로그인이 되어있는 상태]
            assert memberEntity != null;
            if (memberEntity.getChannelImg() == null) {
                // [채널에 등록된 이미지가 없는 경우]
                model.addAttribute("isLoginCheck", 1);
            } else {
                model.addAttribute("isLoginCheck", 2);
            }
            model.addAttribute("memberEntity", memberEntity);
        }

        // 1. 각 입력값의 존재 여부는 RoomService 에서 확인한다.
        Page<RoomEntity> roomEntities = roomService.adminGetRoomEntityBySearch(pageable, keyword, local, category);
        // 2. 조회한 값을 Model 을 통해 table 형태로 출력한다.
        model.addAttribute("roomEntities", roomEntities);
        return "admin/admin_table";
    }

    @GetMapping("/adminlist")
    public String adminlist(Model model, @PageableDefault Pageable pageable) {

        HttpSession session = request.getSession();
        MemberDto loginDto = (MemberDto) session.getAttribute("logindto");
        MemberEntity memberEntity = null;
        if (loginDto != null) {
            if (memberRepository.findById(loginDto.getMemberNo()).isPresent())
                memberEntity = memberRepository.findById(loginDto.getMemberNo()).get();
            // [로그인이 되어있는 상태]
            assert memberEntity != null;
            if (memberEntity.getChannelImg() == null) {
                // [채널에 등록된 이미지가 없는 경우]
                model.addAttribute("isLoginCheck", 1);
            } else {
                model.addAttribute("isLoginCheck", 2);
            }
            model.addAttribute("memberEntity", memberEntity);
        }

        // 1. 아무 검색이 없는 경우 [초기 진입 화면]
        // 1.1 header.html href 통해서 들어온다.
        // 별다른 조건없이 모든 데이터 뿌려준다.
        Page<RoomEntity> roomEntities = roomService.getroomlistadmin(pageable);
        model.addAttribute("roomEntities", roomEntities);

        // 2. 누적 결제 금액, 누적 회원수를 알려준다.
        List<HistoryEntity> historyEntities = historyRepository.findAll();
        int totalNumber = 0;
        int totalPrice = 0;
        int people = 0;
        int roomPrice = 0;
        RoomEntity roomEntity = null;
        for (HistoryEntity historyEntity : historyEntities) {
            totalPrice += historyEntity.getHistoryPoint();
            roomEntity = roomRepository.getById(historyEntity.getRoomEntity().getRoomNo());
            roomPrice = roomEntity.getRoomPrice();
            people = historyEntity.getHistoryPoint() / roomPrice;
            totalNumber += people;
        }

        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalNumber", totalNumber);

        return "admin/adminlist";
    }

    @GetMapping("/roomJSONDaySelect")
    @ResponseBody
    public JSONObject daySelectJSON(@RequestParam("select-date") String date) {

        // RoomEntity 를 JSON 으로 변환 후 js 로 넘겨주는 역할
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        List<HistoryEntity> historyEntities = historyRepository.findAll();

        // date 에 해당하는 예약 내역만 불러온다.
        List<TimeTableEntity> timeTableEntities = timeTableRepository.getTimeTableByRoomDate(date);

        for (TimeTableEntity timeTableEntity : timeTableEntities) {
            for (HistoryEntity historyEntity : historyEntities) {
                // 특정 날짜에 해당하는 예약 내역 정보만 json 으로 넘겨줍니다.
                if (historyEntity.getTimeTableEntity().getTimeTableNo() == timeTableEntity.getTimeTableNo()) {

                    RoomEntity roomEntity = null;
                    JSONObject data = new JSONObject();

                    data.put("date", timeTableEntity.getRoomDate()); // YYYY-MM-DD
                    data.put("beginTime", timeTableEntity.getRoomTime().split(",")[0]); // HH, HH
                    data.put("endTime", timeTableEntity.getRoomTime().split(",")[1]); // HH, HH

                    // 3. 현재 예약건에 해당하는 강좌 정보
                    int roomNo = historyEntity.getRoomEntity().getRoomNo();
                    if (roomRepository.findById(roomNo).isPresent()) {
                        roomEntity = roomRepository.findById(roomNo).get();
                    }

                    assert roomEntity != null;
                    data.put("category", roomEntity.getRoomCategory());
                    data.put("local", roomEntity.getRoomLocal());

                    // 4. 예약 정보
                    data.put("createdDate", historyEntity.getCreatedDate()); // 예약이 완료된 날짜
                    data.put("price", historyEntity.getHistoryPoint()); // 회원이 결제한 금액

                    // 5. 신청한 인원 수 : 결제 금액 / 클래스 1명당 금액
                    int person = historyEntity.getHistoryPoint() / roomEntity.getRoomPrice();
                    data.put("person", person);

                    jsonArray.add(data);

                }
            }
        }
        jsonObject.put("history", jsonArray);
        return jsonObject;
    }


    @GetMapping("/roomJSON")
    @ResponseBody
    public JSONObject roomToJSON() {

        Comparator<TimeTableEntity> comparator = new Comparator<TimeTableEntity>() {
            // TimeTableEntity 를 날짜순으로 정렬한다.
            // roomDate --> YYYY : MM : DD 로 정렬시킨다.
            @Override
            public int compare(TimeTableEntity o1, TimeTableEntity o2) {

                String dateCompare1 = "";
                String dateCompare2 = "";

                String dateYear1 = o1.getRoomDate().split("-")[0];
                String dateYear2 = o2.getRoomDate().split("-")[0];

                String dateMonth1 = "";
                String dateMonth2 = "";

                String dateDay1 = "";
                String dateDay2 = "";

                if (Integer.parseInt(o1.getRoomDate().split("-")[1]) < 10) {
                    dateMonth1 = "0" + o1.getRoomDate().split("-")[1];
                } else {
                    dateMonth1 = o1.getRoomDate().split("-")[1];
                }

                if (Integer.parseInt(o1.getRoomDate().split("-")[2]) < 10) {
                    dateDay1 = "0" + o1.getRoomDate().split("-")[2];
                } else {
                    dateDay1 = o1.getRoomDate().split("-")[2];
                }

                if (Integer.parseInt(o2.getRoomDate().split("-")[1]) < 10) {
                    dateMonth2 = "0" + o2.getRoomDate().split("-")[1];
                } else {
                    dateMonth2 = o2.getRoomDate().split("-")[1];
                }

                if (Integer.parseInt(o2.getRoomDate().split("-")[2]) < 10) {
                    dateDay2 = "0" + o2.getRoomDate().split("-")[2];
                } else {
                    dateDay2 = o2.getRoomDate().split("-")[2];
                }

                dateCompare1 = dateYear1 + dateMonth1 + dateDay1;
                dateCompare2 = dateYear2 + dateMonth2 + dateDay2;

                int time1 = Integer.parseInt(dateCompare1);
                int time2 = Integer.parseInt(dateCompare2);

                return time1 - time2;
            }
        };

        // RoomEntity 를 JSON 으로 변환 후 js 로 넘겨주는 역할
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        // 1. historyEntities 는 전체 회원이 예약한 내역이다.
        List<HistoryEntity> historyEntities = historyRepository.findAll();

        // 1. RoomEntity
        // 1. roomCategory
        // 2. roomLocal
        // 2. TimeTableEntity
        // 1. roomDate
        // 2. roomTime
        // 3. HistoryEntity
        // 1. historyPoint
        // 2. createdDate

        // 2. 회원들이 예약한 내역을 roomDate 순으로 정렬해야한다.
        // 2.1 timetable entity 에 저장된 예약 목록을 뽑는다.

        List<TimeTableEntity> timeTableEntities = timeTableRepository.findAll();
        timeTableEntities.sort(comparator);

        for (TimeTableEntity timeTableEntity : timeTableEntities) {
            List<HistoryEntity> historyList = timeTableEntity.getHistoryEntity();

            for (HistoryEntity historyEntity : historyList) {

                RoomEntity roomEntity = null;
                JSONObject data = new JSONObject();

                data.put("date", timeTableEntity.getRoomDate()); // YYYY-MM-DD
                data.put("beginTime", timeTableEntity.getRoomTime().split(",")[0]); // HH, HH
                data.put("endTime", timeTableEntity.getRoomTime().split(",")[1]); // HH, HH

                // 3. 현재 예약건에 해당하는 강좌 정보
                int roomNo = historyEntity.getRoomEntity().getRoomNo();
                if (roomRepository.findById(roomNo).isPresent()) {
                    roomEntity = roomRepository.findById(roomNo).get();
                }

                assert roomEntity != null;
                data.put("category", roomEntity.getRoomCategory());
                data.put("local", roomEntity.getRoomLocal());

                // 4. 예약 정보
                data.put("createdDate", historyEntity.getCreatedDate()); // 예약이 완료된 날짜
                data.put("price", historyEntity.getHistoryPoint()); // 회원이 결제한 금액

                // 5. 신청한 인원 수 : 결제 금액 / 클래스 1명당 금액
                int person = historyEntity.getHistoryPoint() / roomEntity.getRoomPrice();
                data.put("person", person);

                jsonArray.add(data);
            }
        }
        jsonObject.put("history", jsonArray);
        return jsonObject;

    }


    public List<RoomEntity> adminListWithoutPageable(String keyword, String category, String local) {
        List<RoomEntity> roomEntities = null;
        return roomEntities;
    }

    @GetMapping("/delete")
    @ResponseBody
    public String delete(@RequestParam("roomNo") int roomNo) {
        roomService.delete(roomNo);
        return "1";
    }

    // 방번호를 이용한 방 상태변경
    // '검토중' '승인중' '승인완료' '승인거부' '정원마감'
    @GetMapping("/activeupdate")
    @ResponseBody
    public String activeupdate(@RequestParam("roomNo") int roomNo,
                               @RequestParam("active") String update) {
        boolean result = roomService.activeupdate(roomNo, update);
        if (result) {
            return "1";
        } else {
            return "2";
        }
    }

}