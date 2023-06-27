package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
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

        List<UserResponse> response = userServiceImpl.getAllUsers();

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

        UserResponse response = userServiceImpl.getUserResponseById(userId);

        assertEquals(UserMapper.toUserResponse(user), response);
        verify(userRepository).findById(userId);
    }

    @Test
    void createUser_whenInvoked_thenUserCreate() {
        UserRequest userRequest = new UserRequest();
        UserResponse userResponse = new UserResponse();
        User user = new User();
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userServiceImpl.createUser(userRequest);

        assertEquals(userResponse, response);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_whenUserFound_thenUserUpdate() {
        long userId = 1L;
        UserRequest userRequest = new UserRequest("user100", "user100@yandex.ru");
        User updateUser = new User(1L, "user1", "user1@yandex.ru");
        User updatedUser = new User(1L, "user100", "user100@yandex.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(updateUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserResponse response = userServiceImpl.updateUser(userId, userRequest);

        assertEquals(UserMapper.toUserResponse(updatedUser), response);
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_whenUpdateIsEmpty_thenUserUpdate() {
        long userId = 1L;
        UserRequest userRequest = new UserRequest(null, null);
        User updateUser = new User(1L, "user1", "user1@yandex.ru");
        User updatedUser = new User(1L, "user1", "user1@yandex.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(updateUser));
        when(userRepository.save(any(User.class))).thenReturn(updateUser);

        UserResponse response = userServiceImpl.updateUser(userId, userRequest);

        assertEquals(UserMapper.toUserResponse(updatedUser), response);
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