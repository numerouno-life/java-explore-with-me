package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dtos.user.NewUserRequestDto;
import ru.practicum.dtos.user.UserDto;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    @Override
    public List<UserDto> getAllUsers(List<Integer> ids, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        if (ids.isEmpty()) {
            log.info("Get all users without ids");
            return userRepository.findAll(pageRequest).getContent().stream()
                    .map(UserMapper::toUserDto)
                    .toList();
        } else {
            log.info("Get all users with ids");
            return userRepository.findAllByIdIn(pageRequest, ids).getContent().stream()
                    .map(UserMapper::toUserDto)
                    .toList();
        }
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserRequestDto newUserRequestDto) {
        log.info("Create new user");
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(newUserRequestDto)));
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        log.info("Delete user by id {}", userId);
        userRepository.deleteById(userId);
    }
}