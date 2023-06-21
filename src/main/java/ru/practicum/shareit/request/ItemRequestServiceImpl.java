package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public List<ItemRequestDtoOut> getAllUserItemRequestDtoOut(Long userId) {
        userService.getUserById(userId);
        List<ItemRequest> allUserItemRequest = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        List<ItemRequestDtoOut> allUserItemRequestDto = ItemRequestMapper.toItemRequestDtoOutList(allUserItemRequest);
        allUserItemRequestDto.forEach(this::setResponsesList);
        return allUserItemRequestDto;
    }

    @Override
    public List<ItemRequestDtoOut> getAllItemRequestDtoOut(Long userId, int from, int size) {
        userService.getUserById(userId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(page, userId);
        List<ItemRequestDtoOut> irdoList = ItemRequestMapper.toItemRequestDtoOutList(itemRequestList);
        irdoList.forEach(this::setResponsesList);
        return irdoList;
    }

    @Override
    public ItemRequest getItemRequestById(long requestId) {
        Optional<ItemRequest> itemRequestOptional = itemRequestRepository.findById(requestId);
        if (itemRequestOptional.isEmpty()) {
            log.error("Запрос вещи с ID {} не существует", requestId);
            throw new ItemRequestNotFoundException(String.format("Запрос вещи с ID %d не существует", requestId));
        }
        return itemRequestOptional.get();
    }

    @Override
    public ItemRequestDtoOut getItemRequestDtoOutById(long requestId, Long userId) {
        userService.getUserById(userId);
        ItemRequestDtoOut itemRequestDtoOut = ItemRequestMapper.toItemRequestDtoOut(getItemRequestById(requestId));
        setResponsesList(itemRequestDtoOut);
        return itemRequestDtoOut;
    }

    @Transactional
    @Override
    public ItemRequestDtoOut create(Long userId, ItemRequestDtoIn itemRequestDtoIn) {
        ItemRequest itemRequest = ItemRequestMapper.toEntity(itemRequestDtoIn);
        itemRequest.setRequester(userService.getUserById(userId));
        return ItemRequestMapper.toItemRequestDtoOut(itemRequestRepository.save(itemRequest));
    }

    private void setResponsesList(ItemRequestDtoOut irdo) {
        List<Item> itemsByRequestId = itemRepository.findByRequestIdOrderByRequestCreatedDesc(irdo.getId());
        irdo.setItems(ItemMapper.toItemDtoOutList(itemsByRequestId));
    }
}
