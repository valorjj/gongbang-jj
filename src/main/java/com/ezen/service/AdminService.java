package com.ezen.service;

import com.ezen.domain.entity.RoomEntity;
import com.ezen.domain.entity.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private RoomRepository roomRepository;

    // 1. 관리자 페이지 [검색이 존재하는 경우] Room 엔티티 출력
    public Page<RoomEntity> adminGetRoomBySearch(String keyword, String local, String category, @PageableDefault Pageable pageable) {
        // 1. 검색 X 지역 X 카테고리 X
        if (keyword.isEmpty() && local.isEmpty() && category.isEmpty()) {
            // 전체 등록된 Room 엔티티를 출력한다.
            return roomRepository.findAll(pageable);
        }
        // 검색, 지역, 카테고리 셋중 하나라도 존재하는 경우
        else {
            // 1. 검색 X
            if (keyword.isEmpty()) {
                // 1. 지역 O 카테고리 X
                if (category.isEmpty()) {
                    return roomRepository.adminGetRoomByLocal(local, pageable);
                }
                // 2. 지역 X 카테고리 O
                else if (local.isEmpty()) {
                    return roomRepository.adminGetRoomByCategory(category, pageable);
                }
                // 3. 지역 O 카테고리 O
                else {
                    return roomRepository.adminGetRoomByCategoryAndLocal(local, category, pageable);
                }
            }
            // 2. 검색 O
            else {
                // 1. 지역 O 카테고리 X
                if (local.isEmpty()) {

                    return roomRepository.adminGetRoomByLocal(local, pageable);

                }
                // 2. 지역 X 카테고리 O
                else if (category.isEmpty()) {
                    return roomRepository.adminGetRoomByLocal(local, pageable);

                }
                // 3. 지역 X 카테고리 X
                else {
                    return roomRepository.adminGetRoomByLocal(local, pageable);

                }
            }
        }
    }
}
