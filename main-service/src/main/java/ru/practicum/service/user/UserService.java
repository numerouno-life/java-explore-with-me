package ru.practicum.service.user;

import ru.practicum.dtos.user.NewUserRequestDto;
import ru.practicum.dtos.user.UserDto;
import ru.practicum.model.User;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers(List<Integer> ids, Integer from, Integer size);

    UserDto createUser(NewUserRequestDto newUserRequestDtoDto);

    void deleteUserById(Long userId);

    User findUserById(Long userId);
}
