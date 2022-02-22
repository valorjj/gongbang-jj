package com.ezen.domain.entity.repository;

import com.ezen.domain.entity.RoomEntity;
import com.ezen.domain.entity.RoomLikeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomLikeRepository extends JpaRepository<RoomLikeEntity, Integer> {


}
