package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.CommentRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserHaveNoSuchItemException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBooking;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    @Autowired
    private final UserService userService;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final BookingRepository bookingRepository;

    @Autowired
    private final CommentRepository commentRepository;

    @Override
    public ItemWithBookingDto getItemDtoByIdForAll(long itemId, long userId) {
        Item item = getItemById(itemId);
        ItemWithBooking itemWithBooking = ItemMapper.toItemWithBooking(item);
        if (item.getOwner() != null && item.getOwner().getId() == userId) {
            setBookings(itemWithBooking);
        }
        itemWithBooking.setComments(commentRepository.findAllByItemId(itemId));
        return ItemMapper.toItemWithBookingDto(itemWithBooking);
    }

    @Override
    public List<ItemWithBookingDto> getAllUserItemsDto(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(userId);
        List<ItemWithBooking> itemsWithBookingList = ItemMapper.toItemWithBookingList(items);
        itemsWithBookingList.forEach(this::setBookings);
        return ItemMapper.toItemWithBookingDtoList(itemsWithBookingList);
    }

    @Override
    public List<ItemDto> searchItemDtoByText(String text) {
        if (text.length() == 0 || text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.findByNameContainingOrDescriptionContaining(text.toUpperCase());
        return ItemMapper.toItemDtoList(items);
    }

    @Transactional
    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toEntity(itemDto);
        item.setOwner(userService.getUserById(userId));
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto updateItemDto) {
        Item updateItem = ItemMapper.toEntity(updateItemDto);
        userService.getUserDtoById(userId);
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
        return ItemMapper.toItemDto(itemRepository.save(item));
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
        if (item.getOwner() != null && item.getOwner().getId() != userId) {
            log.error("Пользователь с ID: {} не является владельцем вещи с ID: {}", userId, itemId);
            throw new UserHaveNoSuchItemException(
                    String.format("Пользователь с ID: %d не является владельцем вещи с ID: %d", userId, itemId));
        }
        return item;
    }

    @Override
    public ItemDto getItemDtoById(long itemId) { //#Done
        return ItemMapper.toItemDto(getItemById(itemId));
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
