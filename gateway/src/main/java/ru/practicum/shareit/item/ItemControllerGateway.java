package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.GatewayCommentRequest;
import ru.practicum.shareit.item.dto.GatewayItemDtoIn;

import javax.validation.Valid;
import javax.validation.constraints.Min;


@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemControllerGateway {

    @Autowired
    private final ItemClient itemClient;


    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable long itemId) {
        log.info("Входящий запрос GET /items/{}. Пользователя с ID: {}", itemId, userId);
        final ResponseEntity<Object> itemDtoOut = itemClient.getItemDtoByIdForAll(itemId, userId);
        log.info("Исходящий ответ: {}", itemDtoOut);
        return itemDtoOut;
    }

    @GetMapping
    public ResponseEntity<Object> findAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "0") @Min(0) int from,
                                             @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Входящий запрос GET /items?from={}&size={}. Для пользователя с ID: {}", from, size, userId);
        final ResponseEntity<Object> allItemsResponseList = itemClient.getAllUserItems(userId, from, size);
        log.info("Исходящий ответ: {}", allItemsResponseList);
        return allItemsResponseList;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam String text,
                                             @RequestParam(defaultValue = "0") @Min(0) int from,
                                             @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Входящий запрос POST /items/search?text={}&from={}&size={}. " +
                "Для пользователя с ID: {}", text, from, size, userId);
        final ResponseEntity<Object> searchItemsResponseList = itemClient.searchItemDtoByText(text, from, size, userId);
        log.info("Исходящий ответ: {}", searchItemsResponseList);
        return searchItemsResponseList;
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody GatewayItemDtoIn gatewayItemDtoIn,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос POST /items. ID пользователя: {}.  ItemDto: {}", userId, gatewayItemDtoIn);
        final ResponseEntity<Object> createdItem = itemClient.createItem(userId, gatewayItemDtoIn);
        log.info("Исходящий ответ: {}", createdItem);
        return createdItem;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody GatewayCommentRequest commentRequest,
                                         @PathVariable long itemId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос POST /items/{}/comment. ID пользователя: {}. " +
                "CommentDto: {}", itemId, userId, commentRequest);
        final ResponseEntity<Object> createdCommentResponse = itemClient.createComment(itemId, userId, commentRequest);
        log.info("Исходящий ответ: {}", createdCommentResponse);
        return createdCommentResponse;
    }


    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId,
                                 @RequestBody GatewayItemDtoIn gatewayItemDtoIn) {
        log.info("Входящий запрос PATCH /items/{}. ID пользователя: {}.  ItemDto: {}", itemId, userId, gatewayItemDtoIn);
        final ResponseEntity<Object> updatedItemDto = itemClient.updateItem(userId, itemId, gatewayItemDtoIn);
        log.info("Исходящий ответ: {}", updatedItemDto);
        return updatedItemDto;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Входящий запрос DELETE /items/{}. ID пользователя: {}.", itemId, userId);
        itemClient.deleteItem(userId, itemId);
    }
}
