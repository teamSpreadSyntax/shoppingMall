package home.project.repository.order;

import home.project.domain.order.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long>, CartRepositoryCustom  {
    Page<Cart> findAllByMemberId(Long memberId, Pageable pageable);
    Cart findByMemberId(Long memberId);


}
