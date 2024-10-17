package home.project.repository;

import home.project.domain.Cart;
import home.project.domain.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long>, CartRepositoryCustom  {
    Page<Cart> findAllByMemberId(Long memberId, Pageable pageable);
}
