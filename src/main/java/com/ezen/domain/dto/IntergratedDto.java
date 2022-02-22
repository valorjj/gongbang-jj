package com.ezen.domain.dto;


import com.ezen.domain.entity.MemberEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
public class IntergratedDto implements UserDetails {
    // 일반회원 + oauth[소셜계정] => 통합 dto
    private int memberNo;
    private String memberId;
    private String memberPassword;
    private final Set<GrantedAuthority> authorities; // 인증
    // dto 생성자
    public IntergratedDto(MemberEntity memberEntity,
                          Collection< ? extends GrantedAuthority> authorities){
        this.memberId = memberEntity.getMemberId();
        this.memberPassword = memberEntity.getMemberPassword();
        this.memberNo = memberEntity.getMemberNo();
        this.authorities = Collections.unmodifiableSet(new LinkedHashSet<>(authorities));
        // this.authorities = Collections.unmodifiableSet(new LinkedHashSet<>(this.sortAuthorities(authorities)));
    }

//    // 인증 검색 속도를 위한 메소드
//    private Set<GrantedAuthority> sortAuthorities(Collection< ? extends GrantedAuthority> authorities){
//        SortedSet<GrantedAuthority> sortAuthorities = new TreeSet<>(Comparator.comparing(GrantedAuthority::getAuthority));
//        sortAuthorities.addAll(authorities);
//        return sortAuthorities;
//    }


    @Override // 회원의 패스워드를 반환하는 메소드
    public String getPassword() {
        return this.memberPassword;
    }

    @Override // 회원의 id반환 메소드
    public String getUsername() {
        return this.memberId;
    }

    @Override // 계정이 만료여부확인[true면 만료되지 않음] => false로 휴면계정으로 바뀔수 있음
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override // 계정이 잠겨 있는지 확인[true면 잠겨있지 않음]
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override // 계정의 패스워드가 만료여부 확인[true면 만료되지 않음]
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override // 계정이 사용가능한 계정인지 확인[true면 사용가능]
    public boolean isEnabled() {
        return true;
    }

}
