package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    @Autowired
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestResponse> getAllUserItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос GET /requests. ID пользователя: {}.", userId);
        final List<ItemRequestResponse> allUserItemRequest = itemRequestService.getAllUserItemRequest(userId);
        log.info("Исходящий ответ: {}", allUserItemRequest);
        return allUserItemRequest;
    }

    @GetMapping("/all")
    public List<ItemRequestResponse> getAllItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(defaultValue = "0") @Min(0) int from,
                                                       @RequestParam(defaultValue = "5") @Min(1) int size) {
        log.info("Входящий запрос GET /requests/all?from={}&size={}. ID пользователя: {}.", from, size, userId);
        List<ItemRequestResponse> allItemRequest = itemRequestService.getAllItemRequest(userId, from, size);
        log.info("Исходящий ответ: {}", allItemRequest);
        return allItemRequest;
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponse getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable long requestId) {
        log.info("Входящий запрос GET /requests/{}. ID пользователя: {}.", requestId, userId);
        ItemRequestResponse itemRequestResponse = itemRequestService.getItemRequestResponseById(requestId, userId);
        log.info("Исходящий ответ: {}", itemRequestResponse);
        return itemRequestResponse;
    }

    @PostMapping
    public ItemRequestResponse createItemRequest(@Valid @RequestBody ItemRequestRequest itemRequestRequest,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос POST /requests. ID пользователя: {}. " +
                "itemRequestRequest: {}", userId, itemRequestRequest);
        final ItemRequestResponse createdItemRequestResponse = itemRequestService.createItemRequest(userId, itemRequestRequest);
        log.info("Исходящий ответ: {}", createdItemRequestResponse);
        return createdItemRequestResponse;
    }

}
