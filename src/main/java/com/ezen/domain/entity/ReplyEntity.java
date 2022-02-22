package com.ezen.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reply")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReplyEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "replyNo")
    private int replyNo;

    @Column(name = "replyContent")
    private String replyContent;

    // 별점 추가
    @Column(name = "replyStar")
    private int replyStar;

    @ManyToOne
    @JoinColumn(name = "roomNo") // 해당 필드의 이름[컬럼 = 열 = 필드]
    private RoomEntity roomEntity;

    @ManyToOne
    @JoinColumn(name = "memberNo") // 해당 필드의 이름[컬럼 = 열 = 필드]
    private MemberEntity memberEntity;

    @OneToMany(mappedBy = "replyEntity", cascade = CascadeType.ALL)
    private final List<ReplyImgEntity> replyImgEntities = new ArrayList<>();


}
