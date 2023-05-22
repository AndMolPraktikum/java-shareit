package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class UserRepositoryInMemory implements UserRepository {

    private final Map<Long, User> userMap = new HashMap<>();
    private long id = 0;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User get(long id) {
        User user = userMap.get(id);

        if (user == null) {
            log.error("Пользователь с ID {} не существует", id);
            throw new UserNotFoundException(String.format("Пользователь с ID %d не существует", id));
        }

        return user;
    }

    @Override
    public User create(User user) {
        id++;
        user.setId(id);
        userMap.put(id, user);
        return userMap.get(id);
    }

    @Override
    public User update(long key, User user) {
        userMap.put(key, user);
        return userMap.get(key);
    }

    @Override
    public void delete(long userId) {
        userMap.remove(userId);
    }
}
