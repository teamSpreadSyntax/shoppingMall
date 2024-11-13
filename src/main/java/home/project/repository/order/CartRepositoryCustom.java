package home.project.repository.order;

import home.project.domain.order.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CartRepositoryCustom {
    Page<Cart> findAllByMemberId(Long memberId, Pageable pageable);
}