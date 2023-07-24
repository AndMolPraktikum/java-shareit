package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.GatewayCommentRequest;
import ru.practicum.shareit.item.dto.GatewayItemDtoIn;

import java.util.Map;

@Service
public class ItemClient extends BaseClient  {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getItemDtoByIdForAll(long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllUserItems(Long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> searchItemDtoByText(String text, int from, int size, Long userId) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> createItem(Long userId, GatewayItemDtoIn gatewayItemDtoIn) {
        return post("", userId, gatewayItemDtoIn);
    }

    public ResponseEntity<Object> createComment(long itemId, Long userId, GatewayCommentRequest gatewayCommentRequest) {
        return post("/" + itemId + "/comment", userId, gatewayCommentRequest);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, GatewayItemDtoIn gatewayItemDtoIn) {
        return patch("/" + itemId, userId, gatewayItemDtoIn);
    }

    public ResponseEntity<Object> deleteItem(long userId, long itemId) {
        return delete("/" + itemId, userId);
    }
}
