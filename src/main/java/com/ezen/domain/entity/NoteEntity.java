package com.ezen.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "note")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class NoteEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="noteNo")
    private int noteNo; // 문의번호
    @Column(name="noteContents")
    private String noteContents; // 문의 내용
    @Column(name="noteReply")
    private String noteReply;// 문의 답변
    @Column(name="noteRead")
    private int noteRead; // 0이면 안읽음 / 1이면 읽음

    @ManyToOne
    @JoinColumn(name="memberNo")
    private MemberEntity memberEntity; // 보낸사람

    @ManyToOne
    @JoinColumn(name="roomNo")
    private RoomEntity roomEntity; // 문의방 + 받는사람






}