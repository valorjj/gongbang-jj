package com.ezen.domain.entity.repository;

import com.ezen.domain.entity.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<NoteEntity, Integer> {
}