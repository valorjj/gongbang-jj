package com.ezen.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

    /*
    * [역할 부여]
    * @Author : 김정진
    * @Date : 2022-02-12
    *
    * 1. 회원가입 시 부여되는 계급 : MEMBER
    * - MEMBER 등급 회원의 접근 권한
    *   1. 메인 페이지
    *   2. 강의 상세 페이지
    *   3. 회원 정보
    * 2. 강좌 등록 시 : STAFF
    * - STAFF 등급 회원의 접근 권한
    *   1. 본인이 등록한 강의 상세 페이지에서 강좌 내용 수정 페이지와 연동
    *   2. 본인이 등록한 강의 열람 가능
    *   3. 본인이 등록했던 강의 수강생 통계 페이지 접근 가능
    *   4. 본인이 등록한 강의 정산 페이지 맵핑
    *   5. 본인의 강의에 등록한 수강생 목록 열람 가능 (아이디, 메일, 번호만 노출)
    *   6. roomStatus 변경 가능 : '마감' (정원 다 차지 않을 때에도 조기 마감 할 수 있게)
    * 3. 홈페이지 관리자 : ADMIN
    * - 편의성을 위해 DB 에서 회원 가입 후 강제로 ROLE 을 MEMBER 에서 ADMIN 으로 수정한다.
    * - ADMIN 등급 회원의 접근 권한
    *   1. roomStatus 변경 : '승인대기' '승인완료' '마감' '종료'
    *   2. 사이트 전체 통계 페이지 접근 : 강의 개설 현황, 수강 신청 현황, 가입한 회원 통계 등등 (*자유롭게 변경 가능)
    *
    * */

    ADMIN("ROLE_ADMIN", "관리자"), MEMBER("ROLE_MEMBER", "일반회원"), STAFF("ROLE_STAFF", "스태프");
    private final String key;
    private final String role;
}
