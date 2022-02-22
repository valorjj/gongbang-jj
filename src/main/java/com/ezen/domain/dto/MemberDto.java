package com.ezen.domain.dto;

import com.ezen.domain.entity.MemberEntity;
import com.ezen.domain.entity.Role;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString
@Builder
public class MemberDto {
    // 필드
    private int memberNo;   // 회원번호
    /*
     * @Date : 2022-02-12
     * @Note :
     *   1. 기존에 없던 memberId 를 추가했습니다.
     *   2. 일반 회원은 로그인 시 아이디를 포함한 정보를 입력합니다.
     *   3. SNS 연동 로그인 회원은 email 만 입력합니다.
     *   4. 가입과 동시에 '이메일'@gmail.com 에 있는 '이메일' 부분을 아이디로 사용합니다.
     *   5.
     *
     *
     * */
    private String memberId; // 회원 가입 시 입력하는 아이디
    private String memberPassword; // 회원비밀번호
    private String memberName; // 회원이름
    private String memberGender; // 회원성별
    private String memberPhone; // 회원연락처
    private String memberEmail; // 회원이메일
    private Role memberGrade;
    private int memberPoint; // 회원포인트
    private LocalDateTime createdDate; // 회원가입 날짜
    private String channelTitle;
    private String channelContent;
    private String channelImg;

    // Dto -> entity
    public MemberEntity toentity(){
        return MemberEntity.builder()
                .memberNo(this.memberNo)
                .memberPassword(this.memberPassword)
                .memberId( this.memberId)
                .memberName( this.memberName)
                .memberGender( this.memberGender )
                .memberPhone( this.memberPhone)
                .memberEmail( this.memberEmail)
                .memberGrade(Role.MEMBER)
                .memberPoint(this.memberPoint)
                .build();
    }

}
