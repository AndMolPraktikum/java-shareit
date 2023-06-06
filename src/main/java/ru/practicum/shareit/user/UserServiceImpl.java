package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
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
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.error("Пользователь с ID {} не существует", userId);
            throw new UserNotFoundException(String.format("Пользователь с ID %d не существует", userId));
        }
        return userOptional.get();
    }

    @Transactional
    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User updateUser(long userId, User updateUser) {
        User user = getUserById(userId);
        if (updateUser.getName() != null) {
            user.setName(updateUser.getName());
        }
        if (updateUser.getEmail() != null) {
            user.setEmail(updateUser.getEmail());
        }
        updateUser.setId(userId);
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }
}
