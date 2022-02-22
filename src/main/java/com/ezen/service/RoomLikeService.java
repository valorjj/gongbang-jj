package com.ezen.service;


import com.ezen.domain.entity.MemberEntity;
import com.ezen.domain.entity.RoomEntity;
import com.ezen.domain.entity.RoomLikeEntity;
import com.ezen.domain.entity.repository.MemberRepository;
import com.ezen.domain.entity.repository.RoomLikeRepository;
import com.ezen.domain.entity.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class RoomLikeService {

    @Autowired
    RoomLikeRepository roomLikeRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    MemberRepository memberRepository;

    @Transactional
    public boolean roomLikeUpdate(int roomNo, int memberNo){

        // 1.동일한 방 / 작성자 일 경우 업데이트[ 좋아요 취소 -> 삭제 ]
            // 1. 모든 좋아요 가져오기
        List<RoomLikeEntity> roomLikeEntities = roomLikeRepository.findAll();

            // 2. 모든 좋아요 반복문
        for(RoomLikeEntity roomLike : roomLikeEntities){
            if( roomLike.getRoomEntity().getRoomNo() == roomNo && roomLike.getMemberEntity().getMemberNo() == memberNo  ){
                roomLikeRepository.delete(roomLike);
                return false;
            }
        }
        // 좋아요 등록하기
            // 1. 방번호의 엔티티 -> 클릭했을때 인수로 받은 roomNo 를 가지고 해당 엔티티 찾는다
        Optional<RoomEntity> optionalRoomEntity =  roomRepository.findById(roomNo);

         // 2. 등록자의 엔티티
        Optional<MemberEntity> optionalMemberEntity = memberRepository.findById(memberNo);
            // 3. 두개의 엔티티를 새로운 좋아요 엔티티에 저장한다 .
        RoomLikeEntity roomLikeEntity = RoomLikeEntity.builder()
                .memberEntity( optionalMemberEntity.get())
                .roomEntity( optionalRoomEntity.get() )
                .build();
            // 4. 새로운 좋아요 엔티티를 저장 .
        roomLikeRepository.save( roomLikeEntity );
        return true;
    }

}
