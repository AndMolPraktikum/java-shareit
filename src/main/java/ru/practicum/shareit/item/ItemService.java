package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBooking;

import java.util.List;

public interface ItemService {
    ItemWithBooking getItemByIdForAll(long itemId, long userId);

    List<ItemWithBooking> getAllUserItems(Long userId);

    List<Item> searchItemByText(String text);

    Item createItem(Long userId, Item item);

    Item updateItem(long userId, long itemId, Item item);

    void deleteItem(long userId, long itemId);

    Item checkUserItem(long ownerId, long itemId);

    Item getItemById(long itemId);
}
