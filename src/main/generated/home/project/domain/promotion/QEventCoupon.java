package home.project.domain.promotion;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEventCoupon is a Querydsl query type for EventCoupon
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEventCoupon extends EntityPathBase<EventCoupon> {

    private static final long serialVersionUID = 504950575L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEventCoupon eventCoupon = new QEventCoupon("eventCoupon");

    public final home.project.domain.product.QCoupon coupon;

    public final QEvent event;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isUsed = createBoolean("isUsed");

    public QEventCoupon(String variable) {
        this(EventCoupon.class, forVariable(variable), INITS);
    }

    public QEventCoupon(Path<? extends EventCoupon> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEventCoupon(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEventCoupon(PathMetadata metadata, PathInits inits) {
        this(EventCoupon.class, metadata, inits);
    }

    public QEventCoupon(Class<? extends EventCoupon> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coupon = inits.isInitialized("coupon") ? new home.project.domain.product.QCoupon(forProperty("coupon")) : null;
        this.event = inits.isInitialized("event") ? new QEvent(forProperty("event")) : null;
    }

}

