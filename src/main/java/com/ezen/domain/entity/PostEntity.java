package com.ezen.domain.entity;


import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PostEntity extends BaseTimeEntity {

    // [게시글 식별 번호]
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postNo")
    private int postNo;

    // [게시글 제목]
    // 작성자가 입력한 게시글 제목을 저장
    @Column(name = "postTitle")
    private String postTitle;

    // [게시글 내용]
    // summernote 를 활용해서 긴 글이 들어올 예정이기 때문에 longtext 로 지정해둔다.
    // 더 좋은 방법이 있다면 여기 메모
    @Column(name = "postContent", columnDefinition = "LONGTEXT")
    private String postContent;

    // [게시글이 부모인지 자식인지 구분]
    // depth = 0 : 부모
    // depth = 1 : 자식
    @Column(name = "postDepth")
    private int postDepth;

    // [게시글, 답글 순서]
    // 부모 -> order : 0
    // 자식 -> order : 1, 2, 3, ...
    @Column(name = "postOrder")
    private int postOrder;

    // [게시판 조회수]
    @Column(name = "postViewCount")
    private int postViewCount;

    // Board 엔티티와 1:N 맵핑
    @ManyToOne
    @JoinColumn(name = "boardNo")
    private BoardEntity boardEntity;

    // Post 테이블에 작성한 Member 정보가 있어야합니다.
    @ManyToOne
    @JoinColumn(name = "memberNo")
    private MemberEntity memberEntity;

    // 게시물 : 게시물 이미지 = 1 : N 관계
    // 게시물 엔티티 안에는 게시물 이미지 리스트가 맵핑되어 있어서
    // 게시물 하나를 호출하면, 해당 게시물에 등록된 리스트를 불러올 수 있습니다.
    @OneToMany(mappedBy = "postEntity", cascade = CascadeType.ALL)
    @ToString.Exclude
    private final List<PostImgEntity> postImgEntities = new ArrayList<>();

    // 게시물 : 댓글 = 1 : N 관계 맵핑
    // 게시물 하나에 댓글은 여러개 달릴 수 있습니다.
    @OneToMany(mappedBy = "postEntity", cascade = CascadeType.ALL)
    @ToString.Exclude
    private final List<PostReplyEntity> postReplyEntities = new ArrayList<>();

}
