package com.edutechit.edutechit_api.admin.controller;

import com.edutechit.edutechit_api.dto.StatisticsDTO;
import com.edutechit.edutechit_api.repository.DocumentRepository;
import com.edutechit.edutechit_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/statistics")
public class StatisticsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @GetMapping()
    public StatisticsDTO getStatistics() {
        long totalUsers = userRepository.countTotalUsers();
        long totalDocuments = documentRepository.countTotalDocuments();
        long verifiedDocuments = documentRepository.countVerifiedDocuments();
        long createdDocuments = documentRepository.countCreatedDocuments();
        long rejectedDocuments = documentRepository.countRejectedDocuments();
        long totalRoleAdmins = userRepository.countAdmins();
        long totalRoleTeachers = userRepository.countTeachers();
        long totalRoleStudents = userRepository.countStudents();
        long totalRoleUsers = userRepository.countRoleUsers();
        double verifiedPercentage = totalDocuments > 0 ? (double) verifiedDocuments / totalDocuments * 100 : 0;
        double createdPercentage = totalDocuments > 0 ? (double) createdDocuments / totalDocuments * 100 : 0;
        double rejectedPercentage = totalDocuments > 0 ? (double) rejectedDocuments / totalDocuments * 100 : 0;

        return new StatisticsDTO(totalUsers, totalDocuments, verifiedDocuments, createdDocuments, rejectedDocuments,
                verifiedPercentage, createdPercentage, rejectedPercentage, totalRoleAdmins, totalRoleTeachers, totalRoleStudents, totalRoleUsers);
    }
}
