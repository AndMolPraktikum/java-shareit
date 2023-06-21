package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    List<ItemRequestDtoOut> getAllUserItemRequestDtoOut(Long userId);

    List<ItemRequestDtoOut> getAllItemRequestDtoOut(Long userId, int from, int size);

    ItemRequest getItemRequestById(long requestId);

    ItemRequestDtoOut getItemRequestDtoOutById(long requestId, Long userId);

    ItemRequestDtoOut create(Long userId, ItemRequestDtoIn itemRequestDtoIn);
}
