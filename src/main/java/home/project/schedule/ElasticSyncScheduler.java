package home.project.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ElasticSyncScheduler {

    private final JobLauncher jobLauncher;
    private final Job elasticSyncJob;

    @Scheduled(cron = "0 0 1 * * *") // 매일 새벽 1시
    public void syncToElastic() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(elasticSyncJob, jobParameters);
            log.info("Batch job completed with status: {}", execution.getStatus());

        } catch (Exception e) {
            log.error("Batch job failed", e);
        }
    }
}