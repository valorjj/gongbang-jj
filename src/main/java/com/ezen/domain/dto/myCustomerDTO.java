package com.ezen.domain.dto;

import com.ezen.domain.entity.HistoryEntity;
import lombok.*;
import org.hibernate.criterion.Projections;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class myCustomerDTO {

    private int memberNo;
    private String memberEmail;
    private String memberName;
    private String memberPhone;
    private String memberGender;
    private int memberPoint;
    private String memberId;
    private String memberGrade;
    private int historyNo;
    private String phoneNumber;
    private int historyPoint;


}
