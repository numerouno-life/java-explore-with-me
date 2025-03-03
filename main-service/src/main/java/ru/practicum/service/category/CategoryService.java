package ru.practicum.service.category;

import ru.practicum.dtos.category.CategoryDto;
import ru.practicum.dtos.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto addNewCategory(NewCategoryDto newCategoryDto);

    void deleteCategoryById(Long categoryId);

    CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto);

    List<CategoryDto> getAllCategories(Integer from, Integer size);

    CategoryDto getInfoAboutCategoryById(Long catId);
}
