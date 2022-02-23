package com.ezen.service;

import com.ezen.domain.dto.MemberDto;
import com.ezen.domain.entity.*;
import com.ezen.domain.entity.repository.ReplyImgRepository;
import com.ezen.domain.entity.repository.ReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ReplyService {

    @Autowired
    ReplyRepository replyRepository;

    @Autowired
    ReplyImgRepository replyImgRepository;

    @Autowired
    HttpServletRequest request;

    @Autowired
    MemberService memberService;

    @Autowired
    RoomService roomService;

    public boolean write(ReplyEntity replyEntity, List<MultipartFile> files, int roomNo, int replyStar) {

        HttpSession session = request.getSession();
        MemberDto loginDto = (MemberDto) session.getAttribute("logindto");

        MemberEntity memberEntity = memberService.getMember(loginDto.getMemberNo());
        replyEntity.setMemberEntity(memberEntity);

        RoomEntity roomEntity = roomService.getroom(roomNo);
        replyEntity.setRoomEntity(roomEntity);

        // 별점 추가
        replyEntity.setReplyStar(replyStar);

        int replyNo = replyRepository.save(replyEntity).getReplyNo();
        ReplyEntity replyEntitysaved = replyRepository.findById(replyNo).get();

        memberEntity.getReplyEntities().add(replyEntitysaved);
        roomEntity.getReplyEntities().add(replyEntitysaved);

        // 파일처리
        String uuidfile = null;

        if (files.size() != 0) {
            for (MultipartFile file : files) {
                UUID uuid = UUID.randomUUID();
                uuidfile = uuid.toString() + "_" + Objects.requireNonNull(file.getOriginalFilename()).replaceAll("_", "-");

                // 인텔리 전용 경로
                // String dir = "C:\\gongbang\\build\\resources\\main\\static\\replyimg";
                // 리눅스 경로
                String dir = "C:\\gongbang-jj\\out\\production\\resources\\static\\replyimg";

                String filepath = dir + "\\" + uuidfile;
                try {
                    file.transferTo(new File(filepath));
                } catch (Exception e) {
                    System.out.println("파일 저장 실패 : " + e);
                }

                ReplyImgEntity replyImgEntity = ReplyImgEntity.builder()
                        .replyImg(uuidfile)
                        .replyEntity(replyEntitysaved)
                        .build();
                int replyImgNo = replyImgRepository.save(replyImgEntity).getReplyImgNo();
                replyEntitysaved.getReplyImgEntities().add(replyImgRepository.findById(replyImgNo).get());

            }
        } else {

            ReplyImgEntity replyImgEntity = ReplyImgEntity.builder()
                    .replyEntity(replyEntitysaved)
                    .build();

            int replyImgNo = replyImgRepository.save(replyImgEntity).getReplyImgNo();
            replyEntitysaved.getReplyImgEntities().add(replyImgRepository.findById(replyImgNo).get());
        }

        return true;
    }

    public List<ReplyEntity> replylist() {
        return replyRepository.findAll();
    }
}