package home.project.domain.delivery;

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

    private static final long serialVersionUID = -1484207038L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QShipping shipping = new QShipping("shipping");

    public final StringPath arrivedDate = createString("arrivedDate");

    public final StringPath arrivingDate = createString("arrivingDate");

    public final StringPath deliveryAddress = createString("deliveryAddress");

    public final NumberPath<Long> deliveryCost = createNumber("deliveryCost", Long.class);

    public final StringPath deliveryNum = createString("deliveryNum");

    public final EnumPath<DeliveryStatusType> deliveryStatus = createEnum("deliveryStatus", DeliveryStatusType.class);

    public final EnumPath<DeliveryType> deliveryType = createEnum("deliveryType", DeliveryType.class);

    public final StringPath departureDate = createString("departureDate");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final home.project.domain.order.QOrders orders;

    public final StringPath shippingMessage = createString("shippingMessage");

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
        this.orders = inits.isInitialized("orders") ? new home.project.domain.order.QOrders(forProperty("orders"), inits.get("orders")) : null;
    }

}

