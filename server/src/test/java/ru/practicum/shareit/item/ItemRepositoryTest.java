package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user1;

    private ItemRequest itemRequest2;


    @BeforeEach
    public void init() {
        user1 = userRepository.save(new User("user1", "user1@yandex.ru"));
        User user2 = userRepository.save(new User("user2", "user2@yandex.ru"));

        ItemRequest itemRequest1 = itemRequestRepository.save(ItemRequest.builder()
                .description("Нужен аквариум")
                .requester(user1)
                .build());

        itemRequest2 = itemRequestRepository.save(ItemRequest.builder()
                .description("Нужны рыбки")
                .requester(user2)
                .build());

        itemRepository.save(Item.builder()
                .name("Аквариум")
                .description("Для рыбок")
                .available(true)
                .owner(user1)
                .request(itemRequest1)
                .build());

        itemRepository.save(Item.builder()
                .name("Рыбки")
                .description("Для аквариума")
                .available(true)
                .owner(user1)
                .request(itemRequest2)
                .build());

        itemRepository.save(Item.builder()
                .name("Дрель")
                .description("Чтобы сверлить")
                .available(true)
                .owner(user2)
                .build());
    }

    @Test
    void findAllByOwnerIdOrderByIdAsc() {
        int from = 0;
        int size = 5;
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Item> itemList = itemRepository.findAllByOwnerIdOrderByIdAsc(user1.getId(), page);

        assertEquals(2, itemList.size());
        assertEquals("Аквариум", itemList.get(0).getName());
        assertEquals("Рыбки", itemList.get(1).getName());
    }

    @Test
    void findByNameContainingOrDescriptionContaining() {
        int from = 0;
        int size = 5;
        String text = "рЫб";
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Item> itemList = itemRepository.findByNameContainingOrDescriptionContaining(text.toUpperCase(), page);

        assertEquals(2, itemList.size());
        assertEquals("Аквариум", itemList.get(0).getName());
        assertEquals("Рыбки", itemList.get(1).getName());
    }

    @Test
    void findByRequestIdOrderByRequestCreatedDesc() {
        List<Item> itemList = itemRepository.findByRequestIdOrderByRequestCreatedDesc(itemRequest2.getId());

        assertEquals(1, itemList.size());
        assertEquals("Рыбки", itemList.get(0).getName());
    }
}