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
import java.util.*;


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
            // [???????????? ???????????? ??????]
            assert memberEntity != null;
            if (memberEntity.getChannelImg() == null) {
                // [????????? ????????? ???????????? ?????? ??????]
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
            // [???????????? ???????????? ??????]
            assert memberEntity != null;
            if (memberEntity.getChannelImg() == null) {
                // [????????? ????????? ???????????? ?????? ??????]
                model.addAttribute("isLoginCheck", 1);
            } else {
                model.addAttribute("isLoginCheck", 2);
            }
            model.addAttribute("memberEntity", memberEntity);
        }

        // 1. ??? ???????????? ?????? ????????? RoomService ?????? ????????????.
        Page<RoomEntity> roomEntities = roomService.adminGetRoomEntityBySearch(pageable, keyword, local, category);
        // 2. ????????? ?????? Model ??? ?????? table ????????? ????????????.
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
            // [???????????? ???????????? ??????]
            assert memberEntity != null;
            if (memberEntity.getChannelImg() == null) {
                // [????????? ????????? ???????????? ?????? ??????]
                model.addAttribute("isLoginCheck", 1);
            } else {
                model.addAttribute("isLoginCheck", 2);
            }
            model.addAttribute("memberEntity", memberEntity);
        }

        // 1. ?????? ????????? ?????? ?????? [?????? ?????? ??????]
        // 1.1 header.html href ????????? ????????????.
        // ????????? ???????????? ?????? ????????? ????????????.
        Page<RoomEntity> roomEntities = roomService.getroomlistadmin(pageable);
        model.addAttribute("roomEntities", roomEntities);

        // 2. ?????? ?????? ??????, ?????? ???????????? ????????????.
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

        // RoomEntity ??? JSON ?????? ?????? ??? js ??? ???????????? ??????
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        List<HistoryEntity> historyEntities = historyRepository.findAll();

        // date ??? ???????????? ?????? ????????? ????????????.
        List<TimeTableEntity> timeTableEntities = timeTableRepository.getTimeTableByRoomDate(date);

        for (TimeTableEntity timeTableEntity : timeTableEntities) {
            for (HistoryEntity historyEntity : historyEntities) {
                // ?????? ????????? ???????????? ?????? ?????? ????????? json ?????? ???????????????.
                if (historyEntity.getTimeTableEntity().getTimeTableNo() == timeTableEntity.getTimeTableNo()) {

                    RoomEntity roomEntity = null;
                    JSONObject data = new JSONObject();

                    data.put("date", timeTableEntity.getRoomDate()); // YYYY-MM-DD
                    data.put("beginTime", timeTableEntity.getRoomTime().split(",")[0]); // HH, HH
                    data.put("endTime", timeTableEntity.getRoomTime().split(",")[1]); // HH, HH

                    // 3. ?????? ???????????? ???????????? ?????? ??????
                    int roomNo = historyEntity.getRoomEntity().getRoomNo();
                    if (roomRepository.findById(roomNo).isPresent()) {
                        roomEntity = roomRepository.findById(roomNo).get();
                    }

                    assert roomEntity != null;
                    data.put("category", roomEntity.getRoomCategory());
                    data.put("local", roomEntity.getRoomLocal());

                    // 4. ?????? ??????
                    data.put("createdDate", historyEntity.getCreatedDate()); // ????????? ????????? ??????
                    data.put("price", historyEntity.getHistoryPoint()); // ????????? ????????? ??????

                    // 5. ????????? ?????? ??? : ?????? ?????? / ????????? 1?????? ??????
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

//        Comparator<TimeTableEntity> comparator = new Comparator<TimeTableEntity>() {
//            // TimeTableEntity ??? ??????????????? ????????????.
//            // roomDate --> YYYY : MM : DD ??? ???????????????.
//            @Override
//            public int compare(TimeTableEntity o1, TimeTableEntity o2) {
//
//                String dateCompare1 = "";
//                String dateCompare2 = "";
//
//                String dateYear1 = o1.getRoomDate().split("-")[0];
//                String dateYear2 = o2.getRoomDate().split("-")[0];
//
//                String dateMonth1 = "";
//                String dateMonth2 = "";
//
//                String dateDay1 = "";
//                String dateDay2 = "";
//
//                if (Integer.parseInt(o1.getRoomDate().split("-")[1]) < 10) {
//                    dateMonth1 = "0" + o1.getRoomDate().split("-")[1];
//                } else {
//                    dateMonth1 = o1.getRoomDate().split("-")[1];
//                }
//
//                if (Integer.parseInt(o1.getRoomDate().split("-")[2]) < 10) {
//                    dateDay1 = "0" + o1.getRoomDate().split("-")[2];
//                } else {
//                    dateDay1 = o1.getRoomDate().split("-")[2];
//                }
//
//                if (Integer.parseInt(o2.getRoomDate().split("-")[1]) < 10) {
//                    dateMonth2 = "0" + o2.getRoomDate().split("-")[1];
//                } else {
//                    dateMonth2 = o2.getRoomDate().split("-")[1];
//                }
//
//                if (Integer.parseInt(o2.getRoomDate().split("-")[2]) < 10) {
//                    dateDay2 = "0" + o2.getRoomDate().split("-")[2];
//                } else {
//                    dateDay2 = o2.getRoomDate().split("-")[2];
//                }
//
//                dateCompare1 = dateYear1 + dateMonth1 + dateDay1;
//                dateCompare2 = dateYear2 + dateMonth2 + dateDay2;
//
//                int time1 = Integer.parseInt(dateCompare1);
//                int time2 = Integer.parseInt(dateCompare2);
//
//                return time1 - time2;
//            }
//        };

        // RoomEntity ??? JSON ?????? ?????? ??? js ??? ???????????? ??????
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        // 1. historyEntities ??? ?????? ????????? ????????? ????????????.
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

        // 2. ???????????? ????????? ????????? roomDate ????????? ??????????????????.
        // 2.1 timetable entity ??? ????????? ?????? ????????? ?????????.

        List<TimeTableEntity> timeTableEntities = timeTableRepository.findAll();
        // timeTableEntities.sort(comparator);

        // map ??? ???????????? ????????? ????????????.
        Map<String, Integer> myMap = new TreeMap<String, Integer>();

        for (TimeTableEntity timeTableEntity : timeTableEntities) {

            List<HistoryEntity> historyList = timeTableEntity.getHistoryEntity();

            for (HistoryEntity historyEntity : historyList) {

                RoomEntity roomEntity = null;

                String date = timeTableEntity.getRoomDate();

                // 3. ?????? ???????????? ???????????? ?????? ??????
                int roomNo = historyEntity.getRoomEntity().getRoomNo();
                if (roomRepository.findById(roomNo).isPresent()) {
                    roomEntity = roomRepository.findById(roomNo).get();
                }

                // 5. ????????? ?????? ??? : ?????? ?????? / ????????? 1?????? ??????
                int person = historyEntity.getHistoryPoint() / roomEntity.getRoomPrice();

                if (myMap.containsKey(date)) {
                    myMap.put(date, myMap.get(date) + person);
                } else {
                    myMap.put(date, person);
                }
            }
        }

        // json ????????? ??????
        for (String s : myMap.keySet()) {

            for (HistoryEntity historyEntity : historyEntities) {

                JSONObject data = new JSONObject();
                RoomEntity roomEntity = null;

                int roomNo = historyEntity.getRoomEntity().getRoomNo();

                if (roomRepository.findById(roomNo).isPresent()) {
                    roomEntity = roomRepository.findById(roomNo).get();
                }

                assert roomEntity != null;
                data.put("category", roomEntity.getRoomCategory());
                data.put("local", roomEntity.getRoomLocal());

                data.put("date", s);
                data.put("person", myMap.get(s));

                jsonArray.add(data);

            }

        }

        jsonObject.put("history", jsonArray);
        return jsonObject;

    }

    @GetMapping("/roomJSON2")
    @ResponseBody
    public JSONObject roomToJSON2() {

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        // 1. historyEntities ??? ?????? ????????? ????????? ????????????.
        List<HistoryEntity> historyEntities = historyRepository.findAll();

        List<TimeTableEntity> timeTableEntities = timeTableRepository.findAll();


        for (TimeTableEntity timeTableEntity : timeTableEntities) {

            List<HistoryEntity> historyList = timeTableEntity.getHistoryEntity();

            for (HistoryEntity historyEntity : historyList) {

                JSONObject data = new JSONObject();
                RoomEntity roomEntity = null;

                String date = timeTableEntity.getRoomDate();

                int roomNo = historyEntity.getRoomEntity().getRoomNo();
                if (roomRepository.findById(roomNo).isPresent()) {
                    roomEntity = roomRepository.findById(roomNo).get();
                }

                assert roomEntity != null;
                int person = historyEntity.getHistoryPoint() / roomEntity.getRoomPrice();

                data.put("category", roomEntity.getRoomCategory());
                data.put("local", roomEntity.getRoomLocal());

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

    // ???????????? ????????? ??? ????????????
    // '?????????' '?????????' '????????????' '????????????' '????????????'
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