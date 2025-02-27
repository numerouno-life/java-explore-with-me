package ru.practicum.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dtos.user.NewUserRequestDto;
import ru.practicum.dtos.user.UserDto;
import ru.practicum.service.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers(@RequestParam(required = false) List<Integer> ids,
                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        return userService.getAllUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid NewUserRequestDto newUserRequestDtoDto) {
        return userService.createUser(newUserRequestDtoDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
    }
}
