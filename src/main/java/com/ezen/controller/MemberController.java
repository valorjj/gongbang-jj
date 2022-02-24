package com.ezen.controller;

import com.ezen.domain.dto.MemberDto;
import com.ezen.domain.dto.RoomPaymentDto;
import com.ezen.domain.entity.*;
import com.ezen.domain.entity.repository.*;
import com.ezen.service.MemberService;
import com.ezen.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@RequestMapping("/member")
@Controller
public class MemberController { // C S

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TimeTableRepository timeTableRepository;

    // 예약 내역을 저장할 Repo
    // 예약 내역(=HistoryEntity) 는 따로 Controller, Service 를 만들지 않습니다.
    // 예약 내역과 관련된 것은 모두 MemberController, ServiceController 에 선언합니다.
    @Autowired
    private HistoryRepository historyRepository;

    // 클래스 장바구니
    @Autowired
    private RoomLikeRepository roomLikeRepository;

    // 회원가입페이지 연결
    @GetMapping("/signup")
    public String signup() {
        return "member/signup";
    }

    // 회원가입 처리 연결
    @PostMapping("/signupController") // 회원가입 처리 연결
    public String signupController(MemberDto memberDto
    ) {
        memberService.memberSignup(memberDto);
        return "redirect:/";  // 회원가입 성공시 메인페이지 연결
    }

    // 업데이트 처리 연결
    @PostMapping("/updateController") // 회원가입 처리 연결
    public String updateController(MemberDto memberDto, Model model) {

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

        memberService.memberUpdate(memberDto);
        return "member/info"; // 회원가입 성공시 메인페이지 연결
    }

    // 이메일 중복체크
    @GetMapping("/emailcheck")
    @ResponseBody
    public String emailcheck(@RequestParam("memberEmail") String memail) {
        boolean result = memberService.emailcheck(memail);
        if (result) {
            return "1"; // 중복
        } else {
            return "2"; // 중복x
        }
    }

    // 아이디 중복체크
    @GetMapping("idCheck")
    @ResponseBody
    public String idCheck(@RequestParam("memberId") String memberId) {
        boolean result = memberService.idCheck(memberId);
        if (result) {
            return "1";
        } else {
            return "2";
        }
    }

    // 로그인페이지 연결
    @GetMapping("/login")
    public String login() {
        return "member/login";
    }

    // 마이페이지 연결
    @GetMapping("/info")
    public String info(Model model) {

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
        }
        // 3. 찾은 회원정보를 model 인터페이스를 이용한 view 전달하기
        model.addAttribute("memberEntity", memberEntity);

