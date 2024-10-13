package home.project.domain;

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

    private static final long serialVersionUID = 1496892736L;

    public static final QMember member = new QMember("member1");

    public final NumberPath<Long> accumulatedPurchase = createNumber("accumulatedPurchase", Long.class);

    public final DatePath<java.time.LocalDate> birthDate = createDate("birthDate", java.time.LocalDate.class);

    public final StringPath defaultAddress = createString("defaultAddress");

    public final StringPath email = createString("email");

    public final EnumPath<MemberGender> gender = createEnum("gender", MemberGender.class);

    public final EnumPath<MemberGrade> grade = createEnum("grade", MemberGrade.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<MemberCoupon, QMemberCoupon> memberCoupons = this.<MemberCoupon, QMemberCoupon>createList("memberCoupons", MemberCoupon.class, QMemberCoupon.class, PathInits.DIRECT2);

    public final ListPath<MemberEvent, QMemberEvent> memberEvents = this.<MemberEvent, QMemberEvent>createList("memberEvents", MemberEvent.class, QMemberEvent.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final ListPath<Order, QOrder> order = this.<Order, QOrder>createList("order", Order.class, QOrder.class, PathInits.DIRECT2);

    public final StringPath password = createString("password");

    public final StringPath phone = createString("phone");

    public final NumberPath<Long> point = createNumber("point", Long.class);

    public final EnumPath<RoleType> role = createEnum("role", RoleType.class);

    public final StringPath secondAddress = createString("secondAddress");

    public final StringPath thirdAddress = createString("thirdAddress");

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

