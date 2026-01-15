package com.example.serviceA.application.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
public class GatewayController {

    private final RestTemplate restTemplate;
    private final Map<String, String> serviceMappings;

    public GatewayController(
            RestTemplate restTemplate,
            @Value("${serviceB.url}") String serviceBUrl) {

        this.restTemplate = restTemplate;
        this.serviceMappings = Map.of("/transactions", serviceBUrl);
    }

    @RequestMapping(value = "/{path}/**", method = RequestMethod.GET)
    public ResponseEntity<String> forwardGetRequest(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestHeader HttpHeaders headers,
            @RequestParam MultiValueMap<String, String> params,
            @PathVariable String path,
            HttpServletRequest request) {

        return forwardRequest(HttpMethod.GET, authHeader, null, headers, params, path, request);
    }

    @RequestMapping(value = "/{path}/**", method = RequestMethod.POST)
    public ResponseEntity<String> forwardPostRequest(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody(required = false) String body,
            @RequestHeader HttpHeaders headers,
            @RequestParam MultiValueMap<String, String> params,
            @PathVariable String path,
            HttpServletRequest request) {

        return forwardRequest(HttpMethod.POST, authHeader, body, headers, params, path, request);
    }

    private ResponseEntity<String> forwardRequest(
            HttpMethod method,
            String authHeader,
            String body,
            HttpHeaders incomingHeaders,
            MultiValueMap<String, String> params,
            String path,
            HttpServletRequest request) {

        String serviceUrl = serviceMappings.get("/" + path) + "/" + request.getRequestURI().substring(request.getRequestURI().indexOf(path));
        if (serviceUrl == null) {
            return ResponseEntity.badRequest().body("Invalid service path");
        }

        // Build target URL with query params
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(serviceUrl)
                .queryParams(params);

        // Copy headers
        HttpHeaders outgoingHeaders = new HttpHeaders();
        outgoingHeaders.addAll(incomingHeaders);
        outgoingHeaders.set(HttpHeaders.AUTHORIZATION, authHeader);

        HttpEntity<String> httpEntity = new HttpEntity<>(body, outgoingHeaders);

        return restTemplate.exchange(
                uriBuilder.toUriString(),
                method,
                httpEntity,
                String.class
        );
    }
}
