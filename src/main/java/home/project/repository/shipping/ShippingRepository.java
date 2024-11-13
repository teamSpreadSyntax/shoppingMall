package home.project.repository.shipping;

import home.project.domain.delivery.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingRepository extends JpaRepository<Shipping, Long>, ShippingRepositoryCustom  {

//    Page<Shipping> findByMemberId(Long memberId, Pageable pageable);

}
