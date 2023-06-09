package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemWithBookingDto getItemDtoByIdForAll(long itemId, long userId);

    List<ItemWithBookingDto> getAllUserItemsDto(Long userId);

    List<ItemDto> searchItemDtoByText(String text);

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    void deleteItem(long userId, long itemId);

    Item checkUserItem(long ownerId, long itemId);

    ItemDto getItemDtoById(long itemId);

    Item getItemById(long itemId);
}
