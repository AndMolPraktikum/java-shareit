package ru.practicum.shareit.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestMapper {

    public static ItemRequest toEntity(ItemRequestDtoIn itemRequestDtoIn) {
        return new ItemRequest(
                itemRequestDtoIn.getDescription()
        );
    }

    public static ItemRequestDtoOut toItemRequestDtoOut(ItemRequest itemRequest) {
        return new ItemRequestDtoOut(
                itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.toUserDto(itemRequest.getRequester()),
                itemRequest.getCreated(),
                Collections.emptyList()
        );
    }

    public static List<ItemRequestDtoOut> toItemRequestDtoOutList(List<ItemRequest> itemRequestList) {
        return itemRequestList.stream()
                .map(ItemRequestMapper::toItemRequestDtoOut)
                .collect(Collectors.toList());
    }

}
