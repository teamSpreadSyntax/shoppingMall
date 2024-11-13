package home.project.controller.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/elastic")
public class ElasticSyncController {

    private final JobLauncher jobLauncher;
    private final Job elasticSyncJob;
    private final JobExplorer jobExplorer;


    @PostMapping("/sync")
    public ResponseEntity<String> syncToElastic() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(elasticSyncJob, jobParameters);
            return ResponseEntity.ok("Sync started with status: " + execution.getStatus());

        } catch (Exception e) {
            log.error("Failed to start sync", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to start sync: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> getLastJobStatus() {
        try {
            // 마지막 실행 상태 조회
            List<JobInstance> jobInstances = jobExplorer.findJobInstancesByJobName("elasticSyncJob", 0, 1);

            if (!jobInstances.isEmpty()) {
                List<JobExecution> executions = jobExplorer.getJobExecutions(jobInstances.get(0));
                if (!executions.isEmpty()) {
                    JobExecution lastExecution = executions.get(0);
                    return ResponseEntity.ok("Last sync status: " + lastExecution.getStatus());
                }
            }
            return ResponseEntity.ok("No previous sync found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get status: " + e.getMessage());
        }
    }
}