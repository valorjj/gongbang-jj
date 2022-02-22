package com.ezen.domain.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RoomPaymentDto {

    private String roomDate; // 클래스 개설 날짜(YYYY-MM-DD)
    private String roomTime; // 클래스 개설 시간(HH, HH)
    private int person; // 신청한 사람
    private int price; // 지불해야할 금액
}
