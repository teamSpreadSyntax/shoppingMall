package home.project.repository.order;

import home.project.domain.order.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Long>, OrderRepositoryCustom  {
    Optional<Orders> findByOrderNum(String orderNum);
    Page<Orders> findByMemberId(Long memberId, Pageable pageable);

}
