package home.project.domain.common;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QQnA is a Querydsl query type for QnA
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQnA extends EntityPathBase<QnA> {

    private static final long serialVersionUID = -123535705L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QQnA qnA = new QQnA("qnA");

    public final StringPath answer = createString("answer");

    public final DateTimePath<java.time.LocalDateTime> answerDate = createDateTime("answerDate", java.time.LocalDateTime.class);

    public final home.project.domain.member.QMember answerer;

    public final EnumPath<AnswerStatus> answerStatus = createEnum("answerStatus", AnswerStatus.class);

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final home.project.domain.member.QMember member;

    public final home.project.domain.order.QOrders orders;

    public final home.project.domain.product.QProduct product;

    public final EnumPath<QnAType> qnAType = createEnum("qnAType", QnAType.class);

    public final StringPath subject = createString("subject");

    public QQnA(String variable) {
        this(QnA.class, forVariable(variable), INITS);
    }

    public QQnA(Path<? extends QnA> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QQnA(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QQnA(PathMetadata metadata, PathInits inits) {
        this(QnA.class, metadata, inits);
    }

    public QQnA(Class<? extends QnA> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.answerer = inits.isInitialized("answerer") ? new home.project.domain.member.QMember(forProperty("answerer")) : null;
        this.member = inits.isInitialized("member") ? new home.project.domain.member.QMember(forProperty("member")) : null;
        this.orders = inits.isInitialized("orders") ? new home.project.domain.order.QOrders(forProperty("orders"), inits.get("orders")) : null;
        this.product = inits.isInitialized("product") ? new home.project.domain.product.QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

