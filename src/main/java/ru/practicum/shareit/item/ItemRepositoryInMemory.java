package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ItemRepositoryInMemory implements ItemRepository {

    private final Map<Long, List<Item>> items = new HashMap<>();
    private long id = 0;

    @Override
    public Item get(long itemId) {
        List<Item> onlyOneItem = items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getId() == itemId)
                .collect(Collectors.toList());

        if (onlyOneItem.size() != 1) {
            log.error("Вещь с ID {} не существует", itemId);
            throw new ItemNotFoundException(String.format("Вещь с ID %d не существует", itemId));
        }

        return onlyOneItem.get(0);
    }

    @Override
    public List<Item> getAllUserItems(Long userId) {
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public List<Item> getAllItems() {
        return items.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public Item createItem(Item item) {
        id++;
        item.setId(id);
        items.compute(item.getOwnerId(), (userId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });

        return get(id);
    }

    @Override
    public Item updateItem(long userId, Item item) {
        List<Item> userItems = items.get(userId);
        userItems.removeIf(i -> i.getId().equals(item.getId()));
        userItems.add(item);

        return get(item.getId());
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        List<Item> userItems = items.get(userId);
        userItems.removeIf(item -> item.getId() == (itemId));
    }
}
