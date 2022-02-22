package com.ezen.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "history")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class HistoryEntity extends BaseTimeEntity {

    /*
     * 회원의 이전 수강 신청 내역, 댓글 남김 내역을 담는 엔티티
     * */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int historyNo;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "historyPoint")
    private int historyPoint;

    // 1명의 회원이 여러 클래스를 신청할 수 있다.
    @ManyToOne()
    @JoinColumn(name = "memberNo")
    private MemberEntity memberEntity;

    // 1개의 강의가 여러 신청 내역을 가질 수 있다.
    @ManyToOne()
    @JoinColumn(name = "roomNo")
    private RoomEntity roomEntity;

    // 타임 테이블 : 예약 내역 = 1 : N
    @ManyToOne()
    @JoinColumn(name = "timeTableNo")
    private TimeTableEntity timeTableEntity;

}
