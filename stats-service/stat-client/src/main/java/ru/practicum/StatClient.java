package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHitDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatClient extends BaseClient {

    public StatClient(@Value("${stat-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .build());
    }

    public ResponseEntity<Object> sendHit(EndpointHitDto endpointHitDto) {
        return post("/hits", endpointHitDto);
    }

    public ResponseEntity<Object> getHits(String start, String end, List<String> uris, boolean unique) {
        Map<String, Object> params = new HashMap<>();
        params.put("start", start);
        params.put("end", end);
        if (uris != null && !uris.isEmpty()) {
            params.put("uris", String.join(",", uris));
        }
        params.put("unique", unique);
        return get("", params);
    }
}