package com.ezen.domain.dto;

import com.ezen.domain.entity.MemberEntity;
import com.ezen.domain.entity.Role;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.Map;


@Getter
@Setter
public class Oauth2Dto {

    // 닉네임
    String name;
    // 이메일
    String email;
    // 회원정보
    private Map<String, Object> attribute;
    // 요청 토큰[키]
    private String nameattributekey;

    // 풀생성자
    @Builder
    public Oauth2Dto(String name, String email, Map<String, Object> attribute, String nameattributekey) {
        this.name = name;
        this.email = email;
        this.attribute = attribute;
        this.nameattributekey = nameattributekey;
    }

    // 클라이언트 구분용[카카오 or 네이버 or 구글]
    public static Oauth2Dto of(String registrationid, String nameattributekey, Map<String, Object> attribute) {
        // 카카오
        if (registrationid.equals("kakao")) {
            return ofkakao(nameattributekey, attribute);
        }
        // 네이버
        else if (registrationid.equals("naver")) {
            return ofnaver(nameattributekey, attribute);
        }
        // 구글 등
        else {
            return ofgoogle(nameattributekey, attribute);
        }
    }

    // 카카오 정보 dto 변환 메소드
    private static Oauth2Dto ofkakao(String nameattributekey, Map<String, Object> attribute) {
        Map<String, Object> kakao_account = (Map<String, Object>) attribute.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakao_account.get("profile");
        return Oauth2Dto.builder()
                .name((String) profile.get("nickname"))
                .email((String) kakao_account.get("email"))
                .attribute(attribute)
                .nameattributekey(nameattributekey)
                .build();

    }

    // 네이버 정보 dto 변환 메소드
    private static Oauth2Dto ofnaver(String nameattributekey, Map<String, Object> attribute) {
        Map<String, Object> response = (Map<String, Object>) attribute.get("response");
        return Oauth2Dto.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .attribute(attribute)
                .nameattributekey(nameattributekey)
                .build();
    }

    // 구글 정보 dto 변환 메소드
    private static Oauth2Dto ofgoogle(String nameattributekey, Map<String, Object> attribute) {
        // 인증키                  ,  회원정보
        return Oauth2Dto.builder()
                .name((String) attribute.get("name")) // 구글 회원 이름
                .email((String) attribute.get("email")) // 구글 회원 이메일
                .attribute(attribute) // 구글 회원정보
                .nameattributekey(nameattributekey) // 구글 인증키
                .build();

    }


    // 첫 로그인시 회원가입 dto => entity변환 => DB
    public MemberEntity toentity() {
        return MemberEntity.builder()
                .memberName(this.name)
                .memberEmail(this.email)
                .memberId(email.split("@")[0])
                .memberGrade(Role.MEMBER).build();

    }
}