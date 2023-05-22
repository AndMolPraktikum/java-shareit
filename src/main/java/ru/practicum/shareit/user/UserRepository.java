package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    List<User> findAll();

    User get(long userId);

    User create(User user);

    User update(long userId, User user);

    void delete(long userId);
}
