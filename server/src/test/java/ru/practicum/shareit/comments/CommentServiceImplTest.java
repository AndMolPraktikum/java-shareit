package ru.practicum.shareit.comments;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.dto.CommentRequest;
import ru.practicum.shareit.comments.dto.CommentResponse;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.NoCompletedBookingsException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.CommentMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentServiceImpl;


    @Test
    void create_whenUserAndItemAndCommentFound_thenCommentCreate() {
        long itemId = 1L;
        long bookerId = 2L;
        User user = new User(1L, "user", "user@yandex.ru");
        User user2 = new User(2L, "booker", "user2@yandex.ru");
        Item item = new Item(1L, "Кухонный комбайн", "Готовит сам", true, user, null);
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().minusSeconds(10),
                LocalDateTime.now().minusSeconds(1),
                item,
                new User(2L, "booker", "booker@yandex.ru"),
                BookingStatus.APPROVED
        );
        CommentRequest commentRequest = new CommentRequest("Комментарий к вещи 1");
        Comment comment =
                new Comment(1L, "Комментарий к вещи 1", item, user2, LocalDateTime.now());
        when(userService.getUserById(bookerId)).thenReturn(user);
        when(itemService.getItemById(itemId)).thenReturn(item);
        when(bookingRepository.findAllByBookerIdAndItemIdAndAfterEnd(bookerId, itemId)).thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponse response = commentServiceImpl.create(itemId, bookerId, commentRequest);

        assertEquals(CommentMapper.toCommentResponse(comment), response);
        verify(userService).getUserById(bookerId);
        verify(itemService).getItemById(itemId);
        verify(bookingRepository).findAllByBookerIdAndItemIdAndAfterEnd(bookerId, itemId);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void create_whenCommentNotFound_NoCompletedBookingsException() {
        long itemId = 1L;
        long bookerId = 2L;
        User user = new User(1L, "user", "user@yandex.ru");
        Item item = new Item(1L, "Кухонный комбайн", "Готовит сам", true, user, null);
        CommentRequest commentRequest = new CommentRequest("Комментарий к вещи 1");

        when(userService.getUserById(bookerId)).thenReturn(user);
        when(itemService.getItemById(itemId)).thenReturn(item);
        when(bookingRepository.findAllByBookerIdAndItemIdAndAfterEnd(bookerId, itemId))
                .thenReturn(Collections.emptyList());

        assertThrows(NoCompletedBookingsException.class,
                () -> commentServiceImpl.create(itemId, bookerId, commentRequest));

        verify(userService).getUserById(bookerId);
        verify(itemService).getItemById(itemId);
        verify(bookingRepository).findAllByBookerIdAndItemIdAndAfterEnd(bookerId, itemId);
        verify(commentRepository, never()).save(any(Comment.class));
    }
}