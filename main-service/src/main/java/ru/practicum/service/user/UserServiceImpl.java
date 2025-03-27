package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dtos.user.NewUserRequestDto;
import ru.practicum.dtos.user.UserDto;
import ru.practicum.error.exception.EntityNotFoundException;
import ru.practicum.error.exception.InvalidStateException;
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
        log.info("Get all users");
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        if (ids == null || ids.isEmpty()) {
            log.warn("List of users is empty");
            return userRepository.findAll(pageable).stream()
                    .map(UserMapper::toUserDto)
                    .toList();
        }
        return userRepository.findAllByIdIn(pageable, ids).stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserRequestDto newUserRequestDto) {
        log.info("Create new user");
        if (userRepository.existsByEmail(newUserRequestDto.getEmail())) {
            log.error("User with email {} already exists", newUserRequestDto.getEmail());
            throw new InvalidStateException("User with email " + newUserRequestDto.getEmail() + " already exists");
        }
        User user = userRepository.save(UserMapper.toUser(newUserRequestDto));
        log.info("User with id {} successfully created", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        log.info("Delete user by id {}", userId);
        if (!userRepository.existsById(userId)) {
            log.error("User with id {} not found", userId);
            throw new EntityNotFoundException("User with id {}" + userId + " not found");
        }
        userRepository.deleteById(userId);
    }
}