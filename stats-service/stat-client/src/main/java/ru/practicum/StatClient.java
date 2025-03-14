package ru.practicum;

import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.ViewStats;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatClient extends BaseClient {
    @Value("${stats-service.url}")
    private String serverUrl;


    public StatClient(@Value("${stats-service.url}") String serverUrl, RestTemplateBuilder builder) {
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
        return get("/stats", params);
    }

    public ResponseEntity<Object> addStatEvent(StatDto statDto) {
        UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(serverUrl).path("/hit");
        String url = uri.build().toUriString();
        return post(url, statDto);
    }

    @SuppressWarnings("unchecked")
    public List<ViewStats> readStatEvent(String start, String end, @Nullable List<String> uris, boolean unique) {
        UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(serverUrl).path("/stats");

        if (start != null) {
            uri.queryParam("start", encode(start));
        }
        if (end != null) {
            uri.queryParam("end", encode(end));
        }
        if (uris != null) {
            uri.queryParam("uris", uris);
        }
        String url = uri.build().toUriString();

        ResponseEntity<Object> response = get(url, null);

        return (List<ViewStats>) response.getBody();
    }


    private String encode(String value) {
        if (value != null) {
            return URLEncoder.encode(value, StandardCharsets.UTF_8);
        } else {
            return value;
        }
    }
}