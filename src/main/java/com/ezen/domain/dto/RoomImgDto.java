package com.ezen.domain.dto;

import com.ezen.domain.entity.RoomImgEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RoomImgDto {

    private int roomImgNo;
    private String roomImg;

    public RoomImgEntity toEntity(){
        return RoomImgEntity.builder()
                .roomImg(this.roomImg)
                .build();
    }

}
