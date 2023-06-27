package ru.practicum.shareit.comments;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Item item;

    @BeforeEach
    public void init() {
        User user1 = userRepository.save(new User("user1", "user1@yandex.ru"));
        User user2 = userRepository.save(new User("user2", "user2@yandex.ru"));

        item = itemRepository.save(Item.builder()
                .name("Аквариум")
                .description("Для рыбок")
                .available(true)
                .owner(user1)
                .build());

        commentRepository.save(Comment.builder()
                .text("Комментарий к вещи 1 от пользователя 1")
                .item(item)
                .author(user1)
                .build());

        commentRepository.save(Comment.builder()
                .text("Комментарий к вещи 1 от пользователя 2")
                .item(item)
                .author(user2)
                .build());
    }

    @AfterEach
    public void clear() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void findAllByItemId() {
        List<Comment> commentList = commentRepository.findAllByItemId(item.getId());

        assertEquals(2, commentList.size());
        assertEquals("Комментарий к вещи 1 от пользователя 1", commentList.get(0).getText());
        assertEquals("Комментарий к вещи 1 от пользователя 2", commentList.get(1).getText());
    }
}