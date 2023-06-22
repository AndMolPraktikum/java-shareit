package ru.practicum.shareit.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestMapper {

    public static ItemRequest toEntity(ItemRequestRequest itemRequestRequest) {
        return new ItemRequest(
                itemRequestRequest.getDescription()
        );
    }

    public static ItemRequestResponse toItemRequestResponse(ItemRequest itemRequest) {
        return new ItemRequestResponse(
                itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.toUserResponse(itemRequest.getRequester()),
                itemRequest.getCreated(),
                Collections.emptyList()
        );
    }

    public static List<ItemRequestResponse> toItemRequestResponseList(List<ItemRequest> itemRequestList) {
        return itemRequestList.stream()
                .map(ItemRequestMapper::toItemRequestResponse)
                .collect(Collectors.toList());
    }
}
