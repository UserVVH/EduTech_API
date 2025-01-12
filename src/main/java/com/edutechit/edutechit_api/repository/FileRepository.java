package com.edutechit.edutechit_api.repository;

import com.edutechit.edutechit_api.entity.Document;
import com.edutechit.edutechit_api.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    @Modifying
    @Query("DELETE FROM File f WHERE f.document = :document")
    void deleteByDocument(@Param("document") Document document);
//    void deleteByDocument(Document document);
    File findByDocument(Document document);
    File findByDocumentId(Long documentId);
}