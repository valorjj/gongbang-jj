package com.ezen.domain.entity.repository;

import com.ezen.domain.entity.RoomImgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomImgRepository extends JpaRepository<RoomImgEntity, Integer> {

    // 1. roomNo 사용해서 맵핑된 이미지 리스트 타입으로 출력하기
    @Query(nativeQuery = true, value="SELECT * FROM roomimg WHERE roomNo = :roomNo")
    List<RoomImgEntity> getImagesByRoomNo(@Param("roomNo") int roomNo);

    // 2. roomNo 에 해당하는 이미지 지우기
    @Modifying
    @Query(nativeQuery = true, value="DELETE FROM roomimg WHERE roomNo = :roomNo")
    void removeRoomImgByRoomNo(@Param("roomNo") int roomNo);

}
