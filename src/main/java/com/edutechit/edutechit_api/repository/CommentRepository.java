package com.edutechit.edutechit_api.repository;

import com.edutechit.edutechit_api.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByDocumentId(Long documentId);
    void deleteByDocumentId(Long documentId);
    @Query("SELECT c FROM Comment c WHERE c.document.id = :documentId")
    List<Comment> findCommentsByDocumentId(@Param("documentId") Long documentId);
}
