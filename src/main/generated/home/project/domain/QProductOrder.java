package home.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductOrder is a Querydsl query type for ProductOrder
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductOrder extends EntityPathBase<ProductOrder> {

    private static final long serialVersionUID = -616321595L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductOrder productOrder = new QProductOrder("productOrder");

    public final EnumPath<DeliveryStatusType> deliveryStatus = createEnum("deliveryStatus", DeliveryStatusType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QOrders orders;

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public final QProduct product;

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public QProductOrder(String variable) {
        this(ProductOrder.class, forVariable(variable), INITS);
    }

    public QProductOrder(Path<? extends ProductOrder> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductOrder(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductOrder(PathMetadata metadata, PathInits inits) {
        this(ProductOrder.class, metadata, inits);
    }

    public QProductOrder(Class<? extends ProductOrder> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.orders = inits.isInitialized("orders") ? new QOrders(forProperty("orders"), inits.get("orders")) : null;
        this.product = inits.isInitialized("product") ? new QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

