package com.ezen.controller;

import com.ezen.service.RoomLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RoomLikeController {
    @Autowired
    RoomLikeService roomLikeService;

    @GetMapping("/roomLike/roomLike")
    @ResponseBody
    public int roomLike(@RequestParam("roomNo") int roomNo, @RequestParam int memberNo) {

        boolean result = roomLikeService.roomLikeUpdate(roomNo, memberNo);
        if(result){
            return 1;
        } else{
            return 2;
        }

    }

}
