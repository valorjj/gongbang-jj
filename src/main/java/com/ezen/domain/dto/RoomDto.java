package com.ezen.domain.dto;


import com.ezen.domain.entity.RoomEntity;

public class RoomDto {


    private int roomNo;
    private String roomTitle;
    private String roomCategory;
    private String roomContent;
    private String roomDate;
    private String roomAddress;
    private String roomStatus;
    private int roomMax;

    public RoomEntity toentity(){
        return RoomEntity.builder()
                .roomNo(this.roomNo)
                .roomTitle(this.roomTitle)
                .roomCategory(this.roomCategory)
                .roomContent(this.roomContent)
                .roomAddress(this.roomAddress)
                .roomStatus(this.roomStatus)
                .roomMax(this.roomMax)
                .build();
    }




}
