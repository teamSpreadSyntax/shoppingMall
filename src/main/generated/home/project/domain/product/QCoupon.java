package home.project.domain.product;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCoupon is a Querydsl query type for Coupon
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCoupon extends EntityPathBase<Coupon> {

    private static final long serialVersionUID = 864643755L;

    public static final QCoupon coupon = new QCoupon("coupon");

    public final StringPath assignBy = createString("assignBy");

    public final NumberPath<Integer> discountRate = createNumber("discountRate", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<MemberCoupon, QMemberCoupon> memberCoupons = this.<MemberCoupon, QMemberCoupon>createList("memberCoupons", MemberCoupon.class, QMemberCoupon.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final ListPath<ProductCoupon, QProductCoupon> productCoupons = this.<ProductCoupon, QProductCoupon>createList("productCoupons", ProductCoupon.class, QProductCoupon.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    public QCoupon(String variable) {
        super(Coupon.class, forVariable(variable));
    }

    public QCoupon(Path<? extends Coupon> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCoupon(PathMetadata metadata) {
        super(Coupon.class, metadata);
    }

}

