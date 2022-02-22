package com.ezen.domain.entity.repository;


import com.ezen.domain.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {

    // 1. categoryName 으로 category entity 를 찾는 쿼리문 작성
    @Query(nativeQuery = true, value = "SELECT * FROM category WHERE categoryName = :categoryName")
    Optional<CategoryEntity> findByName(@Param("categoryName") String categoryName);
}
