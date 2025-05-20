package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.DislikeRequestDto;
import ru.practicum.LikeRequestDto;
import ru.practicum.model.EventRating;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RatingServerMapper {

    @Mapping(source = "eventId", target = "eventId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(target = "type", constant = "LIKE") // Устанавливаем тип "LIKE"
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())") // Устанавливаем текущее время
    EventRating toLikeDto(LikeRequestDto likeRequest);

    @Mapping(source = "eventId", target = "eventId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(target = "type", constant = "DISLIKE") // Устанавливаем тип "DISLIKE"
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())") // Устанавливаем текущее время
    EventRating toDislikeDto(DislikeRequestDto dislikeRequest);
}