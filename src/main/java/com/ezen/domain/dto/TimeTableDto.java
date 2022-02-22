package com.ezen.domain.dto;

import com.ezen.domain.entity.TimeTableEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TimeTableDto {

    // 식별 번호
    private int timeTableNo;
    // 강의 개설 날짜
    private String roomDate;
    // 강의 시작시간, 종료시간
    private String roomTime;

    public TimeTableEntity toEntity() {
        return TimeTableEntity.builder()
                .roomTime(this.roomTime)
                .roomDate(this.roomDate)
                .build();
    }

}
