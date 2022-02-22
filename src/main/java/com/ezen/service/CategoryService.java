package com.ezen.service;

import com.ezen.domain.dto.MemberDto;
import com.ezen.domain.entity.CategoryEntity;
import com.ezen.domain.entity.MemberEntity;
import com.ezen.domain.entity.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    HttpServletRequest request;
    @Autowired
    MemberService memberService;

    // [새로운 카테고리 생성]
    @Transactional
    public boolean createNewCategory(String category) {
        // 0. 로그인 된 회원의 session 을 가져온다
        HttpSession session = request.getSession();
        MemberDto loginDTO = (MemberDto) session.getAttribute("logindto");
        MemberEntity memberEntity = memberService.getMember(loginDTO.getMemberNo());
        // 1. category 에는 입력된 카테고리를 저장한다.
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .categoryName(category)
                .build();
        // 2. category 안에 누가 생성한 건지 member 정보를 주입한다.
        categoryEntity.setMemberEntity(memberEntity);
        // 3. 카테고리 이름을 repo 에 저장한다.
        int categoryNo = categoryRepository.save(categoryEntity).getCategoryNo();
        CategoryEntity savedCategoryEntity = categoryRepository.findById(categoryNo).get();
        memberEntity.getCategoryEntities().add(savedCategoryEntity);
        // 4. 저장하고 true 리턴
        return true;
    }

    // [카테고리 No 로 카테고리 엔티티 가져오기]
    public CategoryEntity getCategoryById(int categoryNo) {
        return categoryRepository.findById(categoryNo).get();
    }

    // [categoryName 으로 category 엔티티 찾기]
    public CategoryEntity getCategoryByName(String categoryName) {
        Optional<CategoryEntity> category = categoryRepository.findByName(categoryName);
        return category.get();
    }

    // [카테고리 리스트 불러오기]
    public Page<CategoryEntity> getCategoryList(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }
}
