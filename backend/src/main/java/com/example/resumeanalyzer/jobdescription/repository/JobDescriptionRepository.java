package com.example.resumeanalyzer.jobdescription.repository;

import com.example.resumeanalyzer.jobdescription.entity.JobDescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobDescriptionRepository extends JpaRepository<JobDescription, Long> {
    Page<JobDescription> findByUserId(Long userId, Pageable pageable);
    Optional<JobDescription> findByIdAndUserId(Long id, Long userId);
    boolean existsByIdAndUserId(Long id, Long userId);
}
