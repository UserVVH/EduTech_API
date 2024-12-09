package com.edutechit.edutechit_api.repository;

import com.edutechit.edutechit_api.dto.ListUserDto;
import com.edutechit.edutechit_api.dto.UserDocumentStatsDTO;
import com.edutechit.edutechit_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(@Param("email") String email); // tìm user theo email

  Optional<User> findByIdentifier(String identifier); // tìm user theo identifier

  Optional<User> findById(@Param("id") Long id);  // Tìm user theo ID

  List<User> findByFullnameContainingIgnoreCase(String fullname); // tìm user theo fullname

  @Query(
      "SELECT new com.edutechit.edutechit_api.dto.UserDocumentStatsDTO(u.id, u.fullname, u.email, u.identifier, u.role.name, u.avatar, COUNT(d.id), SUM(d.view)) "
          +
          "FROM User u JOIN u.documents d " +
          "WHERE d.status = 'VERIFIED' " +
          "GROUP BY u.id, u.fullname, u.email, u.identifier, u.role.name, u.avatar " +
          "ORDER BY COUNT(d.id) DESC")
  List<UserDocumentStatsDTO> findUsersOrderByDocumentCountDesc();

  @Query(
      "SELECT new com.edutechit.edutechit_api.dto.ListUserDto(u.id, u.fullname, u.email, u.address, u.identifier, u.avatar, u.createdAt, u.updatedAt, u.enabled, u.role.name) "
          +
          "FROM User u")
  List<ListUserDto> findAllUsersAsListUserDto();


  @Query("SELECT COUNT(u) FROM User u")
  long countTotalUsers();


  @Query("SELECT COUNT(u) FROM User u WHERE u.role.name = 'ADMIN'")
  long countAdmins();

  @Query("SELECT COUNT(u) FROM User u WHERE u.role.name = 'TEACHER'")
  long countTeachers();

  @Query("SELECT COUNT(u) FROM User u WHERE u.role.name = 'STUDENT'")
  long countStudents();

  @Query("SELECT COUNT(u) FROM User u WHERE u.role.name = 'USER'")
  long countRoleUsers();

}