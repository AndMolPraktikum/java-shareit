package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comments.CommentService;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.config.MapperUtil;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBooking;

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

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/{itemId}")
    public ItemWithBookingDto findItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable long itemId) {
        log.info("Входящий запрос GET /items/{}. Пользователя с ID: {}", itemId, userId);
        final ItemWithBooking itemWithBooking = itemService.getItemByIdForAll(itemId, userId);
        final ItemWithBookingDto itemWithBookingDto = ItemMapper.toItemWithBookingDto(itemWithBooking);
        log.info("Исходящий ответ: {}", itemWithBookingDto);
        return itemWithBookingDto;
    }

    ///Создать отдельный DTO класс
    @GetMapping
    public List<ItemWithBookingDto> findAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос GET /items. Для пользователя с ID: {}", userId);
        final List<ItemWithBooking> allUserItems = itemService.getAllUserItems(userId);
        final List<ItemWithBookingDto> allItemsWithBookingDto = ItemMapper.toItemWithBookingDtoList(allUserItems);
        log.info("Исходящий ответ: {}", allItemsWithBookingDto);
        return allItemsWithBookingDto;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemByText(@RequestParam String text) {
        log.info("Входящий запрос POST /items/search. Параметр запроса: {}", text);
        final List<Item> searchQueryResponse = itemService.searchItemByText(text);
        final List<ItemDto> searchQueryResponseDto = MapperUtil.convertList(searchQueryResponse, this::convertToItemDto);
        log.info("Исходящий ответ: {}", searchQueryResponseDto);
        return searchQueryResponseDto;
    }

    /*
     * На вход поступает объект ItemDto
     */
    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос POST /items. ID пользователя: {}.  ItemDto: {}", userId, itemDto);
        final Item item = modelMapper.map(itemDto, Item.class);
        final ItemDto responseDto = modelMapper.map(itemService.createItem(userId, item), ItemDto.class);
        log.info("Исходящий ответ: {}", responseDto);
        return responseDto;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto,
                                    @PathVariable long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос POST /items/{}/comment. ID пользователя: {}. " +
                "CommentDto: {}", itemId, userId, commentDto);
        final Comment createdComment = commentService.create(itemId, userId, modelMapper.map(commentDto, Comment.class));
        final CommentDto createdCommentDto = modelMapper.map(createdComment, CommentDto.class);
        log.info("Исходящий ответ: {}", createdCommentDto);
        return createdCommentDto;
    }


    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Входящий запрос PATCH /items/{}. ID пользователя: {}.  ItemDto: {}", itemId, userId, itemDto);
        final Item updatedItem = itemService.updateItem(userId, itemId, modelMapper.map(itemDto, Item.class));
        final ItemDto updatedItemDto = modelMapper.map(updatedItem, ItemDto.class);
        log.info("Исходящий ответ: {}", updatedItemDto);
        return updatedItemDto;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Входящий запрос DELETE /items/{}. ID пользователя: {}.", itemId, userId);
        itemService.deleteItem(userId, itemId);
    }

    private ItemDto convertToItemDto(Item item) {
        return modelMapper.map(item, ItemDto.class);
    }
}
