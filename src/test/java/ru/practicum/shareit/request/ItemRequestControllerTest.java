package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

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
        List<ItemRequestResponse> itemRequestResponseList = List.of(new ItemRequestResponse(), new ItemRequestResponse());
        when(itemRequestService.getAllUserItemRequest(userId)).thenReturn(itemRequestResponseList);

        List<ItemRequestResponse> requestList = itemRequestController.getAllUserItemRequest(userId);

        assertEquals(itemRequestResponseList, requestList);
        verify(itemRequestService).getAllUserItemRequest(userId);
    }

    @Test
    void getAllItemRequest_whenUserExist_thenResponseContainsListOfItemRequests() {
        long userId = 1L;
        int from = 0;
        int size = 5;
        List<ItemRequestResponse> itemRequestResponseList = List.of(new ItemRequestResponse(), new ItemRequestResponse());
        when(itemRequestService.getAllItemRequest(userId, from, size)).thenReturn(itemRequestResponseList);

        List<ItemRequestResponse> requestList = itemRequestController.getAllItemRequest(userId, from, size);

        assertEquals(itemRequestResponseList, requestList);
        verify(itemRequestService).getAllItemRequest(userId, from, size);
    }

    @Test
    void getItemRequest_whenInvoked_thenResponseContainsItemRequest() {
        long userId = 1L;
        long requestId = 1L;
        ItemRequestResponse itemRequestDto = new ItemRequestResponse();
        when(itemRequestService.getItemRequestResponseById(requestId, userId)).thenReturn(itemRequestDto);

        ItemRequestResponse requestDtoOut = itemRequestController.getItemRequest(userId, requestId);

        assertEquals(itemRequestDto, requestDtoOut);
        verify(itemRequestService).getItemRequestResponseById(requestId, userId);
    }

    @Test
    void createItemRequest_whenInvoked_thenItemRequestCreated() {
        long userId = 1L;
        ItemRequestRequest itemRequestRequest = new ItemRequestRequest();
        ItemRequestResponse itemRequestResponse = new ItemRequestResponse();

        when(itemRequestService.createItemRequest(userId, itemRequestRequest)).thenReturn(itemRequestResponse);

        ItemRequestResponse requestDtoOut = itemRequestController.createItemRequest(itemRequestRequest, userId);

        assertEquals(itemRequestResponse, requestDtoOut);
        verify(itemRequestService).createItemRequest(userId, itemRequestRequest);
    }
}