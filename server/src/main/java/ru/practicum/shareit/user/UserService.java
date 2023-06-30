package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();

    UserResponse getUserResponseById(Long id);

    User getUserById(Long userId);

    UserResponse createUser(UserRequest userRequest);

    UserResponse updateUser(long key, UserRequest userRequest);

    void deleteUser(long userId);
}
