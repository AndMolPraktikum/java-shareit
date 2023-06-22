package ru.practicum.shareit.comments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.dto.CommentRequest;
import ru.practicum.shareit.comments.dto.CommentResponse;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.exception.NoCompletedBookingsException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.CommentMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private final CommentRepository commentRepository;

    @Autowired
    private final UserService userService;

    @Autowired
    private final ItemService itemService;

    @Autowired
    private final BookingRepository bookingRepository;


    @Transactional
    @Override
    public CommentResponse create(long itemId, long userId, CommentRequest commentRequest) {
        Comment comment = CommentMapper.toCommentEntity(commentRequest);
        User author = userService.getUserById(userId);
        Item item = itemService.getItemById(itemId);

        checkBookingItem(itemId, userId);
        comment.setAuthor(author);
        comment.setItem(item);
        return CommentMapper.toCommentResponse(commentRepository.save(comment));
    }

    private void checkBookingItem(long itemId, long userId) {
        List<Booking> bookingsByItemIdAndUserId = bookingRepository
                .findAllByBookerIdAndItemIdAndAfterEnd(userId, itemId);
        if (bookingsByItemIdAndUserId.size() == 0) {
            log.error("У пользователя с ID: {} " +
                    "нет завершенных бронирований по вещи с ID: {}}", userId, itemId);
            throw new NoCompletedBookingsException(String.format("У пользователя с ID: %d " +
                    "нет завершенных бронирований по вещи с ID: %d", userId, itemId));
        }
    }
}
