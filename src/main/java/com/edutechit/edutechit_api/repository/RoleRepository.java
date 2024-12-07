package com.edutechit.edutechit_api.repository;


import com.edutechit.edutechit_api.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name); //tìm role theo tên
}
