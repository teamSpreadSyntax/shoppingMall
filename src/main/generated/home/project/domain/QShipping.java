package home.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QShipping is a Querydsl query type for Shipping
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QShipping extends EntityPathBase<Shipping> {

    private static final long serialVersionUID = -166918444L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QShipping shipping = new QShipping("shipping");

    public final StringPath arrivedDate = createString("arrivedDate");

    public final StringPath arrivingDate = createString("arrivingDate");

    public final StringPath deliveryNum = createString("deliveryNum");

    public final EnumPath<DeliveryStatusType> deliveryStatus = createEnum("deliveryStatus", DeliveryStatusType.class);

    public final EnumPath<DeliveryType> deliveryType = createEnum("deliveryType", DeliveryType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QOrder order;

    public QShipping(String variable) {
        this(Shipping.class, forVariable(variable), INITS);
    }

    public QShipping(Path<? extends Shipping> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QShipping(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QShipping(PathMetadata metadata, PathInits inits) {
        this(Shipping.class, metadata, inits);
    }

    public QShipping(Class<? extends Shipping> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.order = inits.isInitialized("order") ? new QOrder(forProperty("order"), inits.get("order")) : null;
    }

}
