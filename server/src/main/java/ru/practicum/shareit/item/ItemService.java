package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDtoOut getItemDtoByIdForAll(long itemId, long userId);

    List<ItemDtoOut> getAllUserItemsDto(Long userId, int from, int size);

    List<ItemDtoOut> searchItemDtoByText(String text, int from, int size);

    ItemDtoOut createItem(Long userId, ItemDtoIn itemDtoIn);

    ItemDtoOut updateItem(long userId, long itemId, ItemDtoIn itemDtoIn);

    void deleteItem(long userId, long itemId);

    Item checkUserItem(long ownerId, long itemId);

    ItemDtoOut getItemDtoById(long itemId);

    Item getItemById(long itemId);
}
