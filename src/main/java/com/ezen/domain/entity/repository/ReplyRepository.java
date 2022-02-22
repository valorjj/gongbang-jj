package com.ezen.domain.entity.repository;

import com.ezen.domain.entity.ReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReplyRepository extends JpaRepository<ReplyEntity, Integer> {

    // 1. roomNo 에 해당하는 댓글만 List 형태로 반환시킵니다.
    @Query(nativeQuery = true, value ="select * from reply where roomNo = :roomNo")
    List<ReplyEntity> getReplyByRoomNo(@Param("roomNo") int roomNo);
}
