package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;

    @BeforeEach
    public void init() {
        user1 = userRepository.save(new User("user1", "user1@yandex.ru"));
        User user2 = userRepository.save(new User("user2", "user2@yandex.ru"));

        itemRequestRepository.save(ItemRequest.builder()
                .description("Нужен аквариум")
                .requester(user1)
                .build());

        itemRequestRepository.save(ItemRequest.builder()
                .description("Нужны рыбки")
                .requester(user2)
                .build());
    }

    @AfterEach
    public void clearBase() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void findAllByRequesterIdOrderByCreatedDesc_whenInvoked_thenResponseContainsListWithUser1() {
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(user1.getId());

        assertEquals(1, itemRequestList.size());
        assertEquals("Нужен аквариум", itemRequestList.get(0).getDescription());
    }

    @Test
    void findAllByRequesterIdNotOrderByCreatedDesc_whenInvoked_thenResponseContainsListWithUser2() {
        int from = 0;
        int size = 5;
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemRequest> itemRequestList = itemRequestRepository
                .findAllByRequesterIdNotOrderByCreatedDesc(page, user1.getId());

        assertEquals(1, itemRequestList.size());
        assertEquals("Нужны рыбки", itemRequestList.get(0).getDescription());
    }
}