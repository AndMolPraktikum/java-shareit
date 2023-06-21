package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.CommentRepository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserHaveNoSuchItemException;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBooking;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
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
    private ItemRequestService itemRequestService;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ItemServiceImpl itemServiceImpl;

    @Test
    void getItemDtoByIdForAll_whenInvoked_thenResponseWithItemDtoOut() {
        long userId = 2L;
        long itemId = 1L;
        ItemWithBooking itemOut = new ItemWithBooking(1L, "Садовая тачка",
                "Возит сама", true, null, null, new ArrayList<>());
        User user = new User(1L, "user", "user@yandex.ru");
        Item item = new Item(1L, "Садовая тачка", "Возит сама", true, user, null);
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
    void getItemDtoByIdForAll_whenUserIdNotEqualItemIdSetBooking_thenResponseWithItemWithBookingDto() {
        long userId = 2L;
        long itemId = 1L;
        Booking last = new Booking(
                1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                new Item(),
                new User(1L, "user", "user@yandex.ru"),
                BookingStatus.WAITING
        );
        Booking next = new Booking(
                2L,
                LocalDateTime.now().plusSeconds(3),
                LocalDateTime.now().plusSeconds(4),
                new Item(),
                new User(1L, "user", "user@yandex.ru"),
                BookingStatus.WAITING
        );
        User user = new User(2L, "user", "user@yandex.ru");
        Item item = new Item(1L, "Садовая тачка", "Возит сама", true, user, null);
        ItemWithBooking itemOut = new ItemWithBooking(1L, "Садовая тачка",
                "Возит сама", true, last, next, new ArrayList<>());
        when(commentRepository.findAllByItemId(itemId)).thenReturn(Collections.emptyList());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingForItem(itemId)).thenReturn(List.of(last));
        when(bookingRepository.findNextBookingForItem(itemId)).thenReturn(List.of(next));

        ItemDtoOut response = itemServiceImpl.getItemDtoByIdForAll(itemId, userId);

        assertEquals(ItemMapper.toItemWithBookingDto(itemOut), response);
        verify(commentRepository).findAllByItemId(itemId);
        verify(bookingRepository).findLastBookingForItem(itemId);
        verify(bookingRepository).findNextBookingForItem(itemId);
    }

    @Test
    void getItemDtoByIdForAll_whenWithoutNextBooking_thenResponseWithItemWithBookingDto() {
        long userId = 2L;
        long itemId = 1L;
        Booking last = new Booking(
                1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                new Item(),
                new User(1L, "user", "user@yandex.ru"),
                BookingStatus.WAITING
        );
        User user = new User(2L, "user", "user@yandex.ru");
        Item item = new Item(1L, "Садовая тачка", "Возит сама", true, user, null);
        ItemWithBooking itemOut = new ItemWithBooking(1L, "Садовая тачка",
                "Возит сама", true, last, null, new ArrayList<>());
        when(commentRepository.findAllByItemId(itemId)).thenReturn(Collections.emptyList());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingForItem(itemId)).thenReturn(List.of(last));
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
                new Item(1L, "Садовая тачка", "Возит сама", true, new User(), null);
        Item item2 =
                new Item(2L, "Самокат", "Возит сам", true, new User(), null);
        List<Item> itemList = List.of(item1, item2);
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(eq(userId), any(PageRequest.class))).thenReturn(itemList);

        List<ItemDtoOut> response = itemServiceImpl.getAllUserItemsDto(userId, from, size);

        assertEquals(itemList.size(), response.size());
        verify(itemRepository).findAllByOwnerIdOrderByIdAsc(eq(userId), any(PageRequest.class));
    }

    @Test
    void getAllUserItemsDto_whenFromEquals1_thenResponseContainsItemDtoOutInBody() {
        long userId = 1L;
        int from = 1;
        int size = 10;
        Item item1 =
                new Item(1L, "Садовая тачка", "Возит сама", true, new User(), null);
        Item item2 =
                new Item(2L, "Самокат", "Возит сам", true, new User(), null);
        List<Item> itemList = List.of(item1, item2);
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(eq(userId), any(PageRequest.class))).thenReturn(itemList);

        List<ItemDtoOut> response = itemServiceImpl.getAllUserItemsDto(userId, from, size);

        assertEquals(itemList.size(), response.size());
        verify(itemRepository).findAllByOwnerIdOrderByIdAsc(eq(userId), any(PageRequest.class));
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
    void searchItemDtoByText_whenTextLengthIs0_thenResponseContainsItemDtoOutInBody() {
        String text = "";
        int from = 1;
        int size = 10;
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<ItemDtoOut> response = itemServiceImpl.searchItemDtoByText(text, from, size);

        assertEquals(0, response.size());
        verify(itemRepository, never()).findByNameContainingOrDescriptionContaining(text.toUpperCase(), page);
    }

    @Test
    void searchItemDtoByText_whenTextIsBlank_thenResponseContainsItemDtoOutInBody() {
        String text = " ";
        int from = 1;
        int size = 10;
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<ItemDtoOut> response = itemServiceImpl.searchItemDtoByText(text, from, size);

        assertEquals(0, response.size());
        verify(itemRepository, never()).findByNameContainingOrDescriptionContaining(text.toUpperCase(), page);
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
    void createItem_whenRequestNotNull_thenItemCreated() {
        long userId = 1L;
        ItemRequest itemRequest = new ItemRequest();
        ItemDtoIn itemDtoIn =
                new ItemDtoIn(null, "Садовая тачка", "Возит сама", true, 1L);
        Item itemOut = new Item(1L, "Садовая тачка",
                "Возит сама", true, new User(), itemRequest);
        when(itemRequestService.getItemRequestById(anyLong())).thenReturn(itemRequest);
        when(itemRepository.save(any(Item.class))).thenReturn(itemOut);

        ItemDtoOut response = itemServiceImpl.createItem(userId, itemDtoIn);

        assertEquals(ItemMapper.toItemDtoOut(itemOut), response);
        verify(itemRepository).save(any(Item.class));
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
    void updateItem_whenUpdateItemDtoInIsEmpty_thenItemUpdated() {
        long itemId = 1L;
        long userId = 1L;
        User user = new User(1L, "user", "user@yandex.ru");
        ItemDtoIn updateItemDtoIn = new ItemDtoIn(2L, null, null, null);
        Item item = new Item(1L, "Садовая тачка",
                "Возит сама", true, user, null);
        Item updatedItem = new Item(1L, "Садовая тачка",
                "Возит сама", true, user, null);

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