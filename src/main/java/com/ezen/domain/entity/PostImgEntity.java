package com.ezen.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "postImage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PostImgEntity extends BaseTimeEntity {

    // 이미지 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postImgNo")
    private int postImgNo;

    // 등록된 이미지 경로
    @Column(name = "postImg")
    private String postImg;

    // 게시물 : 게시물 이미지 = 1 : N 관계를 가진다.
    // 게시물 하나에 여러개의 이미지가 등록될 수 있다.
    @ManyToOne
    @JoinColumn(name = "postNo")
    private PostEntity postEntity;
}
