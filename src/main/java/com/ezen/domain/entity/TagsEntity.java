package com.ezen.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tags")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TagsEntity {

    // [태그]
    // 태그 고유 식별 번호
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tagNo")
    private int tagNo;

    // [태그 이름]
    // 오늘만무료, 직장인, 오전반, 오후반 등의 메타데이터
    @Column(name = "tagName")
    private String tagName;

    /*
    * room 엔티티와 @ManyToMany 관계를 맺어서 N : M 테이블 맵핑을 해야함
    * 하지만 실질적으로 테이블1- 테이블2 관계를 직접적으로 다대다 맵핑은 불가능. 오류 발생의 근원지
    * 그러니 room_tag 테이블을 중간에 하나 만들어서 고유 식별자만 fk 로 받는 맵핑 테이블을 새로 만들어야한다.
    * */



}
