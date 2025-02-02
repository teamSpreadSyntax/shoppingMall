package home.project.config.redis;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.List;

class PageImplDeserializer extends JsonDeserializer<PageImpl<?>> {
    @Override
    public PageImpl<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // content 파싱
        List<?> content = ctxt.readTreeAsValue(node.get("content"), List.class);

        // pageable 정보 파싱
        JsonNode pageableNode = node.get("pageable");
        int pageNumber = pageableNode.get("pageNumber").asInt();
        int pageSize = pageableNode.get("pageSize").asInt();

        // totalElements 파싱
        long totalElements = node.get("totalElements").asLong();

        // PageImpl 객체 생성
        return new PageImpl<>(content, PageRequest.of(pageNumber, pageSize), totalElements);
    }
}