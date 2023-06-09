package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comments.CommentService;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    @Autowired
    private final ItemService itemService;

    @Autowired
    private final CommentService commentService;

    @GetMapping("/{itemId}")
    public ItemWithBookingDto findItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable long itemId) {
        log.info("Входящий запрос GET /items/{}. Пользователя с ID: {}", itemId, userId);
        final ItemWithBookingDto itemWithBookingDto = itemService.getItemDtoByIdForAll(itemId, userId);
        log.info("Исходящий ответ: {}", itemWithBookingDto);
        return itemWithBookingDto;
    }

    @GetMapping
    public List<ItemWithBookingDto> findAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос GET /items. Для пользователя с ID: {}", userId);
        final List<ItemWithBookingDto> allItemsWithBookingDto = itemService.getAllUserItemsDto(userId);
        log.info("Исходящий ответ: {}", allItemsWithBookingDto);
        return allItemsWithBookingDto;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemByText(@RequestParam String text) {
        log.info("Входящий запрос POST /items/search. Параметр запроса: {}", text);
        final List<ItemDto> searchQueryResponseDto = itemService.searchItemDtoByText(text);
        log.info("Исходящий ответ: {}", searchQueryResponseDto);
        return searchQueryResponseDto;
    }

    /*
     * На вход поступает объект ItemDto
     */
    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос POST /items. ID пользователя: {}.  ItemDto: {}", userId, itemDto);
        final ItemDto responseDto = itemService.createItem(userId, itemDto);
        log.info("Исходящий ответ: {}", responseDto);
        return responseDto;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto,
                                    @PathVariable long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос POST /items/{}/comment. ID пользователя: {}. " +
                "CommentDto: {}", itemId, userId, commentDto);
        final CommentDto createdCommentDto = commentService.create(itemId, userId, commentDto);
        log.info("Исходящий ответ: {}", createdCommentDto);
        return createdCommentDto;
    }


    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Входящий запрос PATCH /items/{}. ID пользователя: {}.  ItemDto: {}", itemId, userId, itemDto);
        final ItemDto updatedItemDto = itemService.updateItem(userId, itemId, itemDto);
        log.info("Исходящий ответ: {}", updatedItemDto);
        return updatedItemDto;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Входящий запрос DELETE /items/{}. ID пользователя: {}.", itemId, userId);
        itemService.deleteItem(userId, itemId);
    }
}
