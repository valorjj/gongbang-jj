package com.ezen.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "roomlike")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RoomLikeEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="roomLikeNo")
    private int roomLikeNo;

    @Column(name="roomLikeStatus")
    private int roomLikeStatus;

    // 회원 : 좋아요 = 1 : 1
    @ManyToOne
    @JoinColumn(name = "memberNo")
    private MemberEntity memberEntity;

    // 강의 : 좋아요 = 1 : 1
    @ManyToOne
    @JoinColumn(name = "roomNo")
    private RoomEntity roomEntity;

    @ManyToOne
    @JoinColumn(name = "roomLikeEntity")
    private RoomLikeEntity roomLikeEntity;

}
