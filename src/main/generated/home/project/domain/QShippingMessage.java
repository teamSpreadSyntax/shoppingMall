package home.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QShippingMessage is a Querydsl query type for ShippingMessage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QShippingMessage extends EntityPathBase<ShippingMessage> {

    private static final long serialVersionUID = 2063399571L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QShippingMessage shippingMessage = new QShippingMessage("shippingMessage");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public QShippingMessage(String variable) {
        this(ShippingMessage.class, forVariable(variable), INITS);
    }

    public QShippingMessage(Path<? extends ShippingMessage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QShippingMessage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QShippingMessage(PathMetadata metadata, PathInits inits) {
        this(ShippingMessage.class, metadata, inits);
    }

    public QShippingMessage(Class<? extends ShippingMessage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

