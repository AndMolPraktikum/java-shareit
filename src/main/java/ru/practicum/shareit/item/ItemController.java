package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    @Autowired
    private final ItemService itemService;

    @GetMapping("/{itemId}") // Информацию о вещи может просмотреть любой пользователь.
    public ItemDto findItemById(@PathVariable long itemId) {
        log.info("Входящий запрос GET /items/{}.", itemId);
        ItemDto itemDto = ItemMapper.toDto(itemService.getItemById(itemId));
        log.info("Исходящий ответ: {}", itemDto);
        return itemDto;
    }

    @GetMapping // Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой.
    public List<ItemDto> findAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос GET /items. Для пользователя с ID: {}", userId);
        List<Item> allUserItems = itemService.getAllUserItems(userId);
        List<ItemDto> allUserItemsDto = ItemMapper.toDtoList(allUserItems);
        log.info("Исходящий ответ: {}", allUserItemsDto);
        return allUserItemsDto;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemByText(@RequestParam String text) {
        log.info("Входящий запрос POST /items/search. Параметр запроса: {}", text);
        List<Item> searchQueryResponse = itemService.searchItemByText(text);
        List<ItemDto> searchQueryResponseDto = ItemMapper.toDtoList(searchQueryResponse);
        log.info("Исходящий ответ: {}", searchQueryResponseDto);
        return searchQueryResponseDto;
    }

    /*
     * На вход поступает объект ItemDto
     */
    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос POST /items. ID пользователя: {}.  ItemDto: {}", userId, itemDto);
        Item item = ItemMapper.toEntity(itemDto);
        ItemDto responseDto = ItemMapper.toDto(itemService.createItem(userId, item));
        log.info("Исходящий ответ: {}", responseDto);
        return responseDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Входящий запрос PATCH /items/{}. ID пользователя: {}.  ItemDto: {}", itemId, userId, itemDto);
        Item item = itemService.updateItem(userId, itemId, ItemMapper.toEntity(itemDto));
        return ItemMapper.toDto(item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Входящий запрос DELETE /items/{}. ID пользователя: {}.", itemId, userId);
        itemService.deleteItem(userId, itemId);
    }
}
