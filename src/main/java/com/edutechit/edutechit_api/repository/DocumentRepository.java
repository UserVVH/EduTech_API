package com.edutechit.edutechit_api.repository;

import com.edutechit.edutechit_api.entity.Category;
import com.edutechit.edutechit_api.entity.Comment;
import com.edutechit.edutechit_api.entity.Document;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Document d WHERE d.id = :documentId")
    void deleteDocumentById(@Param("documentId") Long documentId);

    List<Document> findAllByCategoryId(Long categoryId);
    List<Document> findByCategoryId(Long categoryId);
    void deleteByCategory(Category category);

    @Query("SELECT d FROM Document d WHERE d.status = 'VERIFIED' ORDER BY d.createdAt DESC")
    List<Document> findAllDocuments();

    @Query("SELECT d FROM Document d ORDER BY d.createdAt DESC")
    List<Document> getAll();

    @Query("SELECT d FROM Document d ORDER BY d.createdAt DESC")
    List<Document> findAllDocumentsAdmin();

    //hiển thị 1 document theo id
    @Query("SELECT d FROM Document d WHERE d.id = :documentId")
    Document findDocumentById(@Param("documentId") Long documentId);

    //lấy tên category của document
    @Query("SELECT c.name FROM Document d JOIN d.category c WHERE d.id = :documentId")
    List<String> findCategoryNamesByDocumentId(Long documentId);

    @Query("SELECT f.filePath FROM Document d JOIN d.file f WHERE d.id = :documentId")
    List<String> findFilePathsByDocumentId(Long documentId); //tìm đường dẫn file của một document

    @Query("SELECT c FROM Comment c WHERE c.document.id = :documentId")
    List<Comment> findCommentsByDocumentId(@Param("documentId") Long documentId); //tìm tất cả các comment của một document

    //tìm tất cả các document của một user
    @Query("SELECT d FROM Document d WHERE d.user.id = :userId")
    List<Document> findDocumentsByUserId(@Param("userId") Long userId);

    //tìm tất cả các document của một user có status là VERIFIED
    @Query("SELECT d FROM Document d WHERE d.user.id = :userId AND d.status = 'VERIFIED' ORDER BY d.createdAt DESC")
    List<Document> findDocumentsByUserIdAndVERIFIED(@Param("userId") Long userId);

    //tìm tất cả các document của một user có status là CREATED
    @Query("SELECT d FROM Document d WHERE d.user.id = :userId AND d.status = 'CREATED' ORDER BY d.createdAt DESC")
    List<Document> findDocumentsByUserIdAndCREATED(@Param("userId") Long userId);

    //tìm tất cả các document của một user có status là REJECTED
    @Query("SELECT d FROM Document d WHERE d.user.id = :userId AND d.status = 'REJECTED' ORDER BY d.createdAt DESC")
    List<Document> findDocumentsByUserIdAndREJECTED(@Param("userId") Long userId);

    //tìm tất cả các document theo category có status là VERIFIED
    @Query("SELECT d FROM Document d WHERE d.category.name = :categoryName AND d.status = 'VERIFIED' ORDER BY d.createdAt DESC")
    List<Document> findAllDocumentsByCategoryName(@Param("categoryName") String categoryName);


    //tìm tất cả các document của một user có status là VERIFIED
    @Query("SELECT d FROM Document d WHERE d.user.id = :userId ORDER BY d.createdAt DESC")
    List<Document> findAllByUserId(Long userId);

    //tìm tất cả các document của bản thân có status là VERIFIED
    @Query("SELECT d FROM Document d WHERE d.user.id = :userId ORDER BY d.createdAt DESC")
    List<Document> findAllOfMy(Long userId);

    @Query("SELECT d FROM Document d ORDER BY d.createdAt DESC")
    List<Document> findAll(Sort sort);

    @Query("SELECT d FROM Document d WHERE d.status = 'VERIFIED'")
    List<Document> findAllVerified(Sort sort);

    @Query("SELECT d FROM Document d " +
            "WHERE d.status = 'VERIFIED' " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'title' AND :order = 'asc' THEN d.title END ASC, " +
            "CASE WHEN :sortBy = 'title' AND :order = 'desc' THEN d.title END DESC, " +
            "CASE WHEN :sortBy = 'view' AND :order = 'asc' THEN d.view END ASC, " +
            "CASE WHEN :sortBy = 'view' AND :order = 'desc' THEN d.view END DESC, " +
            "CASE WHEN :sortBy = 'createdAt' AND :order = 'asc' THEN d.createdAt END ASC, " +
            "CASE WHEN :sortBy = 'createdAt' AND :order = 'desc' THEN d.createdAt END DESC")
    List<Document> findAllSorted(@Param("sortBy") String sortBy, @Param("order") String order);



    //seach document theo title
    @Query("SELECT d FROM Document d WHERE d.title LIKE %:title% AND d.status = 'VERIFIED' ORDER BY d.createdAt DESC")
    List<Document> findByTitleContainingIgnoreCase(String title);

    // Top 10 bài viết VERIFIED mới nhất
    @Query("SELECT d FROM Document d WHERE d.status = 'VERIFIED' ORDER BY d.createdAt DESC LIMIT 10")
    List<Document> findTop10ByOrderByCreatedAtDesc();

    // Top 100 bài viết VERIFIED có số view cao nhất
    @Query("SELECT d FROM Document d WHERE d.status = 'VERIFIED' ORDER BY d.view DESC LIMIT 100")
    List<Document> findTop100ByOrderByViewDesc();

    // Top 10 bài viết VERIFIED có số view cao nhất
    @Query("SELECT d FROM Document d WHERE d.status = 'VERIFIED' ORDER BY d.view DESC LIMIT 10")
    List<Document> findTop10ByOrderByViewDesc();

    // Lấy tất cả bài viết "CHƯA ĐƯỢC DUYỆT" của một user
    @Query("SELECT d FROM Document d WHERE d.user.id = :userId AND d.status = 'CREATED' ORDER BY d.createdAt DESC")
    List<Document> findAllDocumentsByUserIdAndStatusCreated(@Param("userId") Long userId);

    // Lấy tất cả bài viết "ĐÃ ĐƯỢC DUYỆT" của một user
    @Query("SELECT d FROM Document d WHERE d.user.id = :userId AND d.status = 'VERIFIED' ORDER BY d.createdAt DESC")
    List<Document> findAllDocumentsByUserIdAndStatusVerified(@Param("userId") Long userId);

    // Lấy tất cả bài viết "BỊ TỪ CHỐI" của một user
    @Query("SELECT d FROM Document d WHERE d.user.id = :userId AND d.status = 'REJECTED' ORDER BY d.createdAt DESC")
    List<Document> findAllDocumentsByUserIdAndStatusRejected(@Param("userId") Long userId);

    // Lấy tất cả bài viết của một user (bất kể trạng thái)
    @Query("SELECT d FROM Document d WHERE d.user.id = :userId ORDER BY d.createdAt DESC")
    List<Document> findAllDocumentsByUserId(@Param("userId") Long userId);

    // Lấy danh sách tất cả các tài liệu "ĐÃ ĐƯỢC DUYỆT"
    @Query("SELECT d FROM Document d WHERE d.status = 'VERIFIED' ORDER BY d.createdAt DESC")
    List<Document> findAllDocumentsByStatusVerified();

    // Lấy danh sách tất cả các tài liệu "CHƯA ĐƯỢC DUYỆT"
    @Query("SELECT d FROM Document d WHERE d.status = 'CREATED' ORDER BY d.createdAt DESC")
    List<Document> findAllDocumentsByStatusCreated();

    // Lấy danh sách tất cả các tài liệu "BỊ TỪ CHỐI"
    @Query("SELECT d FROM Document d WHERE d.status = 'REJECTED' ORDER BY d.createdAt DESC")
    List<Document> findAllDocumentsByStatusRejected();

    @Query("SELECT COUNT(d) FROM Document d")
    long countTotalDocuments();

    @Query("SELECT COUNT(d) FROM Document d WHERE d.status = 'VERIFIED'")
    long countVerifiedDocuments();

    @Query("SELECT COUNT(d) FROM Document d WHERE d.status = 'CREATED'")
    long countCreatedDocuments();

    @Query("SELECT COUNT(d) FROM Document d WHERE d.status = 'REJECTED'")
    long countRejectedDocuments();

    @Query("SELECT d FROM Document d WHERE " +
            "(d.title LIKE %:searchText% OR " +
            "d.user.fullname LIKE %:searchText% OR " +
            "d.category.name LIKE %:searchText% OR " +
            "d.author LIKE %:searchText% OR " +
            "d.publisher LIKE %:searchText% OR " +
            "d.publishingYear LIKE %:searchText%) AND " +
            "d.status = 'VERIFIED'")
    List<Document> findByTitleContainingIgnoreCaseOrUser_FullnameContainingIgnoreCaseOrCategory_NameContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrPublisherContainingIgnoreCaseOrPublishingYearContainingIgnoreCase(
            @Param("searchText") String searchText);
}