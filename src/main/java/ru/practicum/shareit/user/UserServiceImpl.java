package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserAlreadyExistException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.get(userId);
    }

    @Override
    public User createUser(User user) {
        checkEmail(user.getEmail());
        return userRepository.create(user);
    }

    @Override
    public User updateUser(long userId, User updateUser) {
        User user = getUserById(userId);
        if (updateUser.getName() != null) {
            user.setName(updateUser.getName());
        }
        if (updateUser.getEmail() != null && !user.getEmail().equals(updateUser.getEmail())) {
            checkEmail(updateUser.getEmail());
            user.setEmail(updateUser.getEmail());
        }
        return userRepository.update(userId, user);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.delete(userId);
    }

    private void checkEmail(String email) {
        List<User> users = getAllUsers().stream()
                .filter(u -> u.getEmail().equals(email))
                .collect(Collectors.toList());
        if (users.size() != 0) {
            log.error("Пользователь с Email {} уже существует", email);
            throw new UserAlreadyExistException(String.format("Пользователь с ID %s уже существует", email));
        }
    }
}
