package home.project.controller.common;

import home.project.response.CustomResponseEntity;
import home.project.service.product.ProductReindexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/elastic")
public class ElasticSyncController {

    private final JobLauncher jobLauncher;
    private final Job elasticSyncJob;
    private final JobExplorer jobExplorer;
    private final ProductReindexService productReindexService;

    @PostMapping("/sync")
    public ResponseEntity<?> syncToElastic() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(elasticSyncJob, jobParameters);
            String message = "상품 Elasticsearch 동기화가 시작되었습니다. 상태: " + execution.getStatus();
            return new CustomResponseEntity<>(null, message, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Failed to start sync", e);
            return new CustomResponseEntity<>(null, "동기화 실패: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getLastJobStatus() {
        try {
            List<JobInstance> jobInstances = jobExplorer.findJobInstancesByJobName("elasticSyncJob", 0, 1);

            if (!jobInstances.isEmpty()) {
                List<JobExecution> executions = jobExplorer.getJobExecutions(jobInstances.get(0));
                if (!executions.isEmpty()) {
                    JobExecution lastExecution = executions.get(0);
                    String message = "마지막 동기화 상태: " + lastExecution.getStatus();
                    return new CustomResponseEntity<>(null, message, HttpStatus.OK);
                }
            }

            return new CustomResponseEntity<>(null, "이전 동기화 이력이 없습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new CustomResponseEntity<>(null, "상태 조회 실패: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/reindex")
    public ResponseEntity<?> reindexAll() {
        try {
            productReindexService.reindexAllProductsToES();
            return new CustomResponseEntity<>(null, "전체 상품 Elasticsearch 재색인 완료!", HttpStatus.OK);
        } catch (Exception e) {
            log.error("재색인 실패", e);
            return new CustomResponseEntity<>(null, "재색인 중 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/deleteindex")
    public ResponseEntity<?> deleteProductIndex() {
        try {
            productReindexService.deleteAllProductDocuments();
            return new CustomResponseEntity<>(null, "전체 상품 Elasticsearch 삭제 완료!", HttpStatus.OK);
        } catch (Exception e) {
            log.error("삭제 실패", e);
            return new CustomResponseEntity<>(null, "삭제 중 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
