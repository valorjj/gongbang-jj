package com.ezen.service;

import com.ezen.domain.dto.MemberDto;
import com.ezen.domain.entity.*;
import com.ezen.domain.entity.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostService {

    @Autowired
    private PostReplyRepository postReplyRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BoardService boardService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private PostImgRepository postImgRepository;


    // 작성된 게시물 리스트 불러오기
    public Page<PostEntity> getPostList(int boardNo,
                                        @PageableDefault Pageable pageable) {
        //페이지번호
        int page = 0;
        if (pageable.getPageNumber() != 0) {
            page = pageable.getPageNumber() - 1;
        }
        // 페이지 속성[PageRequest.of(페이지번호, 페이지당 게시물수, 정렬기준)]
        pageable = PageRequest.of(page, 4, Sort.by(Sort.Direction.DESC, "postNo")); // 변수 페이지 10개 출력
        return postRepository.findPostByBoardNo(boardNo, pageable);
    }

    // 작성된 게시물 엔티티 불러오기
    public PostEntity getPostEntity(int postNo) {
        Optional<PostEntity> postOptional = postRepository.findById(postNo);
        return postOptional.get();
    }

    // 게시글 등록하기
    @Transactional
    public void createPost(PostEntity post, List<MultipartFile> files, int boardNo) {

        HttpSession session = request.getSession();
        MemberDto memberDto = (MemberDto) session.getAttribute("logindto");

        // 1. postEntity 에 member, board 엔티티를 주입한다.
        MemberEntity memberEntity = memberService.getMemberEntity(memberDto.getMemberNo());
        BoardEntity boardEntity = boardService.getBoardEntity(boardNo);
        post.setMemberEntity(memberEntity);
        post.setBoardEntity(boardEntity);


        // 2. 엔티티를 db에 저장시킨다. [현재 db에 들어가지를 않는다. 대체 왜?]
        int postNo = postRepository.save(post).getPostNo();
        // DB 에 저장된 PostEntity 를 호출한다.
        PostEntity savedPostEntity = null;

        if (postRepository.findById(postNo).isPresent()) {
            savedPostEntity = postRepository.findById(postNo).get();
        }

        memberEntity.getPostEntities().add(savedPostEntity);
        boardEntity.getPostEntities().add(savedPostEntity);

        // 이미지 파일 저장
        String uuidfile = null;

        if (files.size() != 0) {
            for (MultipartFile file : files) {
                System.out.println("[개별 파일]" + file.toString());
                UUID uuid = UUID.randomUUID();
                uuidfile = uuid.toString() + "_" + Objects.requireNonNull(file.getOriginalFilename()).replaceAll("_", "-");

                String dir = "/home/ec2-user/gongbang/build/resources/main/static/postimg";
                String filepath = dir + "/" + uuidfile;

                try {
                    file.transferTo(new File(filepath));
                } catch (Exception e) {
                    System.out.println("오류 : " + e);
                }
                // 이미지 엔티티 빌더
                PostImgEntity postImgEntity = PostImgEntity.builder()
                        .postImg(uuidfile)
                        .postEntity(savedPostEntity)
                        .build();

                int postImgNo = postImgRepository.save(postImgEntity).getPostImgNo();
                PostImgEntity savedPostImgEntity = null;
                if (postImgRepository.findById(postImgNo).isPresent()) {
                    savedPostImgEntity = postImgRepository.findById(postImgNo).get();
                }
                assert savedPostImgEntity != null;
                assert savedPostEntity != null;
                savedPostEntity.getPostImgEntities().add(savedPostImgEntity);
            }
        }
    }

    // 게시물 삭제
    public boolean deletePost(int postNo) {
        Optional<PostEntity> post = postRepository.findById(postNo);
        if (post.isPresent()) {
            postRepository.delete(post.get()); // 게시물 삭제
            return true;
        } else {
            return false;
        }
    }

    // [댓글 등록]
    @Transactional
    public boolean replyWrite(String replyContent, @Lazy int postNo) {
        // 1. 로그인 세션을 호출해서 댓글 입력하는 회원 정보를 불러온다.
        HttpSession session = request.getSession();
        MemberDto loginDTO = (MemberDto) session.getAttribute("loginSession");
        // 2. 로그인 한 회원 엔티티는 member 객체에 담긴다.
        MemberEntity memberEntity = memberService.getMemberEntity(loginDTO.getMemberNo());
        // 2.1 작성하는 게시글 식별번호를 통해서 게시글 엔티티를 불러온다.
        PostEntity post = null;
        if (postRepository.findById(postNo).isPresent())
            post = postRepository.findById(postNo).get();
        // 3. Reply 엔티티를 builder 를 통해서 생성한다.
        // 3.1 부모 댓글은 order, depth 은 '0' 이고
        // 3.2 replyTarget 은 -1 값을 부여한다.
        PostReplyEntity postReplyEntity = PostReplyEntity.builder()
                .postReplyContent(replyContent)
                .postReplyOrder(0)
                .postReplyDepth(0)
                .postReplyTarget(-1)
                .build();
        // 3.3 reply 엔티티를 저장한다.
        // 3.4 저장된 reply 엔티티의 식별 번호를 변수에 할당한다.
        postReplyEntity.setMemberEntity(memberEntity);
        postReplyEntity.setPostEntity(post);

        int replyNo = postReplyRepository.save(postReplyEntity).getPostReplyNo();

        // 4. 저장된 reply 엔티티를 1 : N 관계로 맵핑되어있는 member, post 에 각각 저장한다.
        PostReplyEntity savedReply = postReplyRepository.findById(replyNo).get();
        // 5. 타겟 no 을 본인 번호로 업데이트 해준다.
        savedReply.setPostReplyTarget(savedReply.getPostReplyNo());
        memberEntity.getPostReplyEntities().add(savedReply);
        assert post != null;
        post.getPostReplyEntities().add(savedReply);
        return true;
    }

    // [대댓글 등록]
    @Transactional
    public boolean replyChildWrite(String content, int replyNo, int postNo) {
        // 1. 로그인 세션을 호출해서 댓글 입력하는 회원 정보를 불러온다.
        HttpSession session = request.getSession();
        MemberDto loginDTO = (MemberDto) session.getAttribute("loginSession");
        // 2. 로그인 한 회원 엔티티는 member 객체에 담긴다.
        MemberEntity member = memberService.getMember(loginDTO.getMemberNo());
        // 2.1 작성하는 게시글 식별번호를 통해서 게시글 엔티티를 불러온다.
        PostEntity postEntity = null;
        if (postRepository.findById(postNo).isPresent())
            postEntity = postRepository.findById(postNo).get();
        // 3. 대댓글을 등록한다.
        // 3.1 게시글 번호, 부모 댓글 번호, 작성 내용을 인수로 받는다.
        // 3.2 부모 댓글 depth=0 부터 시작해서 자식 댓글은 +1
        PostReplyEntity parentReply = null;
        if (postReplyRepository.findById(replyNo).isPresent())
            parentReply = postReplyRepository.findById(replyNo).get();
        assert parentReply != null;
        int parentReplyDepth = parentReply.getPostReplyDepth();
        int parentReplyOrder = parentReply.getPostReplyOrder();

        PostReplyEntity replyChild = PostReplyEntity.builder()
                .postReplyContent(content)
                .postReplyTarget(parentReply.getPostReplyNo())
                .build();
        // targetNo 에 해당하는 replyDepth 만을 선택한다.
        int depth = postReplyRepository.getReplyDepthByTargetNo(parentReply.getPostReplyNo());
        replyChild.setPostReplyDepth(depth);
        replyChild.setPostReplyOrder(depth);
        replyChild.setMemberEntity(member);
        replyChild.setPostEntity(postEntity);

        int replyChildNo = postReplyRepository.save(replyChild).getPostReplyNo();

        PostReplyEntity savedReplyChild = null;

        if (postReplyRepository.findById(replyChildNo).isPresent())
            savedReplyChild = postReplyRepository.findById(replyChildNo).get();
        member.getPostReplyEntities().add(savedReplyChild);
        assert postEntity != null;
        postEntity.getPostReplyEntities().add(savedReplyChild);
        return true;

    }


}
