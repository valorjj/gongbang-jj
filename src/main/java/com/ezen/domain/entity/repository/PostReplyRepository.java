package com.ezen.domain.entity.repository;

import com.ezen.domain.entity.PostReplyEntity;
import com.ezen.domain.entity.ReplyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostReplyRepository extends JpaRepository<PostReplyEntity, Integer> {


    // 부모 댓글(depth=0)을 호출하는 쿼리문
    @Query(nativeQuery = true, value = "SELECT * FROM postReply WHERE postReplyDepth = 0 AND postNo = :postReplyNo")
    Page<PostReplyEntity> getParentReply(Pageable pageable, @Param("postReplyNo") int postReplyNo);

    // 대댓글 등록을 위한 쿼리문
    @Query(nativeQuery = true, value = "SELECT count(postReplyDepth) FROM postReply WHERE postReplyTarget = :postReplyTarget")
    int getReplyDepthByTargetNo(@Param("postReplyTarget") int postReplyTarget);

    // 부모 댓글에 속한 자식 댓글을 호출하는 쿼리문
    @Query(nativeQuery = true, value = "SELECT * FROM postReply WHERE postReplyDepth > 0 AND postNo = :postReplyNo")
    List<PostReplyEntity> getChildReply(@Param("postReplyNo") int postReplyNo);

}
