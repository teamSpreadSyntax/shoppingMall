package home.project.repository;

import home.project.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    WishList findByMemberIdAndProductId(Long memberId, Long productId);

    void deleteByMemberAndProduct(Member member, Product product);

    Page<WishList> findAllByMemberId(Long memberId, Pageable pageable);

}
