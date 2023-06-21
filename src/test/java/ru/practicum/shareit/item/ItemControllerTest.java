package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.comments.CommentService;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private ItemController itemController;

    @Test
    void getItemDtoByIdForAll_whenInvoked_thenResponseWithItemDtoOut() {
        long userId = 1L;
        long itemId = 1L;
        ItemDtoOut expectedItem = new ItemDtoOut(1L, "Дрель", "Сверлит сама", true, 1L);
        when(itemService.getItemDtoByIdForAll(itemId, userId)).thenReturn(expectedItem);

        ItemDtoOut response = itemController.findItemById(userId, itemId);

        assertEquals(expectedItem, response);
        verify(itemService).getItemDtoByIdForAll(itemId, userId);
    }

    @Test
    void findAllUserItems_whenInvoked_thenResponseContainsItemDtoOutInBody() {
        long userId = 1L;
        int from = 0;
        int size = 5;
        List<ItemDtoOut> expectedItems = List.of(new ItemDtoOut());
        when(itemService.getAllUserItemsDto(userId, from, size)).thenReturn(expectedItems);

        List<ItemDtoOut> response = itemController.findAllUserItems(userId, from, size);

        assertEquals(expectedItems, response);
        verify(itemService).getAllUserItemsDto(userId, from, size);
    }

    @SneakyThrows
    @Test
    void searchItemByText_whenInvoked_thenResponseContainsItemDtoOutInBody() {
        String text = "text";
        int from = 0;
        int size = 5;
        List<ItemDtoOut> expectedItems = List.of(new ItemDtoOut());
        when(itemService.searchItemDtoByText(text, from, size)).thenReturn(expectedItems);

        List<ItemDtoOut> response = itemController.searchItemByText(text, from, size);

        assertEquals(expectedItems, response);
        verify(itemService).searchItemDtoByText(text, from, size);
    }

    @SneakyThrows
    @Test
    void createItem_whenInvoked_thenSaveItem() {
        long userId = 1L;
        ItemDtoIn itemDtoIn = new ItemDtoIn("Садовая тачка", "Возит сама", true);
        ItemDtoOut itemDtoOut = new ItemDtoOut(1L, "Садовая тачка", "Возит сама", true, null);

        when(itemService.createItem(userId, itemDtoIn)).thenReturn(itemDtoOut);

        ItemDtoOut response = itemController.createItem(itemDtoIn, userId);
        assertEquals(itemDtoOut, response);
        verify(itemService).createItem(userId, itemDtoIn);
    }

    @SneakyThrows
    @Test
    void createComment_whenInvoked_thenSaveComment() {
        long itemId = 1L;
        long userId = 1L;
        CommentDto commentDto = new CommentDto("Комментарий от юзер 1");
        CommentDto commentOut = new CommentDto(1L, "Комментарий от юзер 1", "User 1", LocalDateTime.now());

        when(commentService.create(itemId, userId, commentDto)).thenReturn(commentOut);

        CommentDto response = itemController.createComment(commentDto, userId, userId);
        assertEquals(commentOut, response);
        verify(commentService).create(itemId, userId, commentDto);
    }

    @SneakyThrows
    @Test
    void updateItem_whenItemFound_thenUpdate() {
        long userId = 1L;
        long itemId = 1L;
        ItemDtoOut old = new ItemDtoOut(1L, "Садовая тачка", "Возит сама", true, null);
        ItemDtoIn forUpdate = new ItemDtoIn("Садовая тачка с апгрейдом", "Всё сама делает", true);
        ItemDtoOut updatedItem = new ItemDtoOut(1L, "Садовая тачка с апгрейдом", "Всё сама делает", true, null);
        when(itemService.updateItem(userId, itemId, forUpdate)).thenReturn(updatedItem);
        ItemDtoOut response = itemController.updateItem(userId, itemId, forUpdate);

        assertEquals(updatedItem, response);
        verify(itemService).updateItem(userId, itemId, forUpdate);
    }

    @SneakyThrows
    @Test
    void deleteItem_whenItemFound_thenDelete() {
        long userId = 1L;
        long itemId = 1L;

        itemController.deleteItem(userId, itemId);

        verify(itemService).deleteItem(userId, itemId);
    }
}
