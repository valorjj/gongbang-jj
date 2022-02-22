package com.ezen.controller;

import com.ezen.domain.dto.MemberDto;
import com.ezen.domain.entity.*;
import com.ezen.domain.entity.repository.*;
import com.ezen.service.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private RoomLikeService roomLikeService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TimeTableRepository timeTableRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private ReplyService replyService;

    @Autowired
    private MemberRepository memberRepository;

    // [room_write.html 페이지와 맵핑]
    @GetMapping("/register")
    public String register(Model model) {

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

        return "room/room_register";
    }

    // [room_register_detail.html 페이지와 맵핑]
    @GetMapping("/registerDetail")
    public String registerDetail(Model model) {

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

        return "room/room_register_detail";
    }

    /*
     * @Author : 김정진
     *
     * @Date : 2022-02-07
     * 1. header 에 위치한 검색 창에서 '키워드검색' '지역 선택' '카테고리 선택' 세가지 경우에 결과값을 출력한다.
     * 2.
     */

    // [개설된 강좌 출력]
    // 검색이 있는 경우 / 검색이 없는 경우 구분 짓는다.
    @GetMapping("/list")
    public String roomlist(@PageableDefault Pageable pageable, Model model) {

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

        String keyword = request.getParameter("roomSearch");
        String local = request.getParameter("classLocal");
        String category = request.getParameter("classCategory");

        Page<RoomEntity> roomEntities = null;

        // 1. 검색, 지역, 카테고리 셋 중 하나라도 선택 했을 경우
        // 1. 선택한 결과값을 세션에 저장합니다.
        if (keyword != null || local != null || category != null) {
            session.setAttribute("keyword", keyword);
            session.setAttribute("local", local);
            session.setAttribute("category", category);
        }
        // 2. 아무것도 선택하지 않았을 경우, 이전 검색한 세션을 그대로 활용한다.
        else {
            // 1. 이전 세션이 없는 경우
            if (session.getAttribute("keyword") == null && session.getAttribute("local") == null
                    && session.getAttribute("category") == null) {
                roomEntities = roomService.getRoomEntityBySearch(pageable, "", "", "");
                return "room/room_list";
            } else {
                // 2. 이전 세션이 있는 경우
                keyword = (String) session.getAttribute("keyword");
                local = (String) session.getAttribute("local");
                category = (String) session.getAttribute("category");
            }
        }
        roomEntities = roomService.getRoomEntityBySearch(pageable, keyword, local, category);

        int roomSize = 0;
        System.out.println("#####" + roomEntities.get().count());
        model.addAttribute("listSize", roomEntities.get().count());

        if (roomEntities != null) {
            // 1. 개설된 강좌 리스트 정보를 넘겨줍니다.
            model.addAttribute("roomEntities", roomEntities);
            // 2. roomview.js 에서 사용하기 위해서 검색 관련 변수들을 front 로 넘겨줍니다.
            model.addAttribute("keyword", keyword);
            model.addAttribute("category", category);
            model.addAttribute("local", local);
        } else {
            // 비정상적인 경로로 접근하면 error 페이지를 띄운다.
            return "error";
        }
        return "room/room_list"; // 타임리프를 통한 html 반환
    }

    // 메인 화면에서 지역 아이콘 선택했을 때 검색 후 결과 출력 페이지로 이동
    @GetMapping("/byLocal/{local}")
    public String roomListByLocal(@PathVariable("local") String local, Model model,
                                  @PageableDefault Pageable pageable) {

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
        session.setAttribute("keyword", "");
        session.setAttribute("local", local);
        session.setAttribute("category", "");

        Page<RoomEntity> roomEntities = roomService.getRoomEntityBySearch(pageable, "", local, "");
        if (roomEntities == null) {
            return "error";
        }
        model.addAttribute("roomEntities", roomEntities);
        return "room/room_list";
    }

    // 메인 화면에서 카테고리 선택했을 때 검색 후 결과 출력 페이지로 이동
    @GetMapping("/byCategory/{category}")
    public String roomListByCategory(@PathVariable("category") String category, Model model,
                                     @PageableDefault Pageable pageable) {

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
        session.setAttribute("keyword", "");
        session.setAttribute("local", "");
        session.setAttribute("category", category);

        Page<RoomEntity> roomEntities = roomService.getRoomEntityBySearch(pageable, "", "", category);
        if (roomEntities == null) {
            return "error";
        }
        model.addAttribute("roomEntities", roomEntities);
        return "room/room_list";
    }

    // 룸보기 페이지 이동
    // @Date : 2022-02-16 -> 리뷰 평균 구하는 부분 추가
    @GetMapping("/view/{roomNo}") // 이동
    @Transactional
    public String roomview(@PathVariable("roomNo") int roomNo, Model model) {

        // 1. 선택된 클래스 엔티티를 불러와서 Model 로 전달한다.
        RoomEntity roomEntity = roomService.getroom(roomNo);
        model.addAttribute("roomEntity", roomEntity);

        // 2. roomNo 이용해서 해당 강좌의 개설된 정보 (TimeTable) 을 불러온다.
        List<TimeTableEntity> timeTableEntities = roomEntity.getTimeTableEntity();
        model.addAttribute("timeTableEntities", timeTableEntities);

        // 3. 좋아요 상태보내기
        int count = roomEntity.getRoomLikeEntities().size();
        model.addAttribute("count", count);

        // 4. 조회수를 증가시킵니다.
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

        if (loginDto == null) {
            // [비로그인상태]
            session.setAttribute("logindto", null);
            model.addAttribute("memberNo", -1);
            model.addAttribute("memberCheck", 2);
        } else {
            model.addAttribute("memberNo", loginDto.getMemberNo());
            // 해당 강의를 수강했던 사람의 목록을 넘긴다.
            List<HistoryEntity> historyEntities = historyRepository.getHistoryByRoomNo(roomNo);
            for (HistoryEntity historyEntity : historyEntities) {
                if (historyEntity.getMemberEntity().getMemberNo() == loginDto.getMemberNo()) {
                    // 수강 내역이 있는 사람이면 1 보낸다.
                    model.addAttribute("memberCheck", 1);
                } else {
                    // 수강 내역이 없으면 2 를 보낸다.
                    model.addAttribute("memberCheck", 2);
                }
            }

        }

        if (session.getAttribute(String.valueOf(roomNo)) == null) {
            // 조회수 증가
            roomEntity.setRoomView(roomEntity.getRoomView() + 1);
            // 세션 부여
            session.setAttribute(String.valueOf(roomNo), 1);
            session.setMaxInactiveInterval(60 * 60 * 24);
        }

        // 5. 리뷰 평균을 구해서 room_view.html 에 추가시킵니다.
        float sum = 0;
        float replyAvg = 0;
        List<ReplyEntity> replyEntities;
        // 5.1 해당 클래스에 등록된 댓글을 호출합니다.
        if (!replyRepository.getReplyByRoomNo(roomNo).isEmpty()) {
            replyEntities = replyRepository.getReplyByRoomNo(roomNo);
            float replySize = replyEntities.size();
            for (ReplyEntity reply : replyEntities) {
                sum += reply.getReplyStar();
            }
            replyAvg = (sum / replySize);
        }
        String avg = String.format("%.2f", replyAvg);
        // 리뷰 평균
        model.addAttribute("avg", avg);
        // 평균 점수를 엔티티에 기록한다.
        roomEntity.setRoomAvg(replyAvg);
        return "room/room_view";
    }

    // [작성한 클래스 등록]
    @PostMapping("/classRegister")
    @Transactional
    public String classRegister(Model model,
                                RoomEntity roomEntity,
                                @RequestParam("roomImageInput") List<MultipartFile> files,
                                @RequestParam("addressX") double addressX,
                                @RequestParam("addressY") double addressY,
                                @RequestParam("checkBox1") String checkBox1,
                                @RequestParam("checkBox2") String checkBox2,
                                @RequestParam("checkBox3") String checkBox3,
                                @PageableDefault Pageable pageable) {

        roomEntity.setRoomStatus("검토중");
        roomEntity.setRoomETC(checkBox1 + "," + checkBox2 + "," + checkBox3);
        roomEntity.setRoomAddress(roomEntity.getRoomAddress() + "," + addressY + "," + addressX);
        roomEntity.setRoomView(0);
        roomEntity.setRoomAvg(0);

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

        assert loginDto != null;
        int memberNo = loginDto.getMemberNo();

        boolean result = roomService.registerClass(roomEntity, files);

        if (result) {
            // 2. 등록 완료 후, 내가 등록한 클래스 페이지로 이동
            List<RoomEntity> roomDtos = roomService.getMyGongbang(memberNo);
            model.addAttribute("roomDtos", roomDtos);
            return "redirect:/member/myclass";
        } else {
            return "error";
        }
    }

    // [ 등록된 공방 정보 업데이트 페이지 맵핑]
    @GetMapping("/update/{roomNo}")
    public String update(@PathVariable("roomNo") int roomNo, Model model) {
        // 1. 입력된 모든 정보를 출력한다.
        // 1. 개설된 공방 정보를 넘겨준다.
        // 2. 회원 정보를 넘긴다.
        RoomEntity roomEntity = roomService.getroom(roomNo);
        model.addAttribute("roomEntity", roomEntity);

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
        return "room/room_update";
    }

    // [공방 정보 업데이트 적용 컨트롤러]
    @PostMapping("/updateController")
    @Transactional
    public String updateController(Model model,
                                   @RequestParam("roomTitle") String roomTitle,
                                   @RequestParam("roomContent") String roomContent,
                                   @RequestParam("roomDetail") String roomDetail,
                                   @RequestParam("roomImageInput") List<MultipartFile> files,
                                   @RequestParam("roomCategory") String roomCategory,
                                   @RequestParam("roomLocal") String roomLocal,
                                   @RequestParam("roomAddress") String roomAddress,
                                   @RequestParam("roomMax") int roomMax,
                                   @RequestParam("addressX") double addressX,
                                   @RequestParam("addressY") double addressY,
                                   @RequestParam("checkBox1") String checkBox1,
                                   @RequestParam("checkBox2") String checkBox2,
                                   @RequestParam("checkBox3") String checkBox3,
                                   @RequestParam("roomNo") int roomNo,
                                   @RequestParam("roomStatus") String roomStatus,
                                   @PageableDefault Pageable pageable) {

        RoomEntity targetRoomEntity = null;
        if (roomRepository.findById(roomNo).isPresent()) {
            targetRoomEntity = roomRepository.findById(roomNo).get();
        }
        assert targetRoomEntity != null;
        targetRoomEntity.setRoomETC(checkBox1 + "," + checkBox2 + "," + checkBox3);
        targetRoomEntity.setRoomAddress(roomAddress + "," + addressY + "," + addressX);
        targetRoomEntity.setRoomStatus(roomStatus);
        targetRoomEntity.setRoomCategory(roomCategory);
        targetRoomEntity.setRoomLocal(roomLocal);
        targetRoomEntity.setRoomMax(roomMax);
        targetRoomEntity.setRoomDetail(roomDetail);
        targetRoomEntity.setRoomTitle(roomTitle);
        targetRoomEntity.setRoomContent(roomContent);

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

        boolean result = roomService.updateClass(targetRoomEntity, files);

        List<RoomEntity> roomDtos = roomService.getmyroomlist();
        model.addAttribute("roomDtos", roomDtos);

        return "member/member_class";
    }

    // json 반환 [지도에 띄우고자 하는 방 응답하기]
    @GetMapping("/gongbang.json")
    @ResponseBody
    public JSONObject gongbang(@PageableDefault Pageable pageable) {

        HttpSession session = request.getSession();
        String keyword = (String) session.getAttribute("keyword");
        String local = (String) session.getAttribute("local");
        String category = (String) session.getAttribute("category");

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        Page<RoomEntity> roomEntities = roomService.getRoomEntityBySearch(pageable, keyword, local, category);
        for (RoomEntity roomEntity : roomEntities) {
            JSONObject data = new JSONObject();
            data.put("lat", roomEntity.getRoomAddress().split(",")[1]);
            data.put("lng", roomEntity.getRoomAddress().split(",")[2]);
            data.put("roomTitle", roomEntity.getRoomTitle());
            data.put("roomNo", roomEntity.getRoomNo());
            data.put("roomImg", roomEntity.getRoomImgEntities().get(0).getRoomImg());
            jsonArray.add(data);
        }
        jsonObject.put("positions", jsonArray);
        return jsonObject;
    }

    // 검색 값이 없는 경우에는 리스트 전체를 출력해야합니다.
    @GetMapping("/gongbangAll.json")
    @ResponseBody
    public JSONObject gongbangAll() {

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        List<RoomEntity> roomEntities = roomRepository.findAll();
        for (RoomEntity roomEntity : roomEntities) {
            JSONObject data = new JSONObject();
            data.put("lat", roomEntity.getRoomAddress().split(",")[1]);
            data.put("lng", roomEntity.getRoomAddress().split(",")[2]);
            data.put("roomTitle", roomEntity.getRoomTitle());
            data.put("roomNo", roomEntity.getRoomNo());
            data.put("roomImg", roomEntity.getRoomImgEntities().get(0).getRoomImg());
            jsonArray.add(data);
        }
        jsonObject.put("positions", jsonArray);
        return jsonObject;
    }

    @GetMapping("/addressXY")
    @ResponseBody
    public String addressXY(@RequestParam("roomNo") int roomNo) {
        return roomRepository.findById(roomNo).get().getRoomAddress();
    }

    // 내가 등록한 클래스 보기
    @GetMapping("/timeSelectPage/{roomNo}")
    public String timeSelectController(@PathVariable("roomNo") int roomNo, Model model) {
        // 1. 등록된 클래스 가져오기
        RoomEntity roomEntity = roomService.getroom(roomNo);
        model.addAttribute("room", roomEntity);

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


        return "member/member_timeselect";

    }

    // 등록한 클래스에 날짜, 시간 선택하기
    // /member/member_timeselect.html 에서 값을 받아옵니다.
    // form 태그로 받아오며 날짜, 시간, roomNo 를 받습니다.
    @GetMapping("/timeSelectController")
    public String timeSelectController(TimeTableEntity timeTableEntity,
                                       @RequestParam("beginTime") String beginTime,
                                       @RequestParam("endTime") String endTime,
                                       @RequestParam("roomNo") int roomNo,
                                       Model model, @PageableDefault Pageable pageable) {

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
        assert loginDto != null;
        int memberNo = loginDto.getMemberNo();

        timeTableEntity.setRoomTime(beginTime + "," + endTime);
        RoomEntity roomEntity = roomRepository.getById(roomNo);
        timeTableEntity.setRoomMax(roomEntity.getRoomMax());
        timeTableEntity.setRoomStatus("모집중");
        boolean result = roomService.registerTimeToClass(timeTableEntity, roomNo);

        List<RoomEntity> roomDtos = roomService.getMyGongbang(memberNo);
        model.addAttribute("roomDtos", roomDtos);

        return "member/member_class";
    }

    // @Author : 김정진
    // @Date : 2022-02-10
    // @Note : 특정 roomNo 에 해당하는 TimeTable 정보만 가져오는 메소드
    @GetMapping("/timetable")
    @ResponseBody
    public String getTimeTableByRoomNo(@RequestParam("roomNo") int roomNo) {
        StringBuilder str = new StringBuilder();
        List<TimeTableEntity> timeTableEntities = timeTableRepository.getTimeTableByRoomNo(roomNo);
        for (TimeTableEntity time : timeTableEntities) {
            str.append(time.getRoomDate()).append(",");
        }
        return str.toString();
    }

    // @Author: 김정진
    // @Date : 2022-02-10
    // @Note : YYYY-MM-DD 값으로 RoomEntity 를 조회 후 데이터 뿌려주기
    // JS 에서 Entity 를 읽을 수 없으니 JSON 형태로 변환해서 보낸다.
    @GetMapping("/toJSON")
    @ResponseBody
    public JSONObject getRoomEntityByTimeTableToJson(@RequestParam("activeId") String roomDate,
                                                     @RequestParam("roomNo") int roomNo) {

        HttpSession session = request.getSession();
        MemberDto loginDto = (MemberDto) session.getAttribute("logindto");
        int memberNo = loginDto.getMemberNo();

        JSONObject jsonObject = new JSONObject(); // json
        JSONArray jsonArray = new JSONArray(); // json
        // roomNo 에 해당하는 TimeTable 엔티티만 리스트에 담아서 호출한다.
        List<TimeTableEntity> timeTableEntities = timeTableRepository.getTimeTableByRoomNo(roomNo);
        // roomNo 에 해당하는 개설된 강좌 전체를 for 문으로 조회한다.
        for (TimeTableEntity timeTableEntity : timeTableEntities) {
            int roomNoFromTimeTable = timeTableEntity.getRoomEntity().getRoomNo();
            // TimeTable 에 저장된 RoomNo 를 사용해서 Room Entity 를 호출하되, '승인완료' 인 클래스만 리턴한다.
            RoomEntity roomEntity = roomRepository.findRoomByStatusAndNo("승인완료", roomNoFromTimeTable);
            // 선택한 date 에 해당하는 Room 정보만을 json 에 저장시킨다.
            if (timeTableEntity.getRoomDate().equals(roomDate)) {
                JSONObject data = new JSONObject(); // json
                try {
                    data.put("roomNo", roomEntity.getRoomNo());
                    data.put("category", roomEntity.getRoomCategory());
                    data.put("title", roomEntity.getRoomTitle());
                    data.put("date", timeTableEntity.getRoomDate());
                    data.put("beginTime", timeTableEntity.getRoomTime().split(",")[0]);
                    data.put("endTime", timeTableEntity.getRoomTime().split(",")[1]);
                    data.put("local", roomEntity.getRoomLocal());
                    data.put("max", roomEntity.getRoomMax());
                    data.put("timeMax", timeTableEntity.getRoomMax());
                    jsonArray.add(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        jsonObject.put("json", jsonArray);
        return jsonObject;
        // ajax 에서 받아온 stringify 된 데이터를 map 형태로 변환해서 model 에 출력한다.
        // @GetMapping("/")
    }

    // 문의 등록
    @GetMapping("/notewrite")
    @ResponseBody
    public String notewrite(@RequestParam("roomNo") int roomNo, @RequestParam("noteContents") String noteContents) {

        boolean result = roomService.notewrite(roomNo, noteContents);
        if (result) {
            return "1";
        } else {
            return "2";
        }
    }

    // 읽음처리 업데이트
    @GetMapping("/nreadupdate")
    @ResponseBody // 페이지 전환하면 안되서 사용
    public void nreadupdate(@RequestParam("noteNo") int noteNo) {
        roomService.nreadupdate(noteNo);
    }

    // 개설한 클래스 삭제 02-18 조지훈
    @PostMapping("/roomdelete")
    @ResponseBody
    public String roomdelete(@RequestParam("roomNo") int roomNo) {
        boolean result = roomService.roomdelete(roomNo);
        if (result) {
            return "1";
        } else {
            return "2";
        }
    }

    // 방번호를 이용한 방정보 html 반환
    @GetMapping("/getroom")
    public String getroom(@RequestParam("roomNo") int roomNo, Model model) {

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

        model.addAttribute("room", roomService.getroom(roomNo));
        return "room/room_list"; // room html 반환
    }

    @GetMapping("/room_map")
    public String room_map(Model model, @RequestParam("roomNo") int roomNo) {

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

        RoomEntity roomEntity = roomService.getroom(roomNo);
        String img = roomEntity.getRoomImgEntities().get(0).getRoomImg();

        model.addAttribute("roommap", roomEntity);
        model.addAttribute("img", img);
        return "room/room_map";
    }

}