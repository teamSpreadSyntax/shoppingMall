package home.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductEvent is a Querydsl query type for ProductEvent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductEvent extends EntityPathBase<ProductEvent> {

    private static final long serialVersionUID = -625436399L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductEvent productEvent = new QProductEvent("productEvent");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final QEvent event;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QProduct product;

    public QProductEvent(String variable) {
        this(ProductEvent.class, forVariable(variable), INITS);
    }

    public QProductEvent(Path<? extends ProductEvent> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductEvent(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductEvent(PathMetadata metadata, PathInits inits) {
        this(ProductEvent.class, metadata, inits);
    }

    public QProductEvent(Class<? extends ProductEvent> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.event = inits.isInitialized("event") ? new QEvent(forProperty("event")) : null;
        this.product = inits.isInitialized("product") ? new QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

