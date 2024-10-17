package home.project.repository;

import home.project.domain.Cart;
import home.project.domain.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CartRepositoryCustom {
    Page<Cart> findAllByMemberId(Long memberId, Pageable pageable);
}