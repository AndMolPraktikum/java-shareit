package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.CommentRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserHaveNoSuchItemException;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBooking;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.request.ItemRequestService;
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
    private final UserService userService;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final BookingRepository bookingRepository;

    @Autowired
    private final CommentRepository commentRepository;

    @Autowired
    private final ItemRequestService itemRequestService;

    @Override
    public ItemDtoOut getItemDtoByIdForAll(long itemId, long userId) {
        Item item = getItemById(itemId);
        ItemWithBooking itemWithBooking = ItemMapper.toItemWithBooking(item);
        if (item.getOwner().getId() == userId) {
            setBookings(itemWithBooking);
        }
        itemWithBooking.setComments(commentRepository.findAllByItemId(itemId));
        return ItemMapper.toItemWithBookingDto(itemWithBooking);
    }

    @Override
    public List<ItemDtoOut> getAllUserItemsDto(Long userId, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Item> response = itemRepository.findAllByOwnerIdOrderByIdAsc(userId, page);
        List<ItemWithBooking> itemsWithBookingList = response.stream()
                .map(ItemMapper::toItemWithBooking)
                .collect(Collectors.toList());
        itemsWithBookingList.forEach(this::setBookings);
        return ItemMapper.toItemWithBookingDtoList(itemsWithBookingList);
    }

    @Override
    public List<ItemDtoOut> searchItemDtoByText(String text, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        if (text.length() == 0 || text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> response = itemRepository
                .findByNameContainingOrDescriptionContaining(text.toUpperCase(), page);
        return ItemMapper.toItemDtoOutList(response);
    }

    @Transactional
    @Override
    public ItemDtoOut createItem(Long userId, ItemDtoIn itemDtoIn) {
        Item item = ItemMapper.toEntity(itemDtoIn);
        if (itemDtoIn.getRequestId() != null) {
            item.setRequest(itemRequestService.getItemRequestById(itemDtoIn.getRequestId()));
        }
        item.setOwner(userService.getUserById(userId));
        return ItemMapper.toItemDtoOut(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDtoOut updateItem(long userId, long itemId, ItemDtoIn updateItemDtoIn) {
        Item updateItem = ItemMapper.toEntity(updateItemDtoIn);
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
        return ItemMapper.toItemDtoOut(itemRepository.save(item));
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
    public ItemDtoOut getItemDtoById(long itemId) {
        return ItemMapper.toItemDtoOut(getItemById(itemId));
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
