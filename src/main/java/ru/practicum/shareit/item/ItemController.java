package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comments.CommentService;
import ru.practicum.shareit.comments.dto.CommentRequest;
import ru.practicum.shareit.comments.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;


@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    @Autowired
    private final ItemService itemService;

    @Autowired
    private final CommentService commentService;

    @GetMapping("/{itemId}")
    public ItemDtoOut findItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable long itemId) {
        log.info("Входящий запрос GET /items/{}. Пользователя с ID: {}", itemId, userId);
        final ItemDtoOut itemDtoOut = itemService.getItemDtoByIdForAll(itemId, userId);
        log.info("Исходящий ответ: {}", itemDtoOut);
        return itemDtoOut;
    }

    @GetMapping
    public List<ItemDtoOut> findAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "0") @Min(0) int from,
                                             @RequestParam(defaultValue = "5") @Min(1) int size) {
        log.info("Входящий запрос GET /items?from={}&size={}. Для пользователя с ID: {}", from, size, userId);
        final List<ItemDtoOut> allItemsWithBookingDto = itemService.getAllUserItemsDto(userId, from, size);
        log.info("Исходящий ответ: {}", allItemsWithBookingDto);
        return allItemsWithBookingDto;
    }

    @GetMapping("/search")
    public List<ItemDtoOut> searchItemByText(@RequestParam String text,
                                             @RequestParam(defaultValue = "0") @Min(0) int from,
                                             @RequestParam(defaultValue = "5") @Min(1) int size) {
        log.info("Входящий запрос POST /items/search?from={}&size={}. Параметр запроса: {}", from, size, text);
        final List<ItemDtoOut> searchQueryResponseDto = itemService.searchItemDtoByText(text, from, size);
        log.info("Исходящий ответ: {}", searchQueryResponseDto);
        return searchQueryResponseDto;
    }

    /*
     * На вход поступает объект ItemDto
     */
    @PostMapping
    public ItemDtoOut createItem(@Valid @RequestBody ItemDtoIn itemDtoIn,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос POST /items. ID пользователя: {}.  ItemDto: {}", userId, itemDtoIn);
        final ItemDtoOut responseDto = itemService.createItem(userId, itemDtoIn);
        log.info("Исходящий ответ: {}", responseDto);
        return responseDto;
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse createComment(@Valid @RequestBody CommentRequest commentRequest,
                                         @PathVariable long itemId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос POST /items/{}/comment. ID пользователя: {}. " +
                "CommentDto: {}", itemId, userId, commentRequest);
        final CommentResponse createdCommentResponse = commentService.create(itemId, userId, commentRequest);
        log.info("Исходящий ответ: {}", createdCommentResponse);
        return createdCommentResponse;
    }


    @PatchMapping("/{itemId}")
    public ItemDtoOut updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId,
                                 @RequestBody ItemDtoIn itemDtoIn) {
        log.info("Входящий запрос PATCH /items/{}. ID пользователя: {}.  ItemDto: {}", itemId, userId, itemDtoIn);
        final ItemDtoOut updatedItemDto = itemService.updateItem(userId, itemId, itemDtoIn);
        log.info("Исходящий ответ: {}", updatedItemDto);
        return updatedItemDto;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Входящий запрос DELETE /items/{}. ID пользователя: {}.", itemId, userId);
        itemService.deleteItem(userId, itemId);
    }
}
