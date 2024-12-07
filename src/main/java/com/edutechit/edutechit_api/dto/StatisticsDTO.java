package com.edutechit.edutechit_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticsDTO {
    private long totalUsers;
    private long totalDocuments;
    private long verifiedDocuments;
    private long createdDocuments;
    private long rejectedDocuments;
    private double verifiedPercentage;
    private double createdPercentage;
    private double rejectedPercentage;
    private long totalAdmins;
    private long totalTeachers;
    private long totalStudents;
    private long totalUsersRole;
    
}
