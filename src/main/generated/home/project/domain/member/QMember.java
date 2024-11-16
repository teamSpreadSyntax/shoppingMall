package home.project.domain.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1921105736L;

    public static final QMember member = new QMember("member1");

    public final NumberPath<Long> accumulatedPurchase = createNumber("accumulatedPurchase", Long.class);

    public final DatePath<java.time.LocalDate> birthDate = createDate("birthDate", java.time.LocalDate.class);

    public final StringPath defaultAddress = createString("defaultAddress");

    public final StringPath email = createString("email");

    public final EnumPath<MemberGenderType> gender = createEnum("gender", MemberGenderType.class);

    public final EnumPath<MemberGradeType> grade = createEnum("grade", MemberGradeType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<home.project.domain.product.MemberCoupon, home.project.domain.product.QMemberCoupon> memberCoupons = this.<home.project.domain.product.MemberCoupon, home.project.domain.product.QMemberCoupon>createList("memberCoupons", home.project.domain.product.MemberCoupon.class, home.project.domain.product.QMemberCoupon.class, PathInits.DIRECT2);

    public final ListPath<MemberProduct, QMemberProduct> memberProducts = this.<MemberProduct, QMemberProduct>createList("memberProducts", MemberProduct.class, QMemberProduct.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final ListPath<home.project.domain.notification.Notification, home.project.domain.notification.QNotification> notifications = this.<home.project.domain.notification.Notification, home.project.domain.notification.QNotification>createList("notifications", home.project.domain.notification.Notification.class, home.project.domain.notification.QNotification.class, PathInits.DIRECT2);

    public final ListPath<home.project.domain.order.Orders, home.project.domain.order.QOrders> orders = this.<home.project.domain.order.Orders, home.project.domain.order.QOrders>createList("orders", home.project.domain.order.Orders.class, home.project.domain.order.QOrders.class, PathInits.DIRECT2);

    public final StringPath password = createString("password");

    public final StringPath phone = createString("phone");

    public final NumberPath<Long> point = createNumber("point", Long.class);

    public final EnumPath<RoleType> role = createEnum("role", RoleType.class);

    public final StringPath secondAddress = createString("secondAddress");

    public final StringPath thirdAddress = createString("thirdAddress");

    public final ListPath<home.project.domain.common.WishList, home.project.domain.common.QWishList> wishLists = this.<home.project.domain.common.WishList, home.project.domain.common.QWishList>createList("wishLists", home.project.domain.common.WishList.class, home.project.domain.common.QWishList.class, PathInits.DIRECT2);

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

