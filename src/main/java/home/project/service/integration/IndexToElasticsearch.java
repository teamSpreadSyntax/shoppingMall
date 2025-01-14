package home.project.service.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class IndexToElasticsearch {
    private final ElasticsearchOperations elasticsearchOperations;

    public <T> void indexDocumentToElasticsearch(T document, Class<T> documentClass) {
        IndexOperations indexOperations = elasticsearchOperations.indexOps(documentClass);

        try {
            if (!indexOperations.exists()) {
                // 기존 인덱스가 있다면 삭제
                indexOperations.delete();

                // 설정 생성
                Map<String, Object> settings = Map.of(
                        "index", Map.of(
                                "analysis", Map.of(
                                        "tokenizer", Map.of(
                                                "nori_tokenizer", Map.of(
                                                        "type", "nori_tokenizer",
                                                        "decompound_mode", "mixed"
                                                )
                                        ),
                                        "analyzer", Map.of(
                                                "korean", Map.of(
                                                        "type", "custom",
                                                        "tokenizer", "nori_tokenizer",
                                                        "filter", List.of("lowercase", "trim")
                                                )
                                        )
                                )
                        )
                );

                // 인덱스 설정과 함께 생성
                indexOperations.create(settings);
                indexOperations.putMapping(indexOperations.createMapping(documentClass));
            }

            // Elasticsearch에 문서 색인
            elasticsearchOperations.save(document);

        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    public <T> void deleteDocumentFromElasticsearch(Long documentId, Class<T> documentClass) {
        try {
            // Long 타입의 ID를 String으로 변환하여 Elasticsearch에서 문서 삭제
            elasticsearchOperations.delete(String.valueOf(documentId), documentClass);
            System.out.println("Document with ID " + documentId + " deleted successfully.");
        } catch (Exception e) {
            System.out.println("Error occurred while deleting document: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

}