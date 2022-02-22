package com.ezen.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RoomEntity extends BaseTimeEntity {

    // [클래스 고유 식별 번호]
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "roomNo")
    private int roomNo;

    // [클래스 명]
    @Column(name = "roomTitle")
    private String roomTitle;

    // [클래스 카테고리(베이킹, 음악, 등등 분류)]
    @Column(name = "roomCategory")
    private String roomCategory;

    // [클래스 설명]
    @Column(name = "roomContent")
    private String roomContent;

    // [클래스 상세 설명]
    @Column(name = "roomDetail", columnDefinition = "LONGTEXT")
    private String roomDetail;

    // [클래스 주소]
    // [도로명 주소],[위도],[경도]
    // 위 조합으로 한꺼번에 db에 저장합니다.
    @Column(name = "roomAddress")
    private String roomAddress;

    // 클래스 지역 설정
    @Column(name = "roomLocal")
    private String roomLocal;

    // [클래스 상태]
    // 0 : 승인 대기 중
    // 1 : 승인, 모집 중
    // 2 : 시작하지 않았지만 정원이 꽉 찼음
    // 3 : 지정된 시간이 끝나서 더 이상 참여 및 수정이 불가능함
    @Column(name = "roomStatus")
    private String roomStatus;

    // [클래스 정원]
    // 개발자 마음대로 1명~6명으로 제한을 걸겠습니다.
    @Column(name = "roomMax")
    private int roomMax;

    // [클래스 등록 시 선택하는 체크박스 정보]
    // 좀 더 추가할 예정입니다 (현재 3개)
    @Column(name = "roomETC")
    private String roomETC;

    // 강의 조회수
    @Column(name = "roomView")
    private int roomView;

    // 강의 가격
    @Column(name = "roomPrice")
    private int roomPrice;

    // 개설된 클래스의 평균 후기 점수
    @Column(name = "roomAvg")
    private float roomAvg;

    // 회원번호 관계
    @ManyToOne
    @JoinColumn(name = "memberNo") // 해당 필드의 이름[컬럼 = 열 = 필드]
    private MemberEntity memberEntity;

    // timetable 과의 관계
    @OneToMany(mappedBy = "roomEntity", cascade = CascadeType.ALL)
    @ToString.Exclude
    private final List<TimeTableEntity> timeTableEntity = new ArrayList<>();

    // 이미지 관계
    @OneToMany(mappedBy = "roomEntity", cascade = CascadeType.ALL)
    @ToString.Exclude
    private final List<RoomImgEntity> roomImgEntities = new ArrayList<>();

    // 문의글 리스트
    @OneToMany(mappedBy = "roomEntity", cascade = CascadeType.ALL)
    @ToString.Exclude
    private final List<NoteEntity> noteEntities = new ArrayList<>();

    @OneToMany(mappedBy = "roomEntity", cascade = CascadeType.ALL)
    @ToString.Exclude
    private final List<ReplyEntity> replyEntities = new ArrayList<ReplyEntity>();

    // 클래스 1개는 여러개의 예약 내역을 가질 수 있습니다.
    @OneToMany(mappedBy = "roomEntity", cascade = CascadeType.ALL)
    @ToString.Exclude
    private final List<HistoryEntity> historyEntities = new ArrayList<>();

    @OneToMany(mappedBy = "roomEntity", cascade = CascadeType.ALL)
    @ToString.Exclude
    private final List<RoomLikeEntity> roomLikeEntities = new ArrayList<>();
}
