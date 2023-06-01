package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item getItemById(long itemId);

    List<Item> getAllUserItems(Long userId);

    List<Item> searchItemByText(String text);

    Item createItem(Long userId, Item item);

    Item updateItem(long userId, long itemId, Item item);

    void deleteItem(long userId, long itemId);
}
