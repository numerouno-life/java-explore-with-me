package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dtos.category.CategoryDto;
import ru.practicum.model.Category;

@Mapper
public interface CategoryMapper {

    Category toEntity(CategoryDto categoryDto);

    CategoryDto toDto(Category category);
}
