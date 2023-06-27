package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    List<ItemRequestResponse> getAllUserItemRequest(Long userId);

    List<ItemRequestResponse> getAllItemRequest(Long userId, int from, int size);

    ItemRequest getItemRequestById(long requestId);

    ItemRequestResponse getItemRequestResponseById(long requestId, Long userId);

    ItemRequestResponse createItemRequest(Long userId, ItemRequestRequest itemRequestRequest);
}
