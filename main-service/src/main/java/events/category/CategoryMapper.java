package events.category;

import events.category.dto.CategoryDto;
import events.category.model.Category;

import java.util.List;

public class CategoryMapper {

    public static List<CategoryDto> toCategoryDtoList(List<Category> categories) {
        if (categories.isEmpty()) return List.of();
        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .toList();
    }

    public static CategoryDto toCategoryDto(Category category) {
        if (category == null) return null;
        return new CategoryDto(category.getId(), category.getName());
    }
}
