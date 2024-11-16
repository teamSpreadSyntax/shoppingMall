package home.project.domain.order;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrders is a Querydsl query type for Orders
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrders extends EntityPathBase<Orders> {

    private static final long serialVersionUID = 1985547179L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrders orders = new QOrders("orders");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final home.project.domain.member.QMember member;

    public final DateTimePath<java.time.LocalDateTime> orderDate = createDateTime("orderDate", java.time.LocalDateTime.class);

    public final StringPath orderNum = createString("orderNum");

    public final NumberPath<Long> pointsEarned = createNumber("pointsEarned", Long.class);

    public final NumberPath<Long> pointsUsed = createNumber("pointsUsed", Long.class);

    public final ListPath<home.project.domain.product.ProductOrder, home.project.domain.product.QProductOrder> productOrders = this.<home.project.domain.product.ProductOrder, home.project.domain.product.QProductOrder>createList("productOrders", home.project.domain.product.ProductOrder.class, home.project.domain.product.QProductOrder.class, PathInits.DIRECT2);

    public final home.project.domain.delivery.QShipping shipping;

    public QOrders(String variable) {
        this(Orders.class, forVariable(variable), INITS);
    }

    public QOrders(Path<? extends Orders> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrders(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrders(PathMetadata metadata, PathInits inits) {
        this(Orders.class, metadata, inits);
    }

    public QOrders(Class<? extends Orders> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new home.project.domain.member.QMember(forProperty("member")) : null;
        this.shipping = inits.isInitialized("shipping") ? new home.project.domain.delivery.QShipping(forProperty("shipping"), inits.get("shipping")) : null;
    }

}

