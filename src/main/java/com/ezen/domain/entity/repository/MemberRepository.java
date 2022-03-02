package com.ezen.domain.entity.repository;

import com.ezen.domain.dto.myCustomerDTO;
import com.ezen.domain.entity.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Map;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {

    // 엔티티 검색 findby필드명
    Optional<MemberEntity> findBymemberId(String memberId);

    Optional<MemberEntity> findBymemberEmail(String memberEmail);

    // @Date : 2022-02-26
    // [개인 정산 페이지 : calculate.html]

    // roomNo 를 찾아서 member Entity 정보 찾기
    @Query(nativeQuery = true, value = "select R.memberNo from member M join room R on R.roomNo = :roomNo group by R.memberNo")
    Integer findMemberByRoomNo(@Param("roomNo") int roomNo);

    // [키워드가 존재하지 않는 경우] 특정 멤버가 개설한 클래스에 등록한 회원 내역 불러오기
    String myCustomerQuery1 = "select T.* from member T " +
            "where memberNo = " +
            "(select H.memberNo from member M " +
            "join history H on H.roomMadeBy = :memberNo group by H.memberNo)";

    @Query(nativeQuery = true, value = myCustomerQuery1)
    Page<MemberEntity> getMyCustomerList1(Pageable pageable, @Param("memberNo") int memberNo);

    // [키워드가 존재하지 않고]
    // [멤버가 개설한 특정 공방에 예약한 회원을]
    // 공방 고유 번호를 기준으로 가져오기
    String getMyCustomerQuery3 = "select T.* from member T "
            + "where memberNo = "
            + "(select H.memberNo from member M "
            + "join history H on H.roomMadeBy = :memberNo AND H.roomNo = :roomNo group by H.memberNo";

    @Query(nativeQuery = true, value = getMyCustomerQuery3)
    Page<MemberEntity> getMyCustomerList3(Pageable pageable, @Param("memberNo") int memberNo, @Param("roomNo") int roomNo);

    // [키워드가 존재하는 경우] 특정 멤버가 개설한 클래스에 등록한 회원 내역 불러오기
    // 특정 멤버가 개설한 클래스에 등록한 회원 내역 불러오기 : 전체 조회
    String myCustomerQuery2 = "select T.* from member T where memberNo = (select H.memberNo from member M join history H on H.roomMadeBy = :memberNo group by H.memberNo) AND "
            + "T.memberId like %:keyword% OR "
            + "T.memberPhone like %:keyword% OR "
            + "T.memberGender like %:keyword% OR "
            + "T.memberName like %:keyword%";

    @Query(nativeQuery = true, value = myCustomerQuery2)
    Page<MemberEntity> getMyCustomerList2(Pageable pageable, @Param("memberNo") int memberNo, @Param("keyword") String keyword);


//    // 특정 멤버가 개설한 클래스에 등록한 회원 내역 불러오기 : 전체 조회 [outer join 테스트 중]
//    // 아래 구조로는 실패, mysql 에서는 조회 확인, 스프링에서 쿼리문 쓰는 건 좀 더 학습이 필요해보입니다.
//    String myCustomerQuery2 = "select X.memberNo, X.memberEmail, X.memberName, X.memberPhone, " +
//            "X.memberGender, X.memberPoint, X.memberId, X.memberGrade, F.historyNo, F.phoneNumber, F.historyPoint from history as F " +
//            "left join " +
//            "(select T.* from member T " +
//            "where memberNo = (select H.memberNo from member M join history H on H.roomMadeBy = 1 group by H.memberNo)) X " +
//            "ON " +
//            "F.memberNo = X.memberNo";
//
//    // outer Join 사용해서 HistoryEntity 까지 한번에 담고 싶지만 Page<MemberEntity> 에 담을 수 없는게 당연한 것 같다.
//    // 그러니 History 정보를 담을 새로운 dto 를 만들어보자.
//    @Query(nativeQuery = true, value = myCustomerQuery)
//    Page<myCustomerDTO> getMyCustomerList2(Pageable pageable, @Param("memberNo") int memberNo);


    // 1. member 에 관한 정보 검색에 쓰일 쿼리문
    String memberSearchQuery = "SELECT * FROM member WHERE memberId = :keyword OR "
            + "memberNo = :keyword OR "
            + "memberPhone = :keyword OR "
            + "memberGender = :keyword OR "
            + "memberName = :keyword OR "
            + "channelTitle = :keyword OR "
            + "memberEmail = :keyword";

    @Query(nativeQuery = true, value = memberSearchQuery)
    Page<MemberEntity> findMemberBySearchKeyword(@Param("keyword") String keyword, Pageable pageable);


}
