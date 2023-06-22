package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getAllUsers_whenInvoked_thenResponseContainsListOfUserDtos() {
        List<UserResponse> userList = List.of(new UserResponse(), new UserResponse());
        when(userService.getAllUsers()).thenReturn(userList);

        List<UserResponse> responseList = userController.getAllUsers();

        assertEquals(userList.size(), responseList.size());
        verify(userService).getAllUsers();
    }

    @Test
    void findUserById_whenUserFound_thenResponseContainsUserDto() {
        long userId = 1L;
        UserResponse userResponse = new UserResponse();
        when(userService.getUserResponseById(userId)).thenReturn(userResponse);

        UserResponse response = userController.findUserById(userId);

        assertEquals(userResponse, response);
        verify(userService).getUserResponseById(userId);
    }

    @Test
    void create_whenInvoked_thenSaveUser() {
        UserRequest userRequest = new UserRequest();
        UserResponse userResponse = new UserResponse();
        when(userService.createUser(userRequest)).thenReturn(userResponse);

        UserResponse response = userController.create(userRequest);

        assertEquals(userResponse, response);
        verify(userService).createUser(userRequest);
    }

    @Test
    void update_whenInvoked_thenUpdateUser() {
        long userId = 1L;
        UserRequest userRequest = new UserRequest("user100", "user100@yandex.ru");
        UserResponse updatedUserResponse = new UserResponse(1L, "user100", "user100@yandex.ru");
        when(userService.updateUser(userId, userRequest)).thenReturn(updatedUserResponse);

        UserResponse response = userController.update(userRequest, userId);

        assertEquals(updatedUserResponse, response);
        verify(userService).updateUser(userId, userRequest);
    }

    @Test
    void delete_whenInvoked_thenDeleteUser() {
        long userId = 1L;

        userController.delete(userId);

        verify(userService).deleteUser(userId);
    }
}