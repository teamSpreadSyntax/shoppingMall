package home.project.domain.product;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProduct is a Querydsl query type for Product
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProduct extends EntityPathBase<Product> {

    private static final long serialVersionUID = -233207030L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProduct product = new QProduct("product");

    public final StringPath brand = createString("brand");

    public final QCategory category;

    public final StringPath color = createString("color");

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> defectiveStock = createNumber("defectiveStock", Long.class);

    public final ListPath<String, StringPath> description = this.<String, StringPath>createList("description", String.class, StringPath.class, PathInits.DIRECT2);

    public final NumberPath<Integer> discountRate = createNumber("discountRate", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath mainImageFile = createString("mainImageFile");

    public final StringPath name = createString("name");

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public final ListPath<ProductCoupon, QProductCoupon> productCoupons = this.<ProductCoupon, QProductCoupon>createList("productCoupons", ProductCoupon.class, QProductCoupon.class, PathInits.DIRECT2);

    public final StringPath productNum = createString("productNum");

    public final ListPath<ProductOrder, QProductOrder> productOrder = this.<ProductOrder, QProductOrder>createList("productOrder", ProductOrder.class, QProductOrder.class, PathInits.DIRECT2);

    public final StringPath size = createString("size");

    public final NumberPath<Long> soldQuantity = createNumber("soldQuantity", Long.class);

    public final NumberPath<Long> stock = createNumber("stock", Long.class);

    public QProduct(String variable) {
        this(Product.class, forVariable(variable), INITS);
    }

    public QProduct(Path<? extends Product> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProduct(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProduct(PathMetadata metadata, PathInits inits) {
        this(Product.class, metadata, inits);
    }

    public QProduct(Class<? extends Product> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category"), inits.get("category")) : null;
    }

}

