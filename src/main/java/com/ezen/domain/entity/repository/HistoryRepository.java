package com.ezen.domain.entity.repository;

import com.ezen.domain.entity.HistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistoryRepository extends JpaRepository<HistoryEntity, Integer> {
    // 1. memberNo 를 사용해 특정 회원의 예약 내역을 가져오기
    @Query(nativeQuery = true, value ="SELECT * FROM history WHERE memberNo = :memberNo")
    List<HistoryEntity> getHistoryByMemberNo(@Param("memberNo") int memberNo);

    // 2. 특정 roomNo 을 사용해 해당 강의 예약 내역 호출
    @Query(nativeQuery = true, value ="SELECT * FROM history WHERE roomNo = :roomNo")
    List<HistoryEntity> getHistoryByRoomNo(@Param("roomNo") int roomNo);


}
