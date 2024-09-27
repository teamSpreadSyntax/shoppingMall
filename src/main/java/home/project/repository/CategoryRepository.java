package home.project.repository;


import home.project.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCode(String categoryCode);

    boolean existsByName(String name);

    boolean existsByCode(String categoryCode);
}
