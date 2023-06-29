package ru.practicum.shareit.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMapper {

    public static User toUserEntity(UserRequest userRequest) {
        return new User(
                userRequest.getName(),
                userRequest.getEmail()
        );
    }

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static List<UserResponse> toUserResponseList(List<User> userList) {
        return userList.stream()
                .map(UserMapper::toUserResponse)
                .collect(Collectors.toList());
    }
}
