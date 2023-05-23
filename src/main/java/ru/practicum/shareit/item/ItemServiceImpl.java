package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserHaveNoSuchItemException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final UserService userService;

    @Override
    public Item getItemById(long itemId) {
        return itemRepository.get(itemId);
    }

    @Override
    public List<Item> getAllUserItems(Long userId) {
        return itemRepository.getAllUserItems(userId);
    }

    @Override
    public List<Item> searchItemByText(String text) {
        if (text.length() == 0 || text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> allItems = itemRepository.getAllItems();
        return allItems.stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public Item createItem(Long userId, Item item) {
        userService.getUserById(userId);
        item.setOwnerId(userId);
        return itemRepository.createItem(item);
    }

    @Override
    public Item updateItem(long userId, long itemId, Item updateItem) {
        Item item = checkUserItem(userId, itemId);
        if (updateItem.getName() != null) {
            item.setName(updateItem.getName());
        }
        if (updateItem.getDescription() != null) {
            item.setDescription(updateItem.getDescription());
        }
        if (updateItem.getAvailable() != null) {
            item.setAvailable(updateItem.getAvailable());
        }
        return itemRepository.updateItem(userId, item);
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        checkUserItem(userId, itemId);
        itemRepository.deleteItem(userId, itemId);
    }

    private Item checkUserItem(long userId, long itemId) {
        Item item = getItemById(itemId);
        if (item.getOwnerId() != userId) {
            log.error("Пользователь с ID: {} не является владельцем вещи с ID: {}", userId, itemId);
            throw new UserHaveNoSuchItemException(
                    String.format("Пользователь с ID: %d не является владельцем вещи с ID: %d", userId, itemId));
        }
        return item;
    }
}
