package com.GGT.gmailJobTracker.service;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;


import com.GGT.gmailJobTracker.model.JobApplication;

import com.GGT.gmailJobTracker.model.ApplicationStatus;
import com.GGT.gmailJobTracker.repository.JobApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;

    @Autowired
    public JobApplicationService(JobApplicationRepository jobApplicationRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
    }

    public List<JobApplication> getAllApplications() {
        return jobApplicationRepository.findAll();
    }

    public Optional<JobApplication> getApplicationById(Long id) {
        return jobApplicationRepository.findById(id);
    }

    public JobApplication saveApplication(JobApplication application) {
        if (application.getAppliedDate() == null) {
            application.setAppliedDate(LocalDateTime.now());
        }
        application.setLastUpdated(LocalDateTime.now());
        return jobApplicationRepository.save(application);
    }

    public Optional<JobApplication> updateApplicationStatus(Long id, ApplicationStatus newStatus) {
        return jobApplicationRepository.findById(id)
                .map(application -> {
                    application.setStatus(newStatus);
                    application.setLastUpdated(LocalDateTime.now());
                    return jobApplicationRepository.save(application);
                });
    }

    public Optional<JobApplication> findByEmailThreadId(String threadId) {
        return jobApplicationRepository.findByEmailThreadId(threadId);
    }

    public void deleteApplication(Long id) {
        jobApplicationRepository.deleteById(id);
    }
}