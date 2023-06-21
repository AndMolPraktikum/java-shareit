package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;

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
        List<UserDto> userList = List.of(new UserDto(), new UserDto());
        when(userService.getAllUsersDto()).thenReturn(userList);

        List<UserDto> responseList = userController.getAllUsers();

        assertEquals(userList.size(), responseList.size());
        verify(userService).getAllUsersDto();
    }

    @Test
    void findUserById_whenUserFound_thenResponseContainsUserDto() {
        long userId = 1L;
        UserDto userDto = new UserDto();
        when(userService.getUserDtoById(userId)).thenReturn(userDto);

        UserDto response = userController.findUserById(userId);

        assertEquals(userDto, response);
        verify(userService).getUserDtoById(userId);
    }

    @Test
    void create_whenInvoked_thenSaveUser() {
        UserDto userDto = new UserDto();
        when(userService.createUser(userDto)).thenReturn(userDto);

        UserDto response = userController.create(userDto);

        assertEquals(userDto, response);
        verify(userService).createUser(userDto);
    }

    @Test
    void update_whenInvoked_thenUpdateUser() {
        long userId = 1L;
        UserDto updateUserDto = new UserDto("user100", "user100@yandex.ru");
        UserDto updatedUserDto = new UserDto(1L, "user100", "user100@yandex.ru");
        when(userService.updateUser(userId, updateUserDto)).thenReturn(updatedUserDto);

        UserDto response = userController.update(updateUserDto, userId);

        assertEquals(updatedUserDto, response);
        verify(userService).updateUser(userId, updateUserDto);
    }

    @Test
    void delete_whenInvoked_thenDeleteUser() {
        long userId = 1L;

        userController.delete(userId);

        verify(userService).deleteUser(userId);
    }
}