        return "member/info";
    }

    // 회원삭제 처리
    @GetMapping("/mdelete")
    @ResponseBody
    public int mdelete(@RequestParam("passwordconfirm") String passwordconfirm) {

        // 1. 세션 호출
        HttpSession session = request.getSession();
        MemberDto memberDto = (MemberDto) session.getAttribute("logindto");
        // 2. service에 로그인된 회원번호 , 확인패스워드
        boolean result = memberService.delete(memberDto.getMemberNo(), passwordconfirm);
        // 3. 결과 를 ajax에게 응답
        if (result) {
            return 1;
        } else {
            return 2;
        }
    }

    // @Author : 김정진
    // @Date : 2022-02-17
    // 아이디, 비밀번호 찾기
    @GetMapping("/findMyId")
    @ResponseBody
    public String findMyIdController(@RequestParam("memberName") String name, @RequestParam("memberPhone") String phone) {
        // js 에서 이름, 핸드폰 번호 받고 아이디 알려주기

        MemberEntity memberEntity = null;
        if (memberService.findMyId(name, phone) != null) {
            memberEntity = memberService.findMyId(name, phone);
            return memberEntity.getMemberId();
        }
        return "";
    }


    // @Author : 김정진
    // @Date : 2022-02-11
    // @Note :
    // 1. room_view 에서 예약 버튼을 누르기까지 과정을 담당하는 메소드입니다.
    // 2. 개설된 클래스에 남는 자리가 있는지, 회원의 포인트는 충분한지를 검사합니다.
    // 3. 즉 유효성 검사하는 메소드입니다.
    @GetMapping("/registerClass")
    @ResponseBody
    @Transactional
    public String registerClass(@RequestParam("roomNo") int roomNo,
                                @RequestParam("roomTime") String classTime,
                                @RequestParam("roomDate") String roomDate,
                                @RequestParam("person") int person,
                                @RequestParam("price") int price) {

        MemberEntity memberEntity = null;
        // 0. 로그인된 회원 정보를 불러온다.
        HttpSession session = request.getSession();
        MemberDto loginDto = (MemberDto) session.getAttribute("logindto");
        // 0.1 로그인 세션 정보가 없으면 메인 페이지로 이동해서 로그인을 요구한다.
        if (loginDto == null) {
            return "redirect: /index";
        } else {
            // 0.2 로그인 세션 정보가 존재하면 member Entity 를 호출한다.
            memberEntity = memberService.getMemberEntity(loginDto.getMemberNo());
        }

        // 회원이 가진 포인트가 결제 가격보다 작으면 오류
        if (memberEntity.getMemberPoint() < price) {
            return "3";
        }

        RoomEntity roomEntity = null;
        TimeTableEntity timeTableTmp = null;

        // 1. roomNo 에 해당하는 room 엔티티를 호출한다.
        // 1.1 history 저장 후 room 엔티티에 선언된 list 에 histroy 를 추가시켜야한다.
        if (roomRepository.findById(roomNo).isPresent()) {
            roomEntity = roomRepository.findById(roomNo).get();
        }

        // 2. 받아온 시간으로 TimeTable 을 가져온다.
        // 2.1 TimeTable 내에서 roomTime 에 해당하는 것만 등록한다.
        List<TimeTableEntity> timeTableEntities = timeTableRepository.getTimeTableByRoomNo(roomNo);
        for (TimeTableEntity timeTableEntity : timeTableEntities) {
            if (timeTableEntity.getRoomTime().equals(classTime)) {
                timeTableTmp = timeTableEntity;
            }
        }
        // 3. 수용 가능한 인원보다 신청 인원이 많다면 신청할 수 없다.
        assert timeTableTmp != null;
        if (timeTableTmp.getRoomMax() < person) {
            return "2";
        }
        return "1";
    }

    // @Author : 김정진
    // @Date : 2022-02-16
    // 최종적으로 결제가 이루어지는 메소드입니다.
    // 정원 차감 & 포인트 차감이 이루어집니다.
    @PostMapping("/memberPay")
    @Transactional
    public String memberPay(Model model,
                            @RequestParam("roomNo") int roomNo,
                            @RequestParam("roomTime") String classTime,
                            @RequestParam("roomDate") String roomDate,
                            @RequestParam("person") int person,
                            @RequestParam("price") int price,
                            @RequestParam("phoneNumber") String phone) {


        MemberEntity memberEntity = null;
        // 0. 로그인된 회원 정보를 불러온다.
        HttpSession session = request.getSession();
        MemberDto loginDto = (MemberDto) session.getAttribute("logindto");

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


        // 0.1 로그인 세션 정보가 없으면 메인 페이지로 이동해서 로그인을 요구한다.
        if (loginDto == null) {
            return "redirect: /index";
        } else {
            // 0.2 로그인 세션 정보가 존재하면 member Entity 를 호출한다.
            memberEntity = memberService.getMemberEntity(loginDto.getMemberNo());
        }

        int memberNo = memberEntity.getMemberNo();

        RoomEntity roomEntity = null;
        TimeTableEntity timeTableTmp = null;

        // 1. roomNo 에 해당하는 room 엔티티를 호출한다.
        // 1.1 history 저장 후 room 엔티티에 선언된 list 에 histroy 를 추가시켜야한다.
        if (roomRepository.findById(roomNo).isPresent()) {
            roomEntity = roomRepository.findById(roomNo).get();
        }

        // 2. 받아온 시간으로 TimeTable 을 가져온다.
        // 2.1 TimeTable 내에서 roomTime 에 해당하는 것만 등록한다.
        List<TimeTableEntity> timeTableEntities = timeTableRepository.getTimeTableByRoomNo(roomNo);
        for (TimeTableEntity timeTableEntity : timeTableEntities) {
            if (timeTableEntity.getRoomTime().equals(classTime)) {
                timeTableTmp = timeTableEntity;
            }
        }

        // 3. HistoryEntity 에 멤버 정보, 클래스 정보를 들록합니다.
        HistoryEntity historyEntity = HistoryEntity.builder()
                .memberEntity(memberEntity)
                .roomEntity(roomEntity)
                .timeTableEntity(timeTableTmp)
                .build();

        // 4. 예약내역 저장하고 저장번호 받아오기
        int savedHistoryEntityNo = historyRepository.save(historyEntity).getHistoryNo();

        // 5. 신청한 정원만큼 클래스 수용 인원을 감소시킵니다.
        assert timeTableTmp != null;
        timeTableTmp.setRoomMax(timeTableTmp.getRoomMax() - person);
        // 수용 가능 인원이 '0' 명이 된다면, 클래스 상태를 '모집완료' 로 바꿉니다.
        if (timeTableTmp.getRoomMax() <= 0) {
            timeTableTmp.setRoomStatus("모집완료");
        }

        // 6. 위에서 저장한 예약내역 가져오기
        HistoryEntity savedHistoryEntity = historyRepository.findById(savedHistoryEntityNo).get();

        // 7. History 엔티티와 @OneToMany 맵핑이 되어있는 엔티티에 History 엔티티를 추가시킵니다.
        timeTableTmp.getHistoryEntity().add(savedHistoryEntity);
        memberEntity.getHistoryEntities().add(savedHistoryEntity);
        assert roomEntity != null;
        roomEntity.getHistoryEntities().add(savedHistoryEntity);

        // 8. 최종적으로 회원이 가진 포인트를 감소시킵니다.
        memberEntity.setMemberPoint(memberEntity.getMemberPoint() - price);

        int memberPoint;
        if (historyEntity.getHistoryPoint() == 0) {
            memberPoint = 0;
        } else {
            memberPoint = historyEntity.getHistoryPoint();
        }
        historyEntity.setHistoryPoint(memberPoint + price);
        historyEntity.setPhoneNumber(phone);
        List<HistoryEntity> historyEntities = historyRepository.getHistoryByMemberNo(memberNo);
        model.addAttribute("histories", historyEntities);
        return "member/history_list";
    }


    // @Author : 김정진
    // @Date : 2022-02-15 ~ 2022-02-16
    // room_view.html 에서 member_payment.html 로 넘어가는 맵핑
    // 결제하려는 회원의 정보와 클래스 정보를 member_payment.html 에 전달하는 메소드입니다.
    @GetMapping("/memberPaymentController")
    public String memberPaymentController(Model model,
                                          @RequestParam("roomNo") int roomNo,
                                          @RequestParam("roomDate") String roomDate,
                                          @RequestParam("roomTime") String roomTime,
                                          @RequestParam("person") int person,
                                          @RequestParam("price") int price) {

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
        } else {
            return "redirect : /index";
        }

        RoomEntity roomEntity = null;
        if (roomRepository.findById(roomNo).isPresent()) {
            roomEntity = roomRepository.findById(roomNo).get();
        }

        // 인수가 많아서 dto 를 새로 만들어서 넘깁니다.
        RoomPaymentDto roomPaymentDto = RoomPaymentDto.builder()
                .roomDate(roomDate)
                .roomTime(roomTime)
                .person(person)
                .price(price)
                .build();

        model.addAttribute("info", roomPaymentDto);
        model.addAttribute("member", memberEntity);
        model.addAttribute("room", roomEntity);

        return "member/room_payment";
    }

    // [회원 예약 내역 페이지와 맵핑]
    // @Param memberNo : 회원 번호를 넘겨받는다.
    @GetMapping("/reservationListController")
    public String reservationListController(Model model) {

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
        // 로그인 세션에 저장되어 있는 세션을 이용해 memberNo 를 불러옵니다.
        int memberNo = loginDto.getMemberNo();
        // memberNo 에 해당하는 예약 내역을 불러옵니다.
        List<HistoryEntity> historyEntities = historyRepository.getHistoryByMemberNo(memberNo);
        System.out.println(historyEntities);
        model.addAttribute("histories", historyEntities);
        return "member/history_list";
    }

    // [내가 개설한 클래스와 맵핑]
    @GetMapping("/myclass")
    public String myclass(Model model) {

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

        // 로그인 세션에 저장되어 있는 세션을 이용해 memberNo 를 불러옵니다.
        assert loginDto != null;
        int memberNo = loginDto.getMemberNo();

        List<RoomEntity> roomDtos = roomService.getMyGongbang(memberNo);
        model.addAttribute("roomDtos", roomDtos);

        return "member/member_class";
    }

    // [내가 예약한 클래스 날짜에 대한 정보를 달력에 뿌려주기 위한 메소드]
    @GetMapping("/reservation")
    @ResponseBody
    public String reservationList(Model model) {

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

        int memberNo = loginDto.getMemberNo();

        // memberNo -> history -> timetable -> roomDate 순서로 접근해야한다.
        StringBuilder str = new StringBuilder();
        // 1. memberNo 사용해 History 엔티티를 List 형태로 호출한다.
        List<HistoryEntity> historyEntities = historyRepository.getHistoryByMemberNo(memberNo);
        // 2. history 엔티티와 맵핑되어있는 timetable 엔티티를 가져와서 roomDate 를 str 에 담는다.
        for (HistoryEntity history : historyEntities) {
            str.append(history.getTimeTableEntity().getRoomDate()).append(",");
        }
        return str.toString();
    }

    // [내가 예약한 RoomEntity 에 관한 정보를 캘린더에 뿌려주기 위한 메소드]
    // model 값을 history_item.html 에 넘겨서 해당 html 을 뿌려준다.
    @GetMapping("/memberHistoryJSON")
    public String getRoomEntityByMemberNo(@RequestParam("date") String date, Model model) {

        HttpSession session = request.getSession();
        MemberDto loginDto = (MemberDto) session.getAttribute("logindto");
        int memberNo = loginDto.getMemberNo();

        // memberNo 를 사용해서 History 엔티티를 불러옵니다.
        List<HistoryEntity> historyEntities = historyRepository.getHistoryByMemberNo(memberNo);

        // HistoryEntity 에는 memberEntity, roomEntity, timeTableEntity 가 모두 맵핑되어있습니다.

        // 특정 날짜에 개설된 강의 시간 정보만 불러옵니다.
        List<TimeTableEntity> timeTableEntities = timeTableRepository.getTimeTableByRoomDate(date);

        List<HistoryEntity> historyList = new ArrayList<>();

        for (TimeTableEntity timeTableEntity : timeTableEntities) {
            for (HistoryEntity historyEntity : historyEntities) {
                // 연관관계로 맵핑되어 있기 때문에 for 문을 2번 돌며 특정 날짜에 대한 예약 정보만을 전달합니다.
                if (historyEntity.getTimeTableEntity().getTimeTableNo() == timeTableEntity.getTimeTableNo()) {
                    historyList.add(historyEntity);
                }
            }
        }
        model.addAttribute("histories", historyList);
        return "member/history_item";
    }

    // [메시지 페이지와 맵핑]
    @GetMapping("/msg")
    public String msg(Model model) {

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

        model.addAttribute("rooms", roomService.getmyroomlist());
        model.addAttribute("notes", roomService.getmynotelist());
        return "member/member_msg";
    }

    // [쪽지 쓰기]
    @GetMapping("/notereplywrite")
    @ResponseBody
    public String notereplywrite(@RequestParam("noteNo") int noteNo,
                                 @RequestParam("noteReply") String noteReply) {

        roomService.notereplywrite(noteNo, noteReply);

        return "1";
    }

    // [정산 페이지 맵핑]
    @GetMapping("/calculate")
    public String calculate(Model model) {

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

        // 내가 만든 강좌에 예약한 [MemberEntity] 를 출력하는 것에 목표입니다.

        List<MemberEntity> memberEntities = null;

        // 1. 특정 회원이 개설한 클래스를 전부 불러옵니다.
        assert loginDto != null;
        List<RoomEntity> roomEntities = roomRepository.findMyGongbang(loginDto.getMemberNo());
        List<HistoryEntity> historyEntities = historyRepository.findAll();
        List<MemberEntity> memberAsCustomer = new ArrayList<MemberEntity>();

        Page<MemberEntity> memberWithPage = new Page<MemberEntity>() {
            @Override
            public int getTotalPages() {
                return 0;
            }

            @Override
            public long getTotalElements() {
                return 0;
            }

            @Override
            public <U> Page<U> map(Function<? super MemberEntity, ? extends U> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public int getNumberOfElements() {
                return 0;
            }

            @Override
            public List<MemberEntity> getContent() {
                return null;
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }
            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<MemberEntity> iterator() {
                return null;
            }
        };

        for (RoomEntity roomEntity : roomEntities) {
            for (HistoryEntity historyEntity : historyEntities) {
                if (roomEntity.getRoomNo() == historyEntity.getRoomEntity().getRoomNo()) {
                    // 1. 내가 개설한 공방을 예약한 사람
                    int myCustomerNo = historyEntity.getMemberEntity().getMemberNo();

                    memberAsCustomer.add(memberRepository.findById(historyEntity.getMemberEntity().getMemberNo()).get());
                    // memberWithPage.add();
                }
            }
        }
        model.addAttribute("customers", memberAsCustomer);
        return "member/calculate_page";
    }

    // 02-15 채널 정보 출력 - 조지훈
    @GetMapping("/channel/{memberNo}")
    public String channel(@PathVariable("memberNo") int memberNo, Model model) {

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
        // 02-17 조지훈
        String realimg = null;
        if (memberEntity.getChannelImg() != null) {
            realimg = memberEntity.getChannelImg().split("_")[1];
        }
        model.addAttribute("realimg", realimg);
        model.addAttribute("memberEntity", memberEntity);
        return "member/channel";
    }

    // 02-15 채널 정보 등록  - 조지훈
    @PostMapping("/channelregistration")
    public String channelregistration(@RequestParam("memberNo") int memberNo,
                                      @RequestParam("channelContent") String channelContent,
                                      @RequestParam("channelTitle") String channelTitle,
                                      @RequestParam("memberImg") MultipartFile file) {
        try {
            String uuidfile = null; // 02-17 조지훈
            if (!file.getOriginalFilename().equals("")) { // 02-17 조지훈
                UUID uuid = UUID.randomUUID();
                uuidfile = uuid.toString() + "_" + file.getOriginalFilename().replaceAll("_", "-"); // 02-17 조지훈
                // String dir = "C:\\gongbang\\build\\resources\\main\\static\\channelimg";
                String dir = "C:\\gongbang-jj\\out\\production\\resources\\static\\channelimg";
                String filepath = dir + "\\" + uuidfile;
                file.transferTo(new File(filepath));
            }
            memberService.channelregistration(
                    MemberEntity.builder().memberNo(memberNo).channelTitle(channelTitle).channelContent(channelContent).channelImg(uuidfile).build());
        } catch (Exception e) {
            System.out.println(e);
        }
        return "redirect:/member/channel/" + memberNo;
    }

    // 02-17 채널 정보 수정시 기존이미지 삭제버튼 - 조지훈
    @PostMapping("/channelimgdelete")
    @ResponseBody
    public String channelimgdelete(@RequestParam("memberNo") int memberNo) {
        boolean result = memberService.channelimgdelete(memberNo);
        if (result) {
            return "1";
        } else {
            return "2";
        }
    }

    // 02-17 강사소개 작성여부 체크 - 조지훈
    @GetMapping("/channelcheck")
    @ResponseBody
    public String channelcheck(@RequestParam("memberNo") int memberNo) {
        boolean result = memberService.channelcheck(memberNo);
        if (result) {
            return "1";
        } else {
            return "2";
        }
    }

    // 충전소 페이지 맵핑
    @GetMapping("/member_payment")
    public String payment(Model model) {

        // 1. 로그인 세션 호출
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


        return "member/member_payment";
    }


    // 충전 처리 컨트롤러
    @GetMapping("/paymentcontroller")
    @ResponseBody
    public String paymentcontroller(@RequestParam("totalpay") int totalpay) {
        HttpSession session = request.getSession();
        MemberDto loginDto = (MemberDto) session.getAttribute("logindto");
        int memberNo = loginDto.getMemberNo();
        MemberEntity member = memberService.getMemberEntity(loginDto.getMemberNo());

        int memberPoint = member.getMemberPoint();

        boolean result = memberService.payment(memberNo, memberPoint, totalpay);
        if (result) {
            return "1";
        } else {
            return "2";
        }

    }

}
