package home.project.config.redis;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.data.domain.PageImpl;

public class PageImplDeserializerModule extends SimpleModule {
    public PageImplDeserializerModule() {
        addDeserializer(PageImpl.class, new PageImplDeserializer());
    }
}
