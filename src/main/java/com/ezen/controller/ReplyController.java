package com.ezen.controller;

import com.ezen.domain.entity.ReplyEntity;
import com.ezen.service.ReplyService;
import com.ezen.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/reply")
public class ReplyController {

    @Autowired

    ReplyService replyService;
    @Autowired
    RoomService roomService;

    @PostMapping("/writecontroller")
    public String writecontroller(ReplyEntity replyEntity,
                                  @RequestParam("file") List<MultipartFile> files,
                                  @RequestParam("roomNo") int roomNo, @RequestParam("review_star") int replyStar) {
        replyService.write(replyEntity, files, roomNo, replyStar);
        return "redirect:/room/view/" + roomNo;
    }


}
