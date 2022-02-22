package com.ezen.domain.dto;

import com.ezen.domain.entity.RoomEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarDTO {

    String year;
    String month;
    String date;
    // 해당 날짜에 등록된 강의
    RoomEntity roomEntity;


}
