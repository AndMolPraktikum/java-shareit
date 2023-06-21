package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comments.CommentRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserHaveNoSuchItemException;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBooking;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    BookingRepository bookingRepository;

    @InjectMocks
    private ItemServiceImpl itemServiceImpl;

    @Test
    void getItemDtoByIdForAll_whenInvoked_thenResponseWithItemDtoOut() {
        long userId = 1L;
        long itemId = 1L;
        ItemWithBooking itemOut = new ItemWithBooking(1L, "Садовая тачка",
                "Возит сама", true, null, null, new ArrayList<>());
        Item item = new Item(1L, "Садовая тачка", "Возит сама", true);
        when(commentRepository.findAllByItemId(itemId)).thenReturn(Collections.emptyList());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDtoOut response = itemServiceImpl.getItemDtoByIdForAll(itemId, userId);

        assertEquals(ItemMapper.toItemWithBookingDto(itemOut), response);
        verify(commentRepository).findAllByItemId(itemId);
        verify(itemRepository).findById(itemId);
    }

    @Test
    void getItemDtoByIdForAll_whenUserIdNotEqualItemId_thenResponseWithItemWithBookingDto() {
        long userId = 2L;
        long itemId = 1L;
        User user = new User(2L, "user", "user@yandex.ru");
        Item item = new Item(1L, "Садовая тачка", "Возит сама", true, user, null);
        ItemWithBooking itemOut = new ItemWithBooking(1L, "Садовая тачка",
                "Возит сама", true, null, null, new ArrayList<>());

        when(commentRepository.findAllByItemId(itemId)).thenReturn(Collections.emptyList());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingForItem(itemId)).thenReturn(Collections.emptyList());
        when(bookingRepository.findNextBookingForItem(itemId)).thenReturn(Collections.emptyList());

        ItemDtoOut response = itemServiceImpl.getItemDtoByIdForAll(itemId, userId);

        assertEquals(ItemMapper.toItemWithBookingDto(itemOut), response);
        verify(commentRepository).findAllByItemId(itemId);
        verify(bookingRepository).findLastBookingForItem(itemId);
        verify(bookingRepository).findNextBookingForItem(itemId);
    }

    @Test
    void getAllUserItemsDto_whenInvoked_thenResponseContainsItemDtoOutInBody() {
        long userId = 1L;
        int from = 0;
        int size = 10;
        Item item1 =
                new Item(1L, "Садовая тачка","Возит сама", true, new User(), null);
        Item item2 =
                new Item(2L, "Самокат","Возит сам", true, new User(), null);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Item> itemList = List.of(item1, item2);
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(userId, page)).thenReturn(itemList);

        List<ItemDtoOut> response = itemServiceImpl.getAllUserItemsDto(userId, from, size);

        assertEquals(itemList.size(), response.size());
        verify(itemRepository).findAllByOwnerIdOrderByIdAsc(userId, page);
    }

    @Test
    void searchItemDtoByText_whenItemWithTextFound_thenResponseContainsItemDtoOutInBody() {
        String text = "example";
        int from = 0;
        int size = 10;
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Item> itemList = List.of(new Item(), new Item());
        when(itemRepository.findByNameContainingOrDescriptionContaining(text.toUpperCase(), page))
                .thenReturn(itemList);

        List<ItemDtoOut> response = itemServiceImpl.searchItemDtoByText(text, from, size);

        assertEquals(itemList.size(), response.size());
        verify(itemRepository).findByNameContainingOrDescriptionContaining(text.toUpperCase(), page);
    }

    @Test
    void createItem_whenInvoked_thenItemCreated() {
        long userId = 1L;
        ItemDtoIn itemDtoIn = new ItemDtoIn("Садовая тачка", "Возит сама", true);
        Item item = new Item(null, "Садовая тачка", "Возит сама", true);
        Item itemOut = new Item(1L, "Садовая тачка",
                "Возит сама", true, new User(), null);

        when(itemRepository.save(item)).thenReturn(itemOut);

        ItemDtoOut response = itemServiceImpl.createItem(userId, itemDtoIn);
        assertEquals(ItemMapper.toItemDtoOut(itemOut), response);
        verify(itemRepository).save(item);
    }

    @Test
    void updateItem_whenInvoked_thenItemUpdated() {
        long itemId = 1L;
        long userId = 1L;
        User user = new User(1L, "user", "user@yandex.ru");
        ItemDtoIn updateItemDtoIn = new ItemDtoIn(2L, "Супер тачка",
                "Теперь это робот", false);
        Item item = new Item(1L, "Садовая тачка",
                "Возит сама", true, user, null);
        Item updatedItem = new Item(1L, "Супер тачка",
                "Теперь это робот", false, user, null);

        when(userService.getUserDtoById(userId)).thenReturn(new UserDto());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(updatedItem);

        ItemDtoOut itemDtoOut = itemServiceImpl.updateItem(userId, itemId, updateItemDtoIn);

        assertEquals(ItemMapper.toItemDtoOut(item), itemDtoOut);
        verify(userService).getUserDtoById(userId);
        verify(itemRepository).findById(itemId);
        verify(itemRepository).save(item);
    }

    @Test
    void deleteItem_whenInvoked_thenDeleteItem() {
        long itemId = 1L;
        long userId = 1L;
        User user = new User(1L, "user", "user@yandex.ru");
        Item item = new Item(1L, "Садовая тачка", "Возит сама", true, user, null);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemServiceImpl.deleteItem(userId, itemId);

        verify(itemRepository, times(1)).deleteById(itemId);
    }

    @Test
    void checkUserItem_whenItemFound_thenResponseWithItem() {
        long itemId = 1L;
        long userId = 1L;
        User user = new User(1L, "user", "user@yandex.ru");
        Item item = new Item(1L, "Садовая тачка", "Возит сама", true, user, null);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Item response = itemServiceImpl.checkUserItem(userId, itemId);

        assertEquals(item, response);
        verify(itemRepository).findById(itemId);
    }

    @Test
    void checkUserItem_whenUserHaveNoSuchItem_thenUserHaveNoSuchItemExceptionThrown() {
        long itemId = 1L;
        long userId = 2L;
        User user = new User(1L, "user", "user@yandex.ru");
        Item item = new Item(1L, "Садовая тачка", "Возит сама", true, user, null);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(UserHaveNoSuchItemException.class, () -> itemServiceImpl.checkUserItem(userId, itemId));
        verify(itemRepository).findById(itemId);
    }

    @Test
    void getItemDtoById_whenUserFound_thenResponseWithItemDtoOut() {
        long itemId = 1L;
        ItemDtoOut itemDtoOut =
                new ItemDtoOut(1L, "Садовая тачка", "Возит сама", true, null);
        Item item = new Item(1L, "Садовая тачка", "Возит сама", true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDtoOut response = itemServiceImpl.getItemDtoById(itemId);

        assertEquals(itemDtoOut, response);
        verify(itemRepository).findById(itemId);
    }

    @Test
    void getItemById_whenUserFound_thenResponseWithItem() {
        long itemId = 1L;
        Item item = new Item(1L, "Садовая тачка", "Возит сама", true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Item response = itemServiceImpl.getItemById(itemId);
        assertEquals(item, response);
        verify(itemRepository).findById(itemId);
    }

    @Test
    void getItemById_whenItemNotFound_thenItemNotFoundExceptionThrown() {
        long itemId = 0L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemServiceImpl.getItemById(itemId));
        verify(itemRepository).findById(itemId);
    }
}