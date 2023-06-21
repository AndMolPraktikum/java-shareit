package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;

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
    public List<ItemRequestDtoOut> getAllUserItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос GET /requests. ID пользователя: {}.", userId);
        final List<ItemRequestDtoOut> allUserItemRequestDto = itemRequestService.getAllUserItemRequestDtoOut(userId);
        log.info("Исходящий ответ: {}", allUserItemRequestDto);
        return allUserItemRequestDto;
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOut> getAllItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "0") @Min(0) int from,
                                                     @RequestParam(defaultValue = "5") @Min(1) int size) {
        log.info("Входящий запрос GET /requests/all?from={}&size={}. ID пользователя: {}.", from, size, userId);
        List<ItemRequestDtoOut> allItemRequestDto = itemRequestService.getAllItemRequestDtoOut(userId, from, size);
        log.info("Исходящий ответ: {}", allItemRequestDto);
        return allItemRequestDto;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOut getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable long requestId) {
        log.info("Входящий запрос GET /requests/{}. ID пользователя: {}.", requestId, userId);
        ItemRequestDtoOut itemRequestDto = itemRequestService.getItemRequestDtoOutById(requestId, userId);
        log.info("Исходящий ответ: {}", itemRequestDto);
        return itemRequestDto;
    }

    @PostMapping
    public ItemRequestDtoOut createItemRequest(@Valid @RequestBody ItemRequestDtoIn itemRequestDtoIn,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос POST /requests. ID пользователя: {}.  ItemRequestDto: {}", userId, itemRequestDtoIn);
        final ItemRequestDtoOut createdItemRequestDtoOut = itemRequestService.create(userId, itemRequestDtoIn);
        log.info("Исходящий ответ: {}", createdItemRequestDtoOut);
        return createdItemRequestDtoOut;
    }

}
