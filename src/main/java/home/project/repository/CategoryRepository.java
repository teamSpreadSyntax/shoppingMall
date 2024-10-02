package home.project.repository;


import home.project.domain.Category;
import home.project.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.code LIKE :code% AND LENGTH(c.code) = :length")
    List<Category> findDirectChildCategories(@Param("code") String code, @Param("length") int length);

    Optional<Category> findByCode(String categoryCode);

    boolean existsByName(String name);

    boolean existsByCode(String categoryCode);

    List<Category> findAllByCodeStartingWith(String category);
}
