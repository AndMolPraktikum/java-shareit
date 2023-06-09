package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsersDto();

    UserDto getUserDtoById(Long id);

    User getUserById(Long userId);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(long key, UserDto userDto);

    void deleteUser(long userId);
}
