package home.project.repository.product;

import home.project.domain.common.WishList;
import home.project.domain.member.Member;
import home.project.domain.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    WishList findByMemberIdAndProductId(Long memberId, Long productId);

    void deleteByMemberAndProduct(Member member, Product product);

    Page<WishList> findAllByMemberId(Long memberId, Pageable pageable);

    List<WishList> findByMemberId(Long memberId);


    @Query("SELECT w.product.id FROM WishList w WHERE w.member.id = :memberId")
    List<Long> findProductIdsByMemberId(@Param("memberId") Long memberId);

}
