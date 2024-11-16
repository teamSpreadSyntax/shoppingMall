package home.project.domain.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberProduct is a Querydsl query type for MemberProduct
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberProduct extends EntityPathBase<MemberProduct> {

    private static final long serialVersionUID = 1928619143L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberProduct memberProduct = new QMemberProduct("memberProduct");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final home.project.domain.product.QProduct product;

    public QMemberProduct(String variable) {
        this(MemberProduct.class, forVariable(variable), INITS);
    }

    public QMemberProduct(Path<? extends MemberProduct> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberProduct(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberProduct(PathMetadata metadata, PathInits inits) {
        this(MemberProduct.class, metadata, inits);
    }

    public QMemberProduct(Class<? extends MemberProduct> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
        this.product = inits.isInitialized("product") ? new home.project.domain.product.QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

