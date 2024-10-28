package home.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberEvent is a Querydsl query type for MemberEvent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberEvent extends EntityPathBase<MemberEvent> {

    private static final long serialVersionUID = 1169425722L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberEvent memberEvent = new QMemberEvent("memberEvent");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final QEvent event;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public QMemberEvent(String variable) {
        this(MemberEvent.class, forVariable(variable), INITS);
    }

    public QMemberEvent(Path<? extends MemberEvent> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberEvent(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberEvent(PathMetadata metadata, PathInits inits) {
        this(MemberEvent.class, metadata, inits);
    }

    public QMemberEvent(Class<? extends MemberEvent> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.event = inits.isInitialized("event") ? new QEvent(forProperty("event")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

