package com.ezen.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {

    INPROCESS("STATUS_INPROCESS", "검토중"), APPROVED("STATUS_APPROVED", "승인완료"),
    REJECTED("STATUS_REJECT", "승인거부"), DELETED("STATUS_DELETED", "삭제처리");

    private final String key;
    private final String role;


}
