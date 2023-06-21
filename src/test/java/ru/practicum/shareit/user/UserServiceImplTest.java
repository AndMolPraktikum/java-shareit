package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    void getAllUsersDto_whenInvoked_thenResponseContainsListOfUsers() {
        List<User> userList = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(userList);

        List<UserDto> response = userServiceImpl.getAllUsersDto();

        assertEquals(userList.size(), response.size());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_whenUserFound_thenResponseContainsUser() {
        long userId = 1L;
        User user = new User(1L, "user", "user@yandex.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User response = userServiceImpl.getUserById(userId);

        assertEquals(user, response);
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_whenUserNotFound_thenUserNotFoundExceptionThrown() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userServiceImpl.getUserById(userId));

        verify(userRepository).findById(userId);
    }

    @Test
    void getUserDtoById_whenUserExist_thenResponseContainsUserDto() {
        long userId = 1L;
        User user = new User(1L, "user", "user@yandex.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto response = userServiceImpl.getUserDtoById(userId);

        assertEquals(UserMapper.toUserDto(user), response);
        verify(userRepository).findById(userId);
    }

    @Test
    void createUser_whenInvoked_thenUserCreate() {
        UserDto userDto = new UserDto();
        User user = new User();
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto response = userServiceImpl.createUser(userDto);

        assertEquals(userDto, response);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_whenUserFound_thenUserUpdate() {
        long userId = 1L;
        UserDto updateUserDto = new UserDto("user100", "user100@yandex.ru");
        User updateUser = new User(1L, "user1", "user1@yandex.ru");
        User updatedUser = new User(1L, "user100", "user100@yandex.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(updateUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto response = userServiceImpl.updateUser(userId, updateUserDto);

        assertEquals(UserMapper.toUserDto(updatedUser), response);
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_whenUpdateIsEmpty_thenUserUpdate() {
        long userId = 1L;
        UserDto updateUserDto = new UserDto(null, null);
        User updateUser = new User(1L, "user1", "user1@yandex.ru");
        User updatedUser = new User(1L, "user1", "user1@yandex.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(updateUser));
        when(userRepository.save(any(User.class))).thenReturn(updateUser);

        UserDto response = userServiceImpl.updateUser(userId, updateUserDto);

        assertEquals(UserMapper.toUserDto(updatedUser), response);
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_whenInvoked_thenUserDeleted() {
        long userId = 1L;

        userServiceImpl.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }
}