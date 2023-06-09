package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
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
    public List<UserDto> getAllUsersDto() {
        return UserMapper.toUserDtoList(userRepository.findAll());
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

    @Override
    public UserDto getUserDtoById(Long userId) {
        return UserMapper.toUserDto(getUserById(userId));
    }

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUserEntity(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserDto updateUser(long userId, UserDto updateUserDto) {
        User user = getUserById(userId);
        if (updateUserDto.getName() != null) {
            user.setName(updateUserDto.getName());
        }
        if (updateUserDto.getEmail() != null) {
            user.setEmail(updateUserDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }
}
