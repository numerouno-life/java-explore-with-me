package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dtos.user.UserDto;
import ru.practicum.dtos.user.UserShortDto;
import ru.practicum.model.User;

import java.util.List;

@Mapper
public interface UserMapper {

    UserDto toUserDto(User user);

    User toEntity(UserDto userDto);

    UserShortDto toUserShortDto(User user);

    List<UserDto> toUserDtoList(List<User> users);

    List<UserShortDto> toUserShortDtoList(List<User> users);

}
