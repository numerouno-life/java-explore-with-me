package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dtos.user.NewUserRequestDto;
import ru.practicum.dtos.user.UserDto;
import ru.practicum.dtos.user.UserShortDto;
import ru.practicum.model.User;

@UtilityClass
public class UserMapper {

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public User toUser(NewUserRequestDto newUserRequestDtoDto) {
        return User.builder()
                .name(newUserRequestDtoDto.getName())
                .email(newUserRequestDtoDto.getEmail())
                .build();
    }
}
