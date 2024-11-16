package home.project.domain.product;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductCoupon is a Querydsl query type for ProductCoupon
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductCoupon extends EntityPathBase<ProductCoupon> {

    private static final long serialVersionUID = -953458736L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductCoupon productCoupon = new QProductCoupon("productCoupon");

    public final QCoupon coupon;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> issuedAt = createDateTime("issuedAt", java.time.LocalDateTime.class);

    public final BooleanPath isUsed = createBoolean("isUsed");

    public final QProduct product;

    public final DateTimePath<java.time.LocalDateTime> usedAt = createDateTime("usedAt", java.time.LocalDateTime.class);

    public QProductCoupon(String variable) {
        this(ProductCoupon.class, forVariable(variable), INITS);
    }

    public QProductCoupon(Path<? extends ProductCoupon> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductCoupon(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductCoupon(PathMetadata metadata, PathInits inits) {
        this(ProductCoupon.class, metadata, inits);
    }

    public QProductCoupon(Class<? extends ProductCoupon> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coupon = inits.isInitialized("coupon") ? new QCoupon(forProperty("coupon")) : null;
        this.product = inits.isInitialized("product") ? new QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

