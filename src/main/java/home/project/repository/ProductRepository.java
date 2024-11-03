package home.project.repository;


import home.project.domain.Product;
import home.project.domain.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    boolean existsByProductNum(String productNum);

    Product findByProductNum(String productNum);

    Page<Product> findBySeller(Seller seller, Pageable pageable);



}
