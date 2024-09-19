package home.project.repository;


import home.project.domain.Category2;
import home.project.domain.Member;
import home.project.dto.CategoryDTOWithoutId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category2, Long> {

}
