package com.ezen.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "replyimg")
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "replyEntity")
@Setter
@Builder
@Getter
public class ReplyImgEntity extends BaseTimeEntity{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "replyImgNo")
    private int replyImgNo;

    // 이미지경로
    @Column(name = "replyImg")
    private String replyImg;

    // 리플 관계
    @ManyToOne
    @JoinColumn(name = "replyNo")
    private ReplyEntity replyEntity;
}
