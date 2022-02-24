package com.ezen.domain.entity.repository;

import com.ezen.domain.entity.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {

    // 엔티티 검색 findby필드명
    Optional<MemberEntity> findBymemberId(String memberId);

    // 멤버 No 찾기
//    Optional<MemberEntity> findBymemberNo(String memberNo);
    //이메일 검사
    Optional<MemberEntity> findBymemberEmail(String memberEmail);

    // 개인 정산 페이지에 사용할 쿼리문s


    String memberSearchQuery = "SELECT * FROM member WHERE memberId = :keyword OR "
            + "memberNo = :keyword OR "
            + "memberPhone = :keyword OR "
            + "memberGender = :keyword OR "
            + "memberName = :keyword OR "
            + "channelTitle = :keyword OR "
            + "memberEmail = :keyword";


    @Query(nativeQuery = true, value = memberSearchQuery)
    Page<MemberEntity> findMemberBySearchKeyword(@Param("keyword") String keyword);

    /*String findMyCustomer = "SELECT * FROM "*/



}
