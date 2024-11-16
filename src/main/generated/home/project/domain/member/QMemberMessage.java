package home.project.domain.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberMessage is a Querydsl query type for MemberMessage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberMessage extends EntityPathBase<MemberMessage> {

    private static final long serialVersionUID = -1101949025L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberMessage memberMessage = new QMemberMessage("memberMessage");

    public final StringPath content = createString("content");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public QMemberMessage(String variable) {
        this(MemberMessage.class, forVariable(variable), INITS);
    }

    public QMemberMessage(Path<? extends MemberMessage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberMessage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberMessage(PathMetadata metadata, PathInits inits) {
        this(MemberMessage.class, metadata, inits);
    }

    public QMemberMessage(Class<? extends MemberMessage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

