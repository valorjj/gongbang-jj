package com.ezen.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class MemberEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberNo")
    private int memberNo;

    @Column(name = "memberEmail")
    private String memberEmail;

    @Column(name = "memberPassword")
    private String memberPassword;

    @Column(name = "memberName")
    private String memberName;

    @Column(name = "memberPhone")
    private String memberPhone;

    @Column(name = "memberGender")
    private String memberGender;

    @Column(name = "memberPoint")
    private int memberPoint;

    @Column(name = "memberId")
    private String memberId;

    // @Date : 2022-02-16 채널 관련 정보 추가
    @Column(name = "channelTitle")
    private String channelTitle;

    @Column(name = "channelContent")
    private String channelContent;

    @Column(name = "channelImg")
    private String channelImg;


    @Enumerated(EnumType.STRING)
    @Column
    private Role memberGrade; // 회원등급

    public String getRolekey() {
        return this.memberGrade.getKey();
    }

    // oauth2에서 동일한 이메일이면 업데이트 처리 메소드
    public MemberEntity update(String name) {
        this.memberName = name;
        return this;
    }

    // 문의 리스트
    @OneToMany(mappedBy = "memberEntity")
    @ToString.Exclude
    private final List<NoteEntity> noteEntities = new ArrayList<>();


    // 멤버 : 공방개설 = 1 : N
    @OneToMany(mappedBy = "memberEntity", cascade = CascadeType.ALL)
    @ToString.Exclude
    private final List<RoomEntity> roomEntities = new ArrayList<>();

    // 멤버 : 후기 = 1 : N
    @OneToMany(mappedBy = "memberEntity", cascade = CascadeType.ALL)
    @ToString.Exclude
    private final List<ReplyEntity> replyEntities = new ArrayList<>();

    // 멤버 : 예약 = 1 : N
    @OneToMany(mappedBy = "memberEntity", cascade = CascadeType.ALL)
    @ToString.Exclude
    private final List<HistoryEntity> historyEntities = new ArrayList<>();

    // 멤버 : 좋아요 = 1 : N
    @OneToMany(mappedBy = "memberEntity", cascade = CascadeType.ALL)
    @ToString.Exclude
    private final List<RoomLikeEntity> roomLikeEntities = new ArrayList<>();

    // 멤버 게시글 = 1 : N
    @OneToMany(mappedBy = "memberEntity", cascade = CascadeType.ALL)
    @ToString.Exclude
    private final List<PostEntity> postEntities = new ArrayList<>();

    // 멤버 : 댓글 = 1 : N
    @OneToMany(mappedBy = "memberEntity", cascade = CascadeType.ALL)
    @ToString.Exclude
    private final List<PostReplyEntity> postReplyEntities = new ArrayList<>();

    // [스태프] [관리자] 등급은 새로운 카테고리를 생성할 수 있다.
    // 멤버 : 카테고리 = 1 : N
    @OneToMany(mappedBy = "memberEntity")
    @ToString.Exclude
    private final List<CategoryEntity> categoryEntities = new ArrayList<>();


}
