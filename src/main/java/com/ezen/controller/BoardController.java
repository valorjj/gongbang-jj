package com.ezen.controller;

import com.ezen.domain.dto.MemberDto;
import com.ezen.domain.entity.BoardEntity;
import com.ezen.domain.entity.CategoryEntity;
import com.ezen.domain.entity.MemberEntity;
import com.ezen.domain.entity.PostEntity;
import com.ezen.domain.entity.repository.MemberRepository;
import com.ezen.service.BoardService;
import com.ezen.service.CategoryService;
import com.ezen.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    BoardService boardService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    PostService postService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private MemberRepository memberRepository;

    // [게시판 페이지와 맵핑]
    // 카테고리, 게시판을 Model 에 담아서 넘겨준다.
    @GetMapping("/list")
    public String list(Model model, @PageableDefault Pageable pageable) {

        HttpSession session = request.getSession();
        MemberDto loginDto = (MemberDto) session.getAttribute("logindto");
        MemberEntity memberEntity = null;
        if (loginDto != null) {
            if(memberRepository.findById(loginDto.getMemberNo()).isPresent())
                memberEntity = memberRepository.findById(loginDto.getMemberNo()).get();
            // [로그인이 되어있는 상태]
            assert memberEntity != null;
            if (memberEntity.getChannelImg() == null) {
                // [채널에 등록된 이미지가 없는 경우]
                model.addAttribute("isLoginCheck", 1);
            } else {
                model.addAttribute("isLoginCheck", 2);
            }
            model.addAttribute("memberEntity", memberEntity);
        }



        // 1. 만들어진 카테고리 전체 호출
        Page<CategoryEntity> categories = categoryService.getCategoryList(pageable);
        // 1. 카테고리에 해당하는 게시판 목록을 가져온다
        // 1.
        List<BoardEntity> boards = boardService.getBoards();
        // model 에 담아서 html 로 보낸다
        model.addAttribute("boards", boards);
        model.addAttribute("categories", categories);
        return "board/board_list";
    }

    // [게시판 생성 컨트롤러]
    @GetMapping("/newBoardController")
    @ResponseBody
    public String newBoardController(@RequestParam("boardName") String boardName, @RequestParam("categoryName") String categoryName) {
        boolean result = boardService.createNewBoard(boardName, categoryName);
        if (result) {
            return "1";
        } else {
            return "2";
        }
    }


}

