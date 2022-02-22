package com.ezen.domain.entity.repository;

import com.ezen.domain.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<PostEntity, Integer> {

    // 1. boardNo 받고 Post 불러오기
    @Query(nativeQuery = true, value = "SELECT * FROM post WHERE boardNo = :boardNo")
    Page<PostEntity> findPostByBoardNo(@Param("boardNo") int boardNo, Pageable pageable);
}
