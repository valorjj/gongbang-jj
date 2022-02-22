package com.ezen.controller;

import com.ezen.domain.dto.MemberDto;
import com.ezen.domain.entity.*;
import com.ezen.domain.entity.repository.MemberRepository;
import com.ezen.domain.entity.repository.PostReplyRepository;
import com.ezen.service.BoardService;
import com.ezen.service.CategoryService;
import com.ezen.service.MemberService;
import com.ezen.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;

@Controller
@RequestMapping(value = "/community")
public class CommunityController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BoardService boardService;

    @Autowired
    private PostService postService;

    @Autowired
    private PostReplyRepository postReplyRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    // 커뮤니티 탭 컨트롤러

    // [게시판 사이드 바 출력]
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
        // 2. 카테고리에 해당하는 게시판 목록을 가져온다
        List<BoardEntity> boards = boardService.getBoards();
        // model 에 담아서 html 로 보낸다
        model.addAttribute("boards", boards);
        model.addAttribute("categories", categories);
        return "member/community";

    }

    // [특정 게시판 클릭 시 작성된 게시글을 리스트로 출력]
    @GetMapping("/postListController")
    public String postListController(Model model,
                                     @RequestParam("boardNo") int boardNo,
                                     @PageableDefault Pageable pageable) {

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

        // 작성된 PostEntity 를 board_content.html 에 뿌려준다.
        Page<PostEntity> postEntities = postService.getPostList(boardNo, pageable);

        model.addAttribute("posts", postEntities);
        model.addAttribute("boardNo", boardNo);

        return "community/board_content";
    }

    // 게시물 작성하기 페이지 맵핑
    // 카테고리 -> 게시글 리스트 -> 게시글 작성
    // 등록 후 해당 게시판 리스트로 이동 (boardNo)
    @GetMapping("createPost")
    public String createPost(Model model,
                             @RequestParam("boardNo") int boardNo) {

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


        model.addAttribute("boardNo", boardNo);
        return "community/create_post";

    }

    // 게시글 작성 컨트롤러
    // 등록 후 리스트로 이동시킨다.
    @PostMapping("/createPostController")
    public String createPostController(Model model,
                                       @RequestParam("boardNo") int boardNo,
                                       @RequestParam("community-post-img-input") List<MultipartFile> files,
                                       @RequestParam("create-post-title") String title,
                                       @RequestParam("post-content") String content,
                                       @PageableDefault Pageable pageable) {

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


        PostEntity postEntity = PostEntity.builder()
                .postTitle(title)
                .postContent(content)
                .postViewCount(0)
                .postOrder(0)
                .postDepth(0)
                .build();

        // 1. 유효성 검사를 js 에서 처리한다면, 등록에 오류가 생기지 않는다.
        postService.createPost(postEntity, files, boardNo);

        // 2. 작성된 PostEntity 를 board_content.html 에 뿌려준다.
        Page<PostEntity> postEntities = postService.getPostList(boardNo, pageable);

        model.addAttribute("posts", postEntities);
        model.addAttribute("boardNo", boardNo);

        return "community/board_content";

    }

    // 1. 상세 페이지와 맵핑
    // 1.1 postNo 를 인수로 전달받고, 상세 페이지를 출력한다.
    @GetMapping("/viewPost")
    public String viewPost(@RequestParam("postNo") int postNo, Model model,
                           @PageableDefault Pageable pageable,
                           @RequestParam("boardNo") int boardNo) {

        // 1. 현재 로그인 된 회원 정보 호출한 뒤, model 에 담기
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

        // 2. view_post.html 에 Model 값을 뿌려준 다음 html 을 출력한다.
        PostEntity postEntity = postService.getPostEntity(postNo);
        model.addAttribute("post", postEntity);

        // 3. 등록된 댓글을 model 에 추가시킨다.
        Page<PostReplyEntity> postReplyEntities = postReplyRepository.getParentReply(pageable, postNo);
        model.addAttribute("replies", postReplyEntities);

        // 4. 대댓글을 호출해서 model 에 주입한다.
        List<PostReplyEntity> postChildReplyEntities = postReplyRepository.getChildReply(postNo);
        model.addAttribute("childReplies", postChildReplyEntities);

        // 5. 뒤로가기 버튼을 위해 boardNo 를 넘깁니다.
        model.addAttribute("boardNo", boardNo);

        return "community/view_post";
    }

    // 1. 댓글 등록 컨트롤러
    // 1.1 따로 service 를 만들지 않고 바로 repo 에 접근한다.
    @GetMapping("/newPostReply")
    @Transactional
    public String newPostReply(Model model, @RequestParam("postNo") int postNo, @RequestParam("content") String content, @PageableDefault Pageable pageable) {

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

        // 2. 전달받은 인수를 DB 에 저장한다.
        PostReplyEntity postReplyEntity = PostReplyEntity
                .builder()
                .postReplyContent(content)
                .postReplyDepth(0)
                .postReplyOrder(0)
                .build();

        // 3. 내용, Depth, Order 값을 설정한 뒤 DB 에 저장한다.
        int postReplyNo = postReplyRepository.save(postReplyEntity).getPostReplyNo();

        // 4. DB 에 등록된 엔티티를 호출한다.
        PostReplyEntity savedPostReplyEntity = null;
        if (postReplyRepository.findById(postReplyNo).isPresent()) {
            savedPostReplyEntity = postReplyRepository.findById(postReplyNo).get();
        }

        // 4.1 postNo 에 해당하는 PostEntity, MemberEntity 를 주입한다.
        PostEntity postEntity = postService.getPostEntity(postNo);

        assert savedPostReplyEntity != null;
        savedPostReplyEntity.setPostEntity(postEntity);
        savedPostReplyEntity.setMemberEntity(memberEntity);
        savedPostReplyEntity.setPostReplyTarget(savedPostReplyEntity.getPostReplyNo());

        // 5. PostEntity 에 댓글을 추가시킨다.
        postEntity.getPostReplyEntities().add(savedPostReplyEntity);

        model.addAttribute("postNo", postNo);

        // 3. 등록된 댓글을 model 에 추가시킨다.
        Page<PostReplyEntity> postReplyEntities = postReplyRepository.getParentReply(pageable, postNo);
        model.addAttribute("replies", postReplyEntities);

        // 4. 대댓글을 호출해서 model 에 주입한다.
        List<PostReplyEntity> postChildReplyEntities = postReplyRepository.getChildReply(postNo);
        model.addAttribute("childReplies", postChildReplyEntities);

        model.addAttribute("memberEntity", memberEntity);


        // 6. null 체크를 거쳐서 여기까지 도달했으니, "1" 을 리턴한다.
        return "community/post_reply";

    }

    // 1. 대댓글 등록 컨트롤러
    @GetMapping("/childReply")
    @ResponseBody
    @Transactional
    public String childReply(@RequestParam("replyNo") int replyNo,
                             @RequestParam("content") String content,
                             @RequestParam("postNo") int postNo) {

        // 1. 현재 로그인 된 회원 정보 호출
        HttpSession session = request.getSession();
        MemberDto loginDto = (MemberDto) session.getAttribute("logindto");
        MemberEntity memberEntity = memberService.getMemberEntity(loginDto.getMemberNo());

        // 2. 댓글이 등록될 PostEntity 호출
        PostEntity postEntity = postService.getPostEntity(postNo);

        // 3. 대댓글 등록
        PostReplyEntity parentReplyEntity = null;
        if (postReplyRepository.findById(replyNo).isPresent()) {
            parentReplyEntity = postReplyRepository.findById(replyNo).get();
        }

        assert parentReplyEntity != null;
        int parentReplyDepth = parentReplyEntity.getPostReplyDepth();
        int parentReplyOrder = parentReplyEntity.getPostReplyOrder();

        PostReplyEntity postReplyChildEntity = PostReplyEntity
                .builder()
                .postReplyContent(content)
                .postReplyTarget(parentReplyEntity.getPostReplyNo())
                .build();


        int depth = postReplyRepository.getReplyDepthByTargetNo(parentReplyEntity.getPostReplyNo());

        postReplyChildEntity.setPostReplyDepth(depth);
        postReplyChildEntity.setPostReplyOrder(depth);
        postReplyChildEntity.setMemberEntity(memberEntity);
        postReplyChildEntity.setPostEntity(postEntity);

        int savedPostReplyChildNo = postReplyRepository.save(postReplyChildEntity).getPostReplyNo();

        PostReplyEntity savedChildReplyEntity = null;

        if (postReplyRepository.findById(savedPostReplyChildNo).isPresent()) {
            savedChildReplyEntity = postReplyRepository.findById(savedPostReplyChildNo).get();
        }

        memberEntity.getPostReplyEntities().add(savedChildReplyEntity);
        postEntity.getPostReplyEntities().add(savedChildReplyEntity);

        return "1";
    }


}
