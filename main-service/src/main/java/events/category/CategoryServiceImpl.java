package events.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import events.category.dao.CategoryRepository;
import events.category.dto.CategoryDto;
import events.category.dto.NewCategoryDto;
import events.category.model.Category;
import events.event.dao.EventRepository;
import events.exception.DataIntegrityViolationException;
import events.exception.NotFoundException;
import events.exception.ValidationException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new DataIntegrityViolationException("Category's name is exists!");
        }
        return CategoryMapper.toCategoryDto(
                categoryRepository.findByName(newCategoryDto.getName()).orElse(
                        categoryRepository.save(new Category(null, newCategoryDto.getName(), null)))
        );
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(long catId, NewCategoryDto newCategoryDto) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Category (id = " + catId + ") not found!");
        }
        Category category = categoryRepository.findByName(newCategoryDto.getName()).orElse(null);
        if (category != null && category.getId() != catId) {
            throw new DataIntegrityViolationException("Category's name is exists!");
        }
        if (newCategoryDto.getName().length() < 1 ||
                newCategoryDto.getName().length() > 50) {
            throw new ValidationException("Name length must be >=1 and <=50!");
        }
        return CategoryMapper.toCategoryDto(
                categoryRepository.save(new Category(catId, newCategoryDto.getName(), null))
        );
    }

    @Override
    public CategoryDto getCategoryById(long catId) {
        return CategoryMapper.toCategoryDto(
                categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Category (id = " + catId + ") not found!"))
        );
    }

    @Override
    public List<CategoryDto> getCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).get()
                .map(CategoryMapper::toCategoryDto)
                .toList();
    }

    @Transactional
    @Override
    public void deleteCategory(long catId) {
        if (eventRepository.existsByCategoryId(catId)) {
            throw new DataIntegrityViolationException("Category is in use!");
        }
        categoryRepository.deleteById(catId);
    }
}
