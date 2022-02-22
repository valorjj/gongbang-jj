package com.ezen.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BoardEntity extends BaseTimeEntity {
    /*
     * 게시판 목록은 [관리자]만이 생성, 수정, 삭제가 가능합니다.
     * 각 게시판에 카테고리를 부여해서 분류합니다.
     * 예를 들어 가전, 애완동물, 화장품, 의류 등으로 대분류로 한번 나눕니다.
     * Board 아래 Post 테이블이 게시글이 작성되는 곳 입니다.
     * */

    // [게시판 식별 번호]
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "boardNo")
    private int boardNo;

    // [각각의 게시판 이름]
    @Column(name = "boardName")
    private String boardName;

    // 카테고리 : 게시판 = 1 : N 관계를 맵핑한다.
    @ManyToOne
    @JoinColumn(name = "categoryNo")
    private CategoryEntity categoryEntity;

    // 게시판 : 게시글 = 1 : N 관계를 맵핑한다.
    // 게시판이 삭제되면 게시글도 모두 삭제되도록 fk 관계 부여
    @OneToMany(mappedBy = "boardEntity", cascade = CascadeType.ALL)
    @ToString.Exclude
    private final List<PostEntity> postEntities = new ArrayList<>();


}
