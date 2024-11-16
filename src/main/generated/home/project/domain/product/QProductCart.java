package home.project.domain.product;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductCart is a Querydsl query type for ProductCart
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductCart extends EntityPathBase<ProductCart> {

    private static final long serialVersionUID = -452401846L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductCart productCart = new QProductCart("productCart");

    public final home.project.domain.order.QCart cart;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QProduct product;

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public QProductCart(String variable) {
        this(ProductCart.class, forVariable(variable), INITS);
    }

    public QProductCart(Path<? extends ProductCart> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductCart(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductCart(PathMetadata metadata, PathInits inits) {
        this(ProductCart.class, metadata, inits);
    }

    public QProductCart(Class<? extends ProductCart> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.cart = inits.isInitialized("cart") ? new home.project.domain.order.QCart(forProperty("cart"), inits.get("cart")) : null;
        this.product = inits.isInitialized("product") ? new QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

