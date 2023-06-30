package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;

    @Override
    public List<UserResponse> getAllUsers() {
        return UserMapper.toUserResponseList(userRepository.findAll());
    }

    @Override
    public User getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.error("Пользователь с ID {} не существует", userId);
            throw new UserNotFoundException(String.format("Пользователь с ID %d не существует", userId));
        }
        return userOptional.get();
    }

    @Override
    public UserResponse getUserResponseById(Long userId) {
        return UserMapper.toUserResponse(getUserById(userId));
    }

    @Transactional
    @Override
    public UserResponse createUser(UserRequest userRequest) {
        User user = UserMapper.toUserEntity(userRequest);
        return UserMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserResponse updateUser(long userId, UserRequest userRequest) {
        User user = getUserById(userId);
        if (userRequest.getName() != null) {
            user.setName(userRequest.getName());
        }
        if (userRequest.getEmail() != null) {
            user.setEmail(userRequest.getEmail());
        }
        return UserMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }
}
