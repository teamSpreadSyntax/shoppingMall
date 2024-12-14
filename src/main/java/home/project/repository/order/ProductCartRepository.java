package home.project.repository.order;


import home.project.domain.product.ProductCart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCartRepository extends JpaRepository<ProductCart, Long> {
    void deleteByProductIdAndCart_MemberId(Long productId, Long memberId);
    Page<ProductCart> findByCart_Member_Id(Long memberId, Pageable pageable);

}
