package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dtos.category.CategoryDto;
import ru.practicum.dtos.category.NewCategoryDto;
import ru.practicum.service.category.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addNewCategory(@RequestBody @Validated NewCategoryDto newCategoryDto) {
        return categoryService.addNewCategory(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable Long catId) {
        categoryService.deleteCategoryById(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId, @RequestBody @Validated CategoryDto categoryDto) {
        return categoryService.updateCategory(catId, categoryDto);
    }
}
