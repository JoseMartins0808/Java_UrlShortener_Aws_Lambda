package com.br.martins.CreateUrlAwsShortener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class Main implements RequestHandler<Map<String, Object>, Map<String, String>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final S3Client s3Client = S3Client.builder().build();

    @Override
    public Map<String, String> handleRequest(Map<String, Object> input, Context context) {

        final String body = input.get("body").toString();

        Map <String, String> bodyMap;

        try{

            bodyMap = objectMapper.readValue(body, Map.class);

            System.out.println(bodyMap);

        }catch(Exception exception) {

            throw new RuntimeException("Error parsing JSON body: " + exception.getMessage(), exception);
        }

        final String originalUrl = bodyMap.get("originalUrl");
        final String expirationTime = bodyMap.get("expirationTime");
        final String shortUrlCode = UUID.randomUUID().toString().substring(0, 8);
        final long expirationTimeInSeconds = Long.parseLong(expirationTime);

        System.out.println("Tempo de expiração em segundos: " + expirationTime);

        final UrlDto urlDto = new UrlDto(originalUrl, expirationTimeInSeconds);

        try {
            
            final String urlDtoToJson = objectMapper.writeValueAsString(urlDto);

            final PutObjectRequest request = PutObjectRequest.builder()
                .bucket("nome-do-bucket")
                .key(shortUrlCode + ".json")
                .build();

            s3Client.putObject(request, RequestBody.fromString(urlDtoToJson));

        } catch (Exception exception) {
            
            throw new RuntimeException("Error saving URL data to S3: " + exception.getMessage(), exception);
        }

        final Map<String, String> response = new HashMap<String,String>();
        response.put("code", shortUrlCode);


        return response;
    }
}