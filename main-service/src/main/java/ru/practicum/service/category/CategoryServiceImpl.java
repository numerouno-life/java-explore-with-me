package ru.practicum.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dtos.category.CategoryDto;
import ru.practicum.dtos.category.NewCategoryDto;
import ru.practicum.error.exception.DuplicateCategoryException;
import ru.practicum.error.exception.EntityNotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto addNewCategory(NewCategoryDto newCategoryDto) {
        log.info("Add new category");
        try {
            Category category = categoryRepository.save(CategoryMapper.toNewCategoryDto(newCategoryDto));
            return CategoryMapper.toCategoryDto(category);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateCategoryException("Категория с таким именем уже существует");
        }
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long categoryId) {
        log.info("Delete category by id: {}", categoryId);
        if (!categoryRepository.existsById(categoryId)) {
            log.error("Category with id: {} not found", categoryId);
            throw new EntityNotFoundException("Category not found");
        }
        categoryRepository.deleteById(categoryId);
        log.info("Category with ID: '{}' successfully deleted", categoryId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        log.info("Update category by id: {}", categoryId);
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> {
                    log.error("Error updating category: Category with ID '{}' not found", categoryId);
                    return new EntityNotFoundException("Category not found");
                }
        );
        if (categoryRepository.findByName(categoryDto.getName()).isPresent() &&
                !categoryDto.getName().equals(category.getName())) {
            log.error("Error updating category: A category with the name '{}' already exists", categoryDto.getName());
            throw new DuplicateCategoryException("Category already exist");
        }
        Optional.ofNullable(categoryDto.getName()).ifPresent(category::setName);
        category.setId(categoryId);
        log.info("Category with ID '{}' successfully updated", categoryId);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        log.info("Get all categories");
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "id"));
        return categoryRepository.findAll(pageRequest).stream()
                .map(CategoryMapper::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getInfoAboutCategoryById(Long catId) {
        log.info("Get info About Category by ID:{}", catId);
        if (categoryRepository.findById(catId).isPresent()) {
            return CategoryMapper.toCategoryDto(categoryRepository.findById(catId).get());
        } else {
            log.error("Category with ID:{} not found", catId);
            throw new EntityNotFoundException("Category with ID:" + catId + " not found");
        }
    }
}
