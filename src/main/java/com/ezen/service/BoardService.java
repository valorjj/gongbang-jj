package com.ezen.service;

import com.ezen.domain.entity.BoardEntity;
import com.ezen.domain.entity.CategoryEntity;
import com.ezen.domain.entity.PostEntity;
import com.ezen.domain.entity.repository.BoardRepository;
import com.ezen.domain.entity.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
public class BoardService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    HttpServletRequest request;

    @Autowired
    MemberService memberService;

    @Autowired
    CategoryService categoryService;

    // [게시판 등록]
    public boolean createNewBoard(String boardName, String categoryName) {
        // 1. 입력받은 이름으로 board 엔티티 생성
        BoardEntity newBoard = BoardEntity.builder().boardName(boardName).build();
        // 2. category 엔티티를 가져와야한다.
        // 2.1 categoryName 에 해당하는 category 를 찾는 쿼리문을 작성한다.
        CategoryEntity categoryEntity = categoryService.getCategoryByName(categoryName);
        // 카테고리 엔티티를 게시판 엔티티에 주입한다.
        newBoard.setCategoryEntity(categoryEntity);
        // 카테고리 엔티티에 보드 엔티티를 추가시킨다.
        // DB 에 저장한 뒤, 해당 boardNo 를 변수에 저장
        int boardNo = boardRepository.save(newBoard).getBoardNo();
        // 해당하는 boardNo 를 이용해서 DB 에서 꺼내온다.
        BoardEntity savedBoard = boardRepository.findById(boardNo).get();
        // 저장된 Board 를 Category 엔티티에 맵핑된 BoardList 에 저장시킵니다.
        categoryEntity.getBoardEntities().add(savedBoard);
        return true;
    }

    // [탭 가져오기]
    public List<BoardEntity> getBoards() {
        return boardRepository.findAll();
    }

    // boardNo 로 board 엔티티 가져오기
    public BoardEntity getBoardEntity(int boardNo) {
        Optional<BoardEntity> boardOptional = boardRepository.findById(boardNo);
        return boardOptional.get();
    }

}
