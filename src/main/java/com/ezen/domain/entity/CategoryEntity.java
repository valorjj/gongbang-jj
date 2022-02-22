package com.ezen.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CategoryEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryNo")
    private int categoryNo;

    // 각각의 게시판이 가질 카테고리
    @Column(name = "categoryName")
    private String categoryName;

    // 회원과 1:N 관계를 맺는다.
    // 게시판은 관리자(관리자 포함)가 승인한 사람만이 개설, 수정, 삭제할 수 있다.
    @ManyToOne
    @JoinColumn(name = "memberNo")
    private MemberEntity memberEntity;

    // 카테고리 : 게시판 = 1 : N 관계를 맺는다.
    @OneToMany(mappedBy = "categoryEntity")
    @ToString.Exclude
    private final List<BoardEntity> boardEntities = new ArrayList<>();


}
