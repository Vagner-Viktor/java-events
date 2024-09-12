package events.category.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import events.category.model.Category;

import java.util.Optional;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Long>, JpaRepository<Category, Long> {
    boolean existsByName(String name);

    Optional<Category> findByName(String name);
}
