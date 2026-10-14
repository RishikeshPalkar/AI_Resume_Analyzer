package com.example.resumeanalyzer.jobdescription.service;

import com.example.resumeanalyzer.common.exception.ResourceNotFoundException;
import com.example.resumeanalyzer.common.response.PagedResponse;
import com.example.resumeanalyzer.jobdescription.dto.JobDescriptionRequest;
import com.example.resumeanalyzer.jobdescription.dto.JobDescriptionResponse;
import com.example.resumeanalyzer.jobdescription.entity.JobDescription;
import com.example.resumeanalyzer.jobdescription.repository.JobDescriptionRepository;
import com.example.resumeanalyzer.user.entity.User;
import com.example.resumeanalyzer.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobDescriptionService {

    private final JobDescriptionRepository jobDescriptionRepository;
    private final UserRepository userRepository;

    public JobDescriptionService(JobDescriptionRepository jobDescriptionRepository, UserRepository userRepository) {
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public JobDescriptionResponse createJobDescription(Long userId, JobDescriptionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        JobDescription jobDescription = new JobDescription(
                user,
                request.getTitle() != null ? request.getTitle().trim() : null,
                request.getCompany() != null ? request.getCompany().trim() : null,
                request.getDescriptionText().trim()
        );

        jobDescription = jobDescriptionRepository.save(jobDescription);
        return mapToResponse(jobDescription);
    }

    @Transactional(readOnly = true)
    public PagedResponse<JobDescriptionResponse> getJobDescriptionsForUser(Long userId, Pageable pageable) {
        Page<JobDescription> page = jobDescriptionRepository.findByUserId(userId, pageable);
        List<JobDescriptionResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Transactional(readOnly = true)
    public JobDescriptionResponse getJobDescriptionById(Long id, Long userId) {
        JobDescription jobDescription = jobDescriptionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("JobDescription", "id", id));
        return mapToResponse(jobDescription);
    }

    @Transactional(readOnly = true)
    public JobDescription getJobDescriptionEntity(Long id, Long userId) {
        return jobDescriptionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("JobDescription", "id", id));
    }

    @Transactional
    public void deleteJobDescription(Long id, Long userId) {
        JobDescription jobDescription = jobDescriptionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("JobDescription", "id", id));
        jobDescriptionRepository.delete(jobDescription);
    }

    private JobDescriptionResponse mapToResponse(JobDescription jd) {
        return new JobDescriptionResponse(
                jd.getId(),
                jd.getTitle(),
                jd.getCompany(),
                jd.getDescriptionText(),
                jd.getCreatedAt()
        );
    }
}
