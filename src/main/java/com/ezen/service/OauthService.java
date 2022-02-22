package com.ezen.service;

import com.ezen.domain.dto.MemberDto;
import com.ezen.domain.dto.Oauth2Dto;
import com.ezen.domain.entity.MemberEntity;
import com.ezen.domain.entity.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;

@Service
public class OauthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private MemberRepository memberRepository;

    @Override // 소셜 로그인후 회원정보 가져오기 메소드
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);  // perperties에 요청 uri로부터  인증, 토큰, 회원정보 등등

        // 회원정보 속성 가져오기
        String nameattributekey = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // 클라이언트 아이디 가져오기
        String registrationid = userRequest.getClientRegistration().getRegistrationId();

        // DTO
        Oauth2Dto oauth2Dto = Oauth2Dto.of(registrationid, nameattributekey, oAuth2User.getAttributes());
        // DB 저장
        MemberEntity memberEntity = saveorupdate(oauth2Dto);
        // 세션 할당
        // 소셜 로그인시 id가 없기 때문에 이메일에서 @뒤를 제거한 아이디를 세션에 담기
        String snsid = memberEntity.getMemberEmail().split("@")[0];
        MemberDto loginDto = MemberDto.builder().memberId(snsid).memberNo(memberEntity.getMemberNo()).build();
        HttpSession session = request.getSession();   // 서버내 세션 가져오기
        session.setAttribute("logindto", loginDto);    // 세션 설정

        // if 채널이미지 있으면 채널이미지 로컬에 저장된 경로 부여
        // if 없으면


        // 계층화
        // 캡슐화


        // 리턴 ( 회원정보와 권한[키] )
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(memberEntity.getRolekey())),
                oauth2Dto.getAttribute(),
                oauth2Dto.getNameattributekey());
    }


    // 동일한 이메일이 있을경우 업데이트 동일한 이메일 없으면 저장
    public MemberEntity saveorupdate(Oauth2Dto oauth2Dto) {
        // 1. memberRepository 이용한 동일한 이메일찾기. [ findBy필드명 -> 반환타입 : Optional
        MemberEntity memberEntity = memberRepository.findBymemberEmail(oauth2Dto.getEmail())
                .map(entity -> entity.update(oauth2Dto.getName()))
                // map( 임시객체명 => 임시객체명.메소드) : 동일한 이메일이 있을 경우 => 특정 이벤트 수정
                .orElse(oauth2Dto.toentity());    // orElse(  )  : 동일한 이메일이 없을경우 dto->entity
        return memberRepository.save(memberEntity);
    }
}
