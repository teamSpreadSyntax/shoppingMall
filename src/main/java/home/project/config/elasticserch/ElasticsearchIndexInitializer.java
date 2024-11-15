package home.project.config.elasticserch;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.domain.elasticsearch.ProductDocument;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticsearchIndexInitializer {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void initializeIndex() {
        try {
            log.info("Starting Elasticsearch index initialization...");
            createProductIndex();
        } catch (Exception e) {
            log.error("Failed to initialize Elasticsearch indices", e);
            throw new RuntimeException("Failed to initialize Elasticsearch indices", e);  // 에러를 던져서 초기화 실패를 명확히 함
        }
    }

    private void createProductIndex() throws IOException {
        IndexOperations indexOps = elasticsearchOperations.indexOps(ProductDocument.class);

        if (indexOps.exists()) {
            log.info("Product index already exists. Deleting existing index...");
            indexOps.delete();  // 기존 인덱스 삭제 (개발 환경에서만 사용하세요)
        }

        // Load settings from JSON file
        Resource resource = new ClassPathResource("elasticsearch/product-settings.json");
        Map settings = objectMapper.readValue(resource.getInputStream(), Map.class);

        // Create mapping first
        Document mapping = indexOps.createMapping(ProductDocument.class);

        // Create index with settings
        indexOps.create(Document.from(settings));

        // Apply mapping after index creation
        boolean mappingResult = indexOps.putMapping(mapping);

        if (mappingResult) {
            log.info("Successfully created product index and applied mapping.");
        } else {
            log.error("Failed to apply mapping to product index.");
            throw new RuntimeException("Failed to apply mapping to product index");
        }
    }
}