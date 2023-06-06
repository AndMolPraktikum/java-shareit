package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.CommentRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserHaveNoSuchItemException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBooking;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private final UserService userService;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final BookingRepository bookingRepository;

    @Autowired
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemWithBooking getItemByIdForAll(long itemId, long userId) {
        Item item = getItemById(itemId);
        ItemWithBooking itemWithBooking = modelMapper.map(item, ItemWithBooking.class);
        if (item.getOwner().getId() == userId) {
            setBookings(itemWithBooking);
        }
        itemWithBooking.setComments(commentRepository.findAllByItemId(itemId));
        return itemWithBooking;
    }

    @Override
    public List<ItemWithBooking> getAllUserItems(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(userId);
        List<ItemWithBooking> itemsWithBooking = items.stream()
                .map(item -> modelMapper.map(item, ItemWithBooking.class))
                .collect(Collectors.toList());
        itemsWithBooking.forEach(this::setBookings);

        return itemsWithBooking;
    }

    @Override
    public List<Item> searchItemByText(String text) {
        if (text.length() == 0 || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findByNameContainingOrDescriptionContaining(text.toUpperCase());
    }

    @Transactional
    @Override
    public Item createItem(Long userId, Item item) {
        item.setOwner(userService.getUserById(userId));
        return itemRepository.save(item);
    }

    @Transactional
    @Override
    public Item updateItem(long userId, long itemId, Item updateItem) {
        userService.getUserById(userId);
        Item item = checkUserItem(userId, itemId);
        if (updateItem.getName() != null) {
            item.setName(updateItem.getName());
        }
        if (updateItem.getDescription() != null) {
            item.setDescription(updateItem.getDescription());
        }
        if (updateItem.getAvailable() != null) {
            item.setAvailable(updateItem.getAvailable());
        }
        return itemRepository.save(item);
    }

    @Transactional
    @Override
    public void deleteItem(long userId, long itemId) {
        checkUserItem(userId, itemId);
        itemRepository.deleteById(itemId);
    }

    @Override
    public Item checkUserItem(long userId, long itemId) {
        Item item = getItemById(itemId);
        if (item.getOwner().getId() != userId) {
            log.error("Пользователь с ID: {} не является владельцем вещи с ID: {}", userId, itemId);
            throw new UserHaveNoSuchItemException(
                    String.format("Пользователь с ID: %d не является владельцем вещи с ID: %d", userId, itemId));
        }
        return item;
    }

    @Override
    public Item getItemById(long itemId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            log.error("Вещь с ID {} не существует", itemId);
            throw new ItemNotFoundException(String.format("Вещь с ID %d не существует", itemId));
        }
        return itemOptional.get();
    }

    private void setBookings(ItemWithBooking itemWithBooking) {
        long itemId = itemWithBooking.getId();
        List<Booking> last = bookingRepository.findLastBookingForItem(itemId);
        List<Booking> next = bookingRepository.findNextBookingForItem(itemId);
        itemWithBooking.setLastBooking(last.size() != 0 ? last.get(0) : null);
        if (last.size() != 0) {
            itemWithBooking.setNextBooking(next.size() != 0 ? next.get(0) : null);
        }
    }
}
