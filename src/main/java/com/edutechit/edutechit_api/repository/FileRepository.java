package com.edutechit.edutechit_api.repository;

import com.edutechit.edutechit_api.entity.Document;
import com.edutechit.edutechit_api.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    void deleteByDocument(Document document);
    File findByDocument(Document document);
    File findByDocumentId(Long documentId);
}