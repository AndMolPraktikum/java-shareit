package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    ItemRequestServiceImpl itemRequestServiceImpl;

    @Test
    void getAllUserItemRequestDtoOut_whenInvoked_thenResponseContainsListOfItemRequests() {
        long userId = 1L;
        ItemRequest itemRequest1 = new ItemRequest(1L, "Нужен аквариум", new User(), LocalDateTime.now());
        ItemRequest itemRequest2 = new ItemRequest(2L, "Нужны рыбки", new User(), LocalDateTime.now());
        List<ItemRequest> itemRequestList = List.of(itemRequest1, itemRequest2);
        when(userService.getUserById(userId)).thenReturn(null);
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId)).thenReturn(itemRequestList);

        List<ItemRequestResponse> requestDtoOutList = itemRequestServiceImpl.getAllUserItemRequest(userId);

        assertEquals(ItemRequestMapper.toItemRequestResponseList(itemRequestList), requestDtoOutList);
        verify(userService).getUserById(userId);
        verify(itemRequestRepository).findAllByRequesterIdOrderByCreatedDesc(userId);
    }

    @Test
    void getAllItemRequestDtoOut_whenInvokedWithFromEquals0_thenResponseContainsListOfItemRequests() {
        long userId = 1L;
        int from = 0;
        int size = 5;
        ItemRequest itemRequest1 = new ItemRequest(1L, "Нужен аквариум", new User(), LocalDateTime.now());
        ItemRequest itemRequest2 = new ItemRequest(2L, "Нужны рыбки", new User(), LocalDateTime.now());
        List<ItemRequest> itemRequestList = List.of(itemRequest1, itemRequest2);
        when(userService.getUserById(userId)).thenReturn(null);
        when(itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(any(PageRequest.class), eq(userId)))
                .thenReturn(itemRequestList);

        List<ItemRequestResponse> requestDtoOutList = itemRequestServiceImpl.getAllItemRequest(userId, from, size);

        assertEquals(ItemRequestMapper.toItemRequestResponseList(itemRequestList), requestDtoOutList);
        verify(userService).getUserById(userId);
        verify(itemRequestRepository).findAllByRequesterIdNotOrderByCreatedDesc(any(PageRequest.class), eq(userId));
    }

    @Test
    void getAllItemRequestDtoOut_whenInvokedWithFromEquals1_thenResponseContainsListOfItemRequests() {
        long userId = 1L;
        int from = 1;
        int size = 5;
        ItemRequest itemRequest1 = new ItemRequest(1L, "Нужен аквариум", new User(), LocalDateTime.now());
        ItemRequest itemRequest2 = new ItemRequest(2L, "Нужны рыбки", new User(), LocalDateTime.now());
        List<ItemRequest> itemRequestList = List.of(itemRequest1, itemRequest2);
        when(userService.getUserById(userId)).thenReturn(null);
        when(itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(any(PageRequest.class), eq(userId)))
                .thenReturn(itemRequestList);

        List<ItemRequestResponse> requestDtoOutList = itemRequestServiceImpl.getAllItemRequest(userId, from, size);

        assertEquals(ItemRequestMapper.toItemRequestResponseList(itemRequestList), requestDtoOutList);
        verify(userService).getUserById(userId);
        verify(itemRequestRepository).findAllByRequesterIdNotOrderByCreatedDesc(any(PageRequest.class), eq(userId));
    }

    @Test
    void getItemRequestById_whenItemRequestFound_thenResponseContainsItemRequest() {
        long requestId = 1L;
        ItemRequest itemRequest = new ItemRequest(1L, "Нужен аквариум", new User(), LocalDateTime.now());
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        ItemRequest requestOut = itemRequestServiceImpl.getItemRequestById(requestId);

        assertEquals(itemRequest, requestOut);
        verify(itemRequestRepository).findById(requestId);
    }

    @Test
    void getItemRequestById_whenItemRequestNotFound_thenItemRequestNotFoundExceptionThrown() {
        long requestId = 1L;
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(ItemRequestNotFoundException.class, () -> itemRequestServiceImpl.getItemRequestById(requestId));

        verify(itemRequestRepository).findById(requestId);
    }

    @Test
    void getItemRequestDtoOutById_whenItemRequestFound_thenResponseContainsItemRequestDtoOut() {
        long requestId = 1L;
        long userId = 1L;
        LocalDateTime localDateTime = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest(1L, "Нужен аквариум", new User(), localDateTime);
        ItemRequestResponse itemRequestResponse = new ItemRequestResponse(1L, "Нужен аквариум", new UserResponse(),
                localDateTime, Collections.emptyList());
        when(userService.getUserById(userId)).thenReturn(new User());
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestIdOrderByRequestCreatedDesc(anyLong())).thenReturn(Collections.emptyList());

        ItemRequestResponse requestDtoOut = itemRequestServiceImpl.getItemRequestResponseById(requestId, userId);

        assertEquals(itemRequestResponse, requestDtoOut);
        verify(userService).getUserById(userId);
        verify(itemRequestRepository).findById(requestId);
        verify(itemRepository).findByRequestIdOrderByRequestCreatedDesc(anyLong());
    }

    @Test
    void create_whenUserExist_thenItemRequestCreated() {
        long userId = 1L;
        ItemRequestRequest itemRequestRequest = new ItemRequestRequest("Нужен аквариум");
        ItemRequest itemRequest = new ItemRequest(1L, "Нужен аквариум", new User(), LocalDateTime.now());
        when(userService.getUserById(userId)).thenReturn(new User());
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestResponse requestDtoOut = itemRequestServiceImpl.createItemRequest(userId, itemRequestRequest);

        assertEquals(ItemRequestMapper.toItemRequestResponse(itemRequest), requestDtoOut);
        verify(userService).getUserById(userId);
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }
}