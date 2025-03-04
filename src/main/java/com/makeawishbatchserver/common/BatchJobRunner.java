package com.makeawishbatchserver.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchJobRunner {
    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;
    private final JobRepository jobRepository;

    @Value("${spring.batch.job.name}")
    private String jobName;

    public void run() {
        try {
            Job job = jobRegistry.getJob(jobName);
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis()) // 중복 실행 가능하도록
                    .addDate("runDate", new Date())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(job, jobParameters);
            log.info("Job {} started with status {}", jobName, execution.getStatus());
        } catch (Exception e) {
            log.error("Failed to execute job: {}", jobName, e);
        }
    }
}
