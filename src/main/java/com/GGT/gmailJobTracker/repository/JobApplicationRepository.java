package com.GGT.gmailJobTracker.repository;


import com.GGT.gmailJobTracker.model.JobApplication;
import com.GGT.gmailJobTracker.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByCompanyNameContainingIgnoreCase(String companyName);

    List<JobApplication> findByStatus(ApplicationStatus status);

    Optional<JobApplication> findByEmailThreadId(String emailThreadId);
}
