package events.category;

import org.springframework.data.domain.Pageable;
import events.category.dto.CategoryDto;
import events.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(long catId, NewCategoryDto newCategoryDto);

    CategoryDto getCategoryById(long catId);

    List<CategoryDto> getCategories(Pageable pageable);

    void deleteCategory(long catId);
}
