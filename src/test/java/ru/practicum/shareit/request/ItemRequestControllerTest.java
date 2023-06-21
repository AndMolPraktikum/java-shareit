package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @Test
    void getAllUserItemRequest_whenUserIdIsCorrect_thenResponseContainsListOfItemRequests() {
        long userId = 1L;
        List<ItemRequestDtoOut> itemRequestDtoOutList = List.of(new ItemRequestDtoOut(), new ItemRequestDtoOut());
        when(itemRequestService.getAllUserItemRequestDtoOut(userId)).thenReturn(itemRequestDtoOutList);

        List<ItemRequestDtoOut> requestList = itemRequestController.getAllUserItemRequest(userId);

        assertEquals(itemRequestDtoOutList, requestList);
        verify(itemRequestService).getAllUserItemRequestDtoOut(userId);
    }

    @Test
    void getAllItemRequest_whenUserExist_thenResponseContainsListOfItemRequests() {
        long userId = 1L;
        int from = 0;
        int size = 5;
        List<ItemRequestDtoOut> itemRequestDtoOutList = List.of(new ItemRequestDtoOut(), new ItemRequestDtoOut());
        when(itemRequestService.getAllItemRequestDtoOut(userId, from, size)).thenReturn(itemRequestDtoOutList);

        List<ItemRequestDtoOut> requestList = itemRequestController.getAllItemRequest(userId, from, size);

        assertEquals(itemRequestDtoOutList, requestList);
        verify(itemRequestService).getAllItemRequestDtoOut(userId, from, size);
    }

    @Test
    void getItemRequest_whenInvoked_thenResponseContainsItemRequest() {
        long userId = 1L;
        long requestId = 1L;
        ItemRequestDtoOut itemRequestDto = new ItemRequestDtoOut();
        when(itemRequestService.getItemRequestDtoOutById(requestId, userId)).thenReturn(itemRequestDto);

        ItemRequestDtoOut requestDtoOut = itemRequestController.getItemRequest(userId, requestId);

        assertEquals(itemRequestDto, requestDtoOut);
        verify(itemRequestService).getItemRequestDtoOutById(requestId, userId);
    }

    @Test
    void createItemRequest_whenInvoked_thenItemRequestCreated() {
        long userId = 1L;
        ItemRequestDtoIn itemRequestDtoIn = new ItemRequestDtoIn();
        ItemRequestDtoOut itemRequestDtoOut = new ItemRequestDtoOut();

        when(itemRequestService.create(userId, itemRequestDtoIn)).thenReturn(itemRequestDtoOut);

        ItemRequestDtoOut requestDtoOut = itemRequestController.createItemRequest(itemRequestDtoIn, userId);

        assertEquals(itemRequestDtoOut, requestDtoOut);
        verify(itemRequestService).create(userId, itemRequestDtoIn);
    }
}