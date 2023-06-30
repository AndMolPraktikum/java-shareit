package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.GatewayItemRequestRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestControllerGateway {

    @Autowired
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getAllUserItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос GET /requests. ID пользователя: {}.", userId);
        final ResponseEntity<Object> allUserItemRequest = itemRequestClient.getAllUserItemRequest(userId);
        log.info("Исходящий ответ: {}", allUserItemRequest);
        return allUserItemRequest;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "0") @Min(0) int from,
                                                    @RequestParam(defaultValue = "5") @Min(1) int size) {
        log.info("Входящий запрос GET /requests/all?from={}&size={}. ID пользователя: {}.", from, size, userId);
        final ResponseEntity<Object> allItemRequest = itemRequestClient.getAllItemRequest(userId, from, size);
        log.info("Исходящий ответ: {}", allItemRequest);
        return allItemRequest;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable long requestId) {
        log.info("Входящий запрос GET /requests/{}. ID пользователя: {}.", requestId, userId);
        final ResponseEntity<Object> itemRequestResponse = itemRequestClient
                .getItemRequestResponseById(requestId, userId);
        log.info("Исходящий ответ: {}", itemRequestResponse);
        return itemRequestResponse;
    }

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody GatewayItemRequestRequest gatewayItemRequestRequest,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос POST /requests. ID пользователя: {}. " +
                "itemRequestRequest: {}", userId, gatewayItemRequestRequest);
        final ResponseEntity<Object> createdItemRequestResponse = itemRequestClient
                .createItemRequest(userId, gatewayItemRequestRequest);
        log.info("Исходящий ответ: {}", createdItemRequestResponse);
        return createdItemRequestResponse;
    }

}
