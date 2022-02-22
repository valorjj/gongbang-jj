package com.ezen.domain.entity.repository;

import com.ezen.domain.entity.RoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends JpaRepository<RoomEntity, Integer> {

    // 1. 특정 사용자가 개설한 공방만 탐색색
    @Query(nativeQuery = true, value = "SELECT * FROM room WHERE memberNo = :memberNo")
    List<RoomEntity> findMyGongbang(@Param("memberNo") int memberNo);

    // 5. roomDate 로 Room 엔티티 찾기
    @Query(nativeQuery = true, value = "SELECT * FROM room WHERE roomDate = :roomDate")
    List<RoomEntity> findRoomByRoomDate(@Param("roomDate") String roomDate);

    // 6. 특정 roomStatus 에 해당하는 Room 엔티티 찾기
    @Query(nativeQuery = true, value = "SELECT * FROM room WHERE roomStatus = :roomStatus")
    Page<RoomEntity> findRoomByRoomStatus(@Param("roomStatus") String roomStatus, Pageable pageable);

    // 7. roomStatus, roomNo 로 Room 엔티티 조회하기
    @Query(nativeQuery = true, value = "SELECT * FROM room WHERE roomStatus = :roomStatus AND roomNo = :roomNo")
    RoomEntity findRoomByStatusAndNo(@Param("roomStatus") String roomStatus, @Param("roomNo") int roomNo);

    // [관리자가 사용하는 쿼리문]

    // 1. 검색 X 인 경우
    // 1. 검색 X 지역 O 카테고리 X
    @Query(nativeQuery = true, value = "SELECT * FROM room WHERE roomLocal = :roomLocal")
    Page<RoomEntity> adminGetRoomByLocal(@Param("roomLocal") String roomLocal, Pageable pageable);

    // 2. 검색 X 지역 X 카테고리 O
    @Query(nativeQuery = true, value = "SELECT * FROM room WHERE roomCategory = :roomCategory")
    Page<RoomEntity> adminGetRoomByCategory(@Param("roomCategory") String roomCategory, Pageable pageable);

    // 3. 검색 X 지역 O 카테고리 O
    @Query(nativeQuery = true, value = "SELECT * FROM room WHERE roomCategory = :category AND roomLocal = :local")
    Page<RoomEntity> adminGetRoomByCategoryAndLocal(@Param("local") String local, @Param("category") String category, Pageable pageable);


    // 2. 검색이 존재하는 경우

    // 1. 검색 O 지역 X 카테고리 X
    // 1. 쿼리문
    String keywordOnly = "SELECT * FROM room "
            + "WHERE roomTitle like %:keyword% OR "
            + "roomContent like %:keyword% OR "
            + "roomStatus like %:keyword% OR "
            + "roomNo like %:keyword% OR "
            + "roomCategory like %:keyword% OR "
            + "roomLocal like %:keyword% OR "
            + "roomPrice like %:keyword% OR "
            + "roomMax like %:keyword%";

    @Query(nativeQuery = true, value = keywordOnly)
    Page<RoomEntity> adminGetRoomByKeyword(@Param("keyword") String keyword, Pageable pageable);


    // 2. 검색 O 지역 O 카테고리 X
    // 2.1 쿼리문
    String keywordAndLocal = "SELECT * FROM room "
            + "WHERE roomLocal = :local OR "
            + "roomTitle like %:keyword% OR "
            + "roomContent like %:keyword% OR "
            + "roomStatus like %:keyword% OR "
            + "roomNo like %:keyword% OR "
            + "roomCategory like %:keyword% OR "
            + "roomLocal like %:keyword% OR "
            + "roomPrice like %:keyword% OR "
            + "roomMax like %:keyword%";

    @Query(nativeQuery = true, value = keywordAndLocal)
    Page<RoomEntity> adminGetRoomByKeywordAndLocal(@Param("keyword") String keyword, @Param("local") String local, Pageable pageable);

    // 3. 검색 O 지역 X 카테고리 O
    // 3.1 쿼리문
    String keywordAndCategory = "SELECT * FROM room "
            + "WHERE roomCategory = :category OR "
            + "roomTitle like %:keyword% OR "
            + "roomContent like %:keyword% OR "
            + "roomStatus like %:keyword% OR "
            + "roomNo like %:keyword% OR "
            + "roomCategory like %:keyword% OR "
            + "roomLocal like %:keyword% OR "
            + "roomPrice like %:keyword% OR "
            + "roomMax like %:keyword%";

    @Query(nativeQuery = true, value = keywordAndCategory)
    Page<RoomEntity> adminGetRoomByKeywordAndCategory(@Param("keyword") String keyword, @Param("category") String category, Pageable pageable);

    // 4. 검색 O 지역 O 카테고리 O
    // 4.1 쿼리문
    String keywordAndLocalAndCategory = "SELECT * FROM room "
            + "WHERE roomCategory = :category AND "
            + "roomLocal = :local OR "
            + "roomTitle like %:keyword% OR "
            + "roomContent like %:keyword% OR "
            + "roomStatus like %:keyword% OR "
            + "roomNo like %:keyword% OR "
            + "roomCategory like %:keyword% OR "
            + "roomLocal like %:keyword% OR "
            + "roomPrice like %:keyword% OR "
            + "roomMax like %:keyword%";

    @Query(nativeQuery = true, value = keywordAndLocalAndCategory)
    Page<RoomEntity> adminGetRoomByKeywordAndLocalAndCategory(@Param("keyword") String keyword, @Param("category") String category, @Param("local") String local, Pageable pageable);
}
