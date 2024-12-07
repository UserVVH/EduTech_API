package com.edutechit.edutechit_api.repository;

import com.edutechit.edutechit_api.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Follow findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteByEmail(String email);
}
