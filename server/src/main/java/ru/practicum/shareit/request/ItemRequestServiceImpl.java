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
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
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
    public List<ItemRequestResponse> getAllUserItemRequest(Long userId) {
        userService.getUserById(userId);
        List<ItemRequest> allUserItemRequest = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        List<ItemRequestResponse> allItemRequestResponses = ItemRequestMapper.toItemRequestResponseList(allUserItemRequest);
        allItemRequestResponses.forEach(this::setResponsesList);
        return allItemRequestResponses;
    }

    @Override
    public List<ItemRequestResponse> getAllItemRequest(Long userId, int from, int size) {
        userService.getUserById(userId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(page, userId);
        List<ItemRequestResponse> irdoList = ItemRequestMapper.toItemRequestResponseList(itemRequestList);
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
    public ItemRequestResponse getItemRequestResponseById(long requestId, Long userId) {
        userService.getUserById(userId);
        ItemRequestResponse itemRequestResponse = ItemRequestMapper.toItemRequestResponse(getItemRequestById(requestId));
        setResponsesList(itemRequestResponse);
        return itemRequestResponse;
    }

    @Transactional
    @Override
    public ItemRequestResponse createItemRequest(Long userId, ItemRequestRequest itemRequestRequest) {
        ItemRequest itemRequest = ItemRequestMapper.toEntity(itemRequestRequest);
        itemRequest.setRequester(userService.getUserById(userId));
        return ItemRequestMapper.toItemRequestResponse(itemRequestRepository.save(itemRequest));
    }

    private void setResponsesList(ItemRequestResponse irdo) {
        List<Item> itemsByRequestId = itemRepository.findByRequestIdOrderByRequestCreatedDesc(irdo.getId());
        irdo.setItems(ItemMapper.toItemDtoOutList(itemsByRequestId));
    }
}
