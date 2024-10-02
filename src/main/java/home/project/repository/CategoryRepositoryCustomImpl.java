//package home.project.repository;
//
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import home.project.domain.Category;
//import home.project.domain.QCategory;
//import jakarta.persistence.EntityManager;
//
//import java.util.List;
//
//public class CategoryRepositoryCustomImpl  implements CategoryRepositoryCustom {
//    private final JPAQueryFactory queryFactory;
//    private final QCategory category = QCategory.category;
//    public CategoryRepositoryCustomImpl(EntityManager em) {
//        this.queryFactory = new JPAQueryFactory(em);
//    }
//
//    @Override
//    public List<Category> findDirectChildCategories(String code, int length) {
//        return queryFactory
//                .selectFrom(category)
//                .where(category.code.startsWith(code)
//                        .and(category.code.length().eq(length)))
//                .fetch();
//    }
//
//}
