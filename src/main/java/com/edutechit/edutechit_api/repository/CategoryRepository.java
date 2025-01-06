package com.edutechit.edutechit_api.repository;

import com.edutechit.edutechit_api.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
//    tìm kiếm không phân biệt chữ hoa, chữ thường
    List<Category> findByNameContainingIgnoreCase(String name);
}
