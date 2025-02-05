package ru.practicum;

import jakarta.annotation.Nullable;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path, @Nullable Map<String, Object> params) {
        return makeAndSendRequest(HttpMethod.GET, path, params, null);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod httpMethod, String path,
                                                          @Nullable Map<String, Object> params, @Nullable T body) {
        HttpEntity<T> entity = new HttpEntity<>(body, defaultHeaders());
        ResponseEntity<Object> response;
        try {
            if (params != null) {
                response = rest.exchange(path, httpMethod, entity, Object.class, params);
            } else {
                response = rest.exchange(path, httpMethod, entity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return response;
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
