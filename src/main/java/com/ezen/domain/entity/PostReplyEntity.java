package com.ezen.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "postReply")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PostReplyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postReplyNo")
    private int postReplyNo;

    // 작성된 댓글 내용
    @Column(name = "postReplyContent")
    private String postReplyContent;

    // 댓글 : 대댓글 구분
    // depth : 0 --> 부모 댓글
    // depth : 1, 2, ... --> 자식 댓글
    @Column(name = "postReplyDepth")
    private int postReplyDepth;

    // 댓글과 대댓글 순서
    @Column(name = "postReplyOrder")
    private int postReplyOrder;

    // 등록된 댓글 번호를 기준으로 group 화 하는 컬럼
    @Column(name = "postReplyTarget")
    private int postReplyTarget;

    // 멤버 : 댓글 = 1 : N 관계
    @ManyToOne
    @JoinColumn(name = "memberNo")
    private MemberEntity memberEntity;

    // 게시글 : 댓글 = 1 : N 관계
    @ManyToOne()
    @JoinColumn(name = "postNo")
    private PostEntity postEntity;
}
