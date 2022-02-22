package com.ezen.domain.entity.repository;

import com.ezen.domain.entity.TimeTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TimeTableRepository extends JpaRepository<TimeTableEntity, Integer> {

    // 1. 가장 최근 9개의 강좌 목록을 호출합니다.
    // 1.1 중복은 허용하지 않습니다.
    // 1.2 중복이 있을 수도 있기 때문에 limit 9 는 사용하지 않습니다.
    // 1.2.1 service 에서 제어
    @Query(nativeQuery = true, value = "SELECT * FROM timetable ORDER BY createdDate DESC")
    List<TimeTableEntity> getByTimeSequence();

    // 2. roomNo 에 해당하는 TimeTable 엔티티를 List 형태로 리턴
    @Query(nativeQuery = true, value = "SELECT * FROM timetable WHERE roomNo = :roomNo")
    List<TimeTableEntity> getTimeTableByRoomNo(@Param("roomNo") int roomNo);

    // 3. roomDate 로 정렬된 결과를 얻는다.
    @Query(nativeQuery = true, value = "SELECT * FROM timetable ORDER BY roomDate ASC")
    List<TimeTableEntity> getTimeTableOrderByRoomDate();

    // 4. roomDate 에 해당하는 값만 가져옵니다.
    @Query(nativeQuery = true, value = "SELECT * FROM timetable WHERE roomDate = :roomDate")
    List<TimeTableEntity> getTimeTableByRoomDate(@Param("roomDate") String roomDate);

}
