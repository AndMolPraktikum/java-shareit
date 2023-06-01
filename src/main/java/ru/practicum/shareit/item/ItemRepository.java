package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item get(long itemId);

    List<Item> getAllUserItems(Long userId);

    List<Item> getAllItems();

    Item createItem(Item item);

    Item updateItem(long userId, Item item);

    void deleteItem(long userId, long itemId);
}
