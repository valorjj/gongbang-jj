package com.ezen.service;

import com.ezen.domain.dto.MemberDto;

import com.ezen.domain.entity.MemberEntity;
import com.ezen.domain.entity.RoomEntity;
import com.ezen.domain.entity.RoomImgEntity;
import com.ezen.domain.entity.repository.*;
import com.ezen.domain.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoomService {

    @Autowired
    private RoomImgRepository roomImgRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private MemberService memberService;
    @Autowired
    private TimeTableRepository timeTableRepository;
    @Autowired
    private NoteRepository noteRepository;

    @Transactional
    public boolean registerClass(RoomEntity roomEntity,
                                 List<MultipartFile> files) {
        // 1. 등록하려는 회원 번호 : 세션 정보
        HttpSession session = request.getSession();
        MemberDto memberDto = (MemberDto) session.getAttribute("logindto");
        MemberEntity memberEntity = memberService.getMember(memberDto.getMemberNo());
        // 2. room entity 에 member entity 를 추가시킨다.
        roomEntity.setMemberEntity(memberEntity);
        // 3. room entity 저장 후 roomNo 를 가져온다.
        int roomNo = roomRepository.save(roomEntity).getRoomNo();
        // 4. member entity 에 room entity 저장
        RoomEntity roomEntitySaved = null;
        if (roomRepository.findById(roomNo).isPresent()) {
            roomEntitySaved = roomRepository.findById(roomNo).get();
        }
        // 4.1 member entity 에 방금 저장된 room entity 를 저장시킨다.
        // 4.2 memberEntity 에는 @OneToMany 형태로 맵핑되어있다.
        // 4.3 member 1명이 여러개의 room 을 등록할 수 있고, 등록할 시 맵핑을 시켜주는 역할이다.
        memberEntity.getRoomEntities().add(roomEntitySaved);
        // 5. 이미지 처리
        String uuidfile = null;
        if (files.size() != 0) {
            for (MultipartFile file : files) {
                // 1. 난수 + '_' + 파일이름
                UUID uuid = UUID.randomUUID();
                uuidfile = uuid.toString() + "_" + Objects.requireNonNull(file.getOriginalFilename()).replaceAll("_", "-");
                // 2. 저장될 경로
                // 2.1 수업 때 배웠던 방식은 프로젝트에 올리는 것[현재 작업폴더]
                // 2.2 Spring 은 Tomcat 이 내장 서버라서, 실행할 때 마다 경로가 바뀐다. (내부적으로 로테이션을 돌면서)

                // 인텔리전용
                // String dir = "C:\\gongbang\\build\\resources\\main\\static\\roomimg";
                // 리눅스 전용
                String dir = "/home/ec2-user/gongbang/build/resources/main/static/roomimg";

                // 3. 저장될 파일의 전체 [현재는 절대]경로
                // 3.1 프로젝트 경로를 맞춘다.
                String filepath = dir + "/" + uuidfile;

                try {
                    // 4. 지정한 경로에 파일을 저장시킨다.
                    file.transferTo(new File(filepath));
                } catch (Exception e) {
                    System.out.println("오류 : " + e);
                }
                // 5.entity 에 파일 경로를 저장한다.
                // 5.1 roomEntity 에 room entity 를 주입해야한다.
                RoomImgEntity roomImgEntity = RoomImgEntity.builder()
                        .roomImg(uuidfile)
                        .roomEntity(roomEntitySaved)
                        .build();
                // 6. 각각의 파일을 repo 를 통해 db에 저장한다.
                // 6.1 해당하는 파일의 roomImgNo 를 통해 해당하는 이미지를 불러온다.
                int roomImgNo = roomImgRepository.save(roomImgEntity).getRoomImgNo();
                RoomImgEntity roomImgEntitySaved = roomImgRepository.findById(roomImgNo).get();
                // 6.2 각각의 이미지를 room entity 에 선언된 list 에 저장시킨다.
                assert roomEntitySaved != null;
                roomEntitySaved.getRoomImgEntities().add(roomImgEntitySaved);
            }
        } else {
            // 1. 첨부한 파일이 존재하지 않는 경우
            // 2. js 에서 검사해서 무조건 사진 파일을 등록하도록 합니다.
        }
        return true;
    }

    // 내가 만든 room list 가져오기
    public List<RoomEntity> getmyroomlist() {
        HttpSession session = request.getSession();
        MemberDto logindto = (MemberDto) session.getAttribute("logindto");
        List<RoomEntity> roomEntities = memberRepository.findById(logindto.getMemberNo()).get().getRoomEntities();
        return roomEntities;
    }

    // 'MEMBER' 권한에서 검색한 결과
    // '승인완료' 된 클래스만 리턴해야합니다.
    // header.html 에서 검색한 결과를 db 에서 받아오는 메소드
    // @Param keyword : 검색창 입력값
    // @Param local : 검색창에서 선택한 지역
    // @Param category : 검색창에서 선택한 카테고리
    public Page<RoomEntity> getRoomEntityBySearch(@PageableDefault Pageable pageable, String keyword, String local, String category) {

        //페이지번호
        int page = 0;
        if (pageable.getPageNumber() != 0) {
            page = pageable.getPageNumber() - 1;
        }
        // 페이지 속성[PageRequest.of(페이지번호, 페이지당 게시물수, 정렬기준)]
        pageable = PageRequest.of(page, 2, Sort.by(Sort.Direction.DESC, "roomNo")); // 변수 페이지 10개 출력

        // 1.1 검색이 없는 경우
        if (keyword.isEmpty()) {
            // 1.2 검색 X 지역 O 카테고리 X
            if (!local.isEmpty() && category.isEmpty()) {
                return roomRepository.adminGetRoomByLocal(local, pageable);
            }
            // 1.3 검색 X 지역 X 카테고리 X
            else if (local.isEmpty() && category.isEmpty()) {
                return roomRepository.findAll(pageable);
            }
            // 1.3 검색 X 지역 X 카테고리 O
            // 더 줄일 수 있지만 혼선이 있을 수 있어 길게 나열해둡니다.
            else if (local.isEmpty() && !category.isEmpty()) {
                return roomRepository.adminGetRoomByCategory(category, pageable);
            }
            // 1.4 검색 X 지역 O 카테고리 O
            else if (!local.isEmpty() && !category.isEmpty()) {
                return roomRepository.adminGetRoomByCategoryAndLocal(local, category, pageable);
            }
        }
        // 2. 검색이 있는 경우
        else {
            // 검색 O 지역 O 카테고리 X
            if (!local.isEmpty() && category.isEmpty()) {
                return roomRepository.adminGetRoomByKeywordAndLocal(keyword, local, pageable);
            }
            // 검색 O 지역 X 카테고리 O
            else if (local.isEmpty() && !category.isEmpty()) {
                return roomRepository.adminGetRoomByKeywordAndCategory(keyword, category, pageable);
            }
            // 검색 O 지역 O 카테고리 O
            else if (!local.isEmpty() && !category.isEmpty()) {
                return roomRepository.adminGetRoomByKeywordAndLocalAndCategory(keyword, category, local, pageable);
            }
            // 검색 O 지역 X 카테고리 X
            else if (local.isEmpty() && category.isEmpty()) {
                return roomRepository.adminGetRoomByKeyword(keyword, pageable);
            }
        }

        // 위의 경우 중 아무것도 해당하지 않는다면 null 리턴
        // 위 조건문을 모두 통과했다면 비정상적인 접근이라고 볼 수 있음
        // null 값에 대한 처리가 되어있는가?
        return null;
    }

    // 검색이 없는 경우
    public List<RoomEntity> getEveryRoomEntity() {
        return roomRepository.findAll();
    }

    // room 상세페이지
    public RoomEntity getroom(int roomNo) {
        return roomRepository.findById(roomNo).get();
    }

    // 모든 룸 가져오기(멤버용)
    // 사용자는 '승인완료' 처리된 클래스만을 볼 수 있습니다.
    public Page<RoomEntity> getroomlist(@PageableDefault Pageable pageable) {

        // 1. 룸 엔티티 Page 타입 변수 선언 및 초기화
        Page<RoomEntity> roomEntities = null;
        int page = -1;
        if (pageable.getPageNumber() == 0) {
            page = 0;
        } else {
            page = pageable.getPageNumber() - 1;
        }
        pageable = PageRequest.of(page, 4, Sort.by(Sort.Direction.DESC, "roomNo")); // 변수 페이지 10개 출력

        // 1. 승인 완료된 클래스만 가져와야합니다.
        roomEntities = roomRepository.findRoomByRoomStatus("승인완료", pageable);
        return roomEntities;

    }

    // [멤버용]
    // 내가 개설한 강좌 불러오기
    public List<RoomEntity> getMyGongbang(int memberNo) {

        HttpSession session = request.getSession();
        MemberDto loginDto = (MemberDto) session.getAttribute("logindto");

        // 1. 룸 엔티티 Page 타입 변수 선언 및 초기화
        List<RoomEntity> myGongbangs = null;
        myGongbangs = roomRepository.findMyGongbang(memberNo);

        return myGongbangs;

    }

    // 모든 룸 가져오기(어드민용)
    // 관리자는 모든 클래스를 볼 수 있습니다.
    public Page<RoomEntity> getroomlistadmin(@PageableDefault Pageable pageable) {

        // 1. 룸 엔티티 Page 타입 변수 선언 및 초기화
        Page<RoomEntity> roomEntities = null;

        int page = -1;
        if (pageable.getPageNumber() == 0) {
            page = 0;
        } else {
            page = pageable.getPageNumber() - 1;
        }
        pageable = PageRequest.of(page, 4, Sort.by(Sort.Direction.DESC, "roomNo")); // 변수 페이지 10개 출력

        // 1. 승인 완료된 클래스만 가져와야합니다.
        roomEntities = roomRepository.findAll(pageable);
        return roomEntities;

    }

    // 룸에 날짜, 시간 지정하기
    @Transactional
    public boolean registerTimeToClass(TimeTableEntity timeTableEntity, int roomNo) {
        if (roomRepository.findById(roomNo).isPresent()) {
            RoomEntity roomEntity = roomRepository.findById(roomNo).get();
            timeTableEntity.setRoomEntity(roomEntity);
            // 정원은 따로 선택받지 않고 처음에 선택했던 그대로
            // 바꾸려면 다 바꿔야해서 번거로워서 일단 이대로 둡니다.
            timeTableEntity.setRoomMax(roomEntity.getRoomMax());
            // room 엔티티에 timeTableEntity 추가
            roomEntity.getTimeTableEntity().add(timeTableEntity);
            // room 리스트에 room 을 추가
            // 작성된 시간 엔티티를 db 에 추가시킨다.
            timeTableRepository.save(timeTableEntity);
            return true;
        } else {
            return false;
        }
    }

    // 메인 화면에 등록된 강좌 출력
    // 가장 최근에 강의가 개설된 강좌 6개만 출력합니다.
    // 아니 그냥 대칭으로 보기 이쁘게 9개 출력합니다.
    public List<RoomEntity> getRoomEntityInMain() {
        // 1. 가장 최근에 등록한 강좌를 TableEntity 에서 빼옵니다.
        List<TimeTableEntity> roomEntities = timeTableRepository.getByTimeSequence();
        // 2. RoomEntity 를 저장하는 리스트를 생성해서 집어넣습니다. 9개가 되면 종료 !
        int count = 0;
        return null;
    }

    // 특정 룸 삭제
    public boolean delete(int roomNo) {
        roomRepository.delete(roomRepository.findById(roomNo).get());
        return true;
    }

    // 특정 룸 상태변경
    // '검토중' '승인중' '승인완료' '승인거부'
    @Transactional
    public boolean activeupdate(int roomNo, String upactive) {

        RoomEntity roomEntity = roomRepository.findById(roomNo).get(); // 엔티티 호출
        if (roomEntity.getRoomStatus().equals(upactive)) {
            // 선택 버튼의 상태와 기존 룸 상태가 동일하면 업데이트X
            return false;
        } else {
            roomEntity.setRoomStatus(upactive);
            return true;
        }
    }

    // 로그인 된 회원이 등록한 문의 출력
    public List<NoteEntity> getmynotelist() {
        HttpSession session = request.getSession();
        MemberDto memberDto = (MemberDto) session.getAttribute("logindto");
        MemberEntity memberEntity = memberService.getMemberEntity(memberDto.getMemberNo());
        return memberEntity.getNoteEntities();

    }

    //문의 등록
    public boolean notewrite(int roomNo, String noteContents) {
        //로그인된 회원정보를 가져온다[작성자]
        HttpSession session = request.getSession();
        MemberDto memberDto = (MemberDto) session.getAttribute("logindto");
        // 만약에 로그인이 되어 있지 않으면
        if (memberDto == null) {
            return false; // 등록실패
        }

        // 문의 엔티티 생성
        NoteEntity noteEntity = new NoteEntity();
        noteEntity.setNoteContents(noteContents); // 작성내용
        noteEntity.setMemberEntity(memberService.getMemberEntity(memberDto.getMemberNo())); // 작성자 엔티티
        noteEntity.setRoomEntity(roomRepository.findById(roomNo).get()); // 방엔티티
        // 문의 엔티티 저장
        int NoteNo = noteRepository.save(noteEntity).getNoteNo();
        // 해당 룸 엔티티의 문의 리스트에 문의 엔티티 저장
        roomRepository.findById(roomNo).get().getNoteEntities().add(noteRepository.findById(NoteNo).get());
        // 해당 회원엔티티의 문의 리스트에 문의 엔티티 저장
        memberService.getMemberEntity(memberDto.getMemberNo()).getNoteEntities().add(noteRepository.findById(NoteNo).get());

        return true;
    }


    //답변등록
    @Transactional
    public boolean notereplywrite(int noteNo, String noteReply) {
        noteRepository.findById(noteNo).get().setNoteReply(noteReply);
        return true;
    }


    // 쪽지 카운트 세기 // nread : 0 읽지 않음 / 1 읽음
    // 모든페이지에서 쿠키나 세션으로 출력해야함. 굳이 반환타입을 사용할 필요 x
    public void nreadcount() {
        HttpSession session = request.getSession();
        MemberDto memberDto = (MemberDto) session.getAttribute("logindto");
        if (memberDto == null) {
        } else {
            int nreadcount = 0; // 안읽은 쪽지의 갯수
            // 로그인된 회원번호와 쪽지 받은 사람의 회원번호가 모두 동일하면
            for (NoteEntity noteEntity : noteRepository.findAll()) {
                if (noteEntity.getRoomEntity().getMemberEntity().getMemberNo() == memberDto.getMemberNo() && noteEntity.getNoteRead() == 0) { // 받는사람 == 로그인된 번호 && 읽음이 0이면
                    // 문의 엔티티. 방엔티티. 멤버엔티티. 멤버번호
                    nreadcount++;
                }
            }
            // 세션에 저장하기
            session.setAttribute("nreadcount", nreadcount);
        }

    }

    //읽음처리 서비스
    @Transactional // 업데이트처리에서 필수
    public boolean nreadupdate(int noteNo) {
        noteRepository.findById(noteNo).get().setNoteRead(1);
        return true;
    }

    // [관리자 권한]
    // @Author : 김정진
    // @Date : 2022-02-17
    // adminlist.html 에서 검색한 결과를 db 에서 받아오는 메소드
    // 일반회원은 승인완료 된 결과만을 볼 수 있다.
    // 관리자는 개설, 삭제, 승인거부 등등 모든 클래스를 볼 수 있다.
    // @Param keyword : 검색창 입력값
    // @Param local : 검색창에서 선택한 지역
    // @Param category : 검색창에서 선택한 카테고리
    public Page<RoomEntity> adminGetRoomEntityBySearch(@PageableDefault Pageable pageable, String keyword, String local, String category) {

        //페이지번호
        int page = 0;
        if (pageable.getPageNumber() != 0) {
            page = pageable.getPageNumber() - 1;
        }
        // 페이지 속성[PageRequest.of(페이지번호, 페이지당 게시물수, 정렬기준)]
        // 한 페이지에서 볼 개수를 변수로 입력받도록 수정 예정입니다.
        pageable = PageRequest.of(page, 30, Sort.by(Sort.Direction.DESC, "roomNo")); // 변수 페이지 10개 출력

        // 1.1 검색이 없는 경우
        if (keyword.equals("-1")) {
            // 1.2 검색 X 지역 O 카테고리 X
            if (!local.equals("-1") && category.equals("-1")) {
                System.out.println(" 검색 X 지역 O 카테고리 X ");
                return roomRepository.adminGetRoomByLocal(local, pageable);
            }
            // 1.3 검색 X 지역 X 카테고리 X
            else if (local.equals("-1") && category.equals("-1")) {
                System.out.println("검색 X 지역 X 카테고리 X ");
                return roomRepository.findAll(pageable);
            }
            // 1.4 검색 X 지역 X 카테고리 O
            // 더 줄일 수 있지만 혼선이 있을 수 있어 길게 나열해둡니다.
            else if (local.equals("-1") && !category.equals("-1")) {
                System.out.println("검색 X 지역 X 카테고리 O");
                return roomRepository.adminGetRoomByCategory(category, pageable);
            }
            // 1.5 검색 X 지역 O 카테고리 O
            else if (!local.equals("-1") && !category.equals("-1")) {
                System.out.println("검색 X 지역 O 카테고리 O");
                return roomRepository.adminGetRoomByCategoryAndLocal(local, category, pageable);
            }
        }
        // 2. 검색이 있는 경우
        else {
            // 검색 O 지역 O 카테고리 X
            if (!local.equals("-1") && category.equals("-1")) {
                return roomRepository.adminGetRoomByKeywordAndLocal(keyword, local, pageable);
            }
            // 검색 O 지역 X 카테고리 O
            else if (local.equals("-1") && !category.equals("-1")) {
                return roomRepository.adminGetRoomByKeywordAndCategory(keyword, category, pageable);
            }
            // 검색 O 지역 O 카테고리 O
            else if (!local.equals("-1") && !category.equals("-1")) {
                return roomRepository.adminGetRoomByKeywordAndLocalAndCategory(keyword, category, local, pageable);
            }
            // 검색 O 지역 X 카테고리 X
            else if (local.equals("-1") && category.equals("-1")) {
                return roomRepository.adminGetRoomByKeyword(keyword, pageable);
            }
        }

        // 위의 경우 중 아무것도 해당하지 않는다면 null 리턴
        // 위 조건문을 모두 통과했다면 비정상적인 접근이라고 볼 수 있음
        // null 값에 대한 처리가 되어있는가?
        return null;
    }

    // 룸 삭제 02-18 조지훈
    @Transactional
    public boolean roomdelete(int roomNo) {
        Optional<RoomEntity> entityOptional = roomRepository.findById(roomNo);
        if (entityOptional != null) {
            roomRepository.delete(entityOptional.get());
            return true;
        }
        return false;
    }

    // [공방 정보 수정]
    @Transactional
    public boolean updateClass(RoomEntity roomEntity, List<MultipartFile> files) {

        // 1. 등록하려는 회원 번호 : 세션 정보
        HttpSession session = request.getSession();
        MemberDto memberDto = (MemberDto) session.getAttribute("logindto");
        MemberEntity memberEntity = memberService.getMember(memberDto.getMemberNo());
        roomEntity.setMemberEntity(memberEntity);

        RoomEntity savedRoomEntity = null;
        int savedRoomNo = roomRepository.save(roomEntity).getRoomNo();
        if (roomRepository.findById(savedRoomNo).isPresent()) {
            savedRoomEntity = roomRepository.findById(savedRoomNo).get();
        }

        // 2. 이미지 처리
        String uuidfile = null;

        // 2.1 이미지가 존재하는 경우
        if (files.size() != 0) {
            // [이미지 처리]
            // 다수의 기존에 등록된 이미지를 불러올 마땅한 방법을 생각해야한다.
            int roomNo = roomEntity.getRoomNo();
            // roomNo 에 해당하는 등록된 이미지 모두 삭제
            roomImgRepository.removeRoomImgByRoomNo(roomNo);
            System.out.println("어떤 이미지가 들어가는지 체크 >>>>   ");
            // 새로 입력받은 파일 입력
            for (MultipartFile file : files) {
                System.out.println(file.toString());
                // 1. 난수 + '_' + 파일이름
                UUID uuid = UUID.randomUUID();
                uuidfile = uuid.toString() + "_" + Objects.requireNonNull(file.getOriginalFilename()).replaceAll("_", "-");
                // 2. 저장될 경로
                // >> 서버에 저장
                // String dir = "C:\\gongbang\\build\\resources\\main\\static\\roomimg";

                // 리눅스 경로
                String dir = "/home/ec2-user/gongbang/build/resources/main/static/roomimg";

                // 3. 저장될 파일의 전체 [현재는 절대]경로
                // 3.1 프로젝트 경로를 맞춘다.
                String filepath = dir + "/" + uuidfile;

                try {
                    file.transferTo(new File(filepath));
                } catch (Exception e) {
                    System.out.println("오류 : " + e);
                }
                RoomImgEntity roomImgEntity = RoomImgEntity.builder()
                        .roomImg(uuidfile)
                        .roomEntity(roomEntity)
                        .build();
                int roomImgNo = roomImgRepository.save(roomImgEntity).getRoomImgNo();
                RoomImgEntity roomImgEntitySaved = roomImgRepository.findById(roomImgNo).get();
                savedRoomEntity.getRoomImgEntities().add(roomImgEntitySaved);
            }
            return true;
        }
        return false;
    }

}
