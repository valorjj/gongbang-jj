package com.ezen.domain.entity.repository;

import com.ezen.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository <MemberEntity, Integer> {

    // 엔티티 검색 findby필드명
    Optional<MemberEntity> findBymemberId(String memberId);
    // 멤버 No 찾기
//    Optional<MemberEntity> findBymemberNo(String memberNo);
    //이메일 검사
    Optional<MemberEntity> findBymemberEmail(String memberEmail);
}
