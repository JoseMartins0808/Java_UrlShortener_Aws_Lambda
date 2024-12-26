package com.br.martins.RedirectUrlShortener;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

public class Main implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final S3Client s3Client = S3Client.builder().build();

    private final ObjectMapper objectMapper = new ObjectMapper();

     @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {

        final String pathParameter = input.get("rawPath").toString(); 
        final String urlCode = pathParameter.replace("/", "");

        if(urlCode == null || urlCode.isBlank()) {

            Map<String, Object> errorMessage = new HashMap<String, Object>();
            errorMessage.put("statusCode", "400");

            Map<String, String> body = new HashMap<String, String>();
            body.put("message", "Short URL code is required");

            errorMessage.put("body", body);
            return errorMessage;
        }

        final String s3BucketName = System.getenv("BUCKET_S3_URL_SHORTENER");

        final GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(s3BucketName)
            .key(urlCode + ".json")
            .build();

        InputStream s3ObjectStream;

        try {
            s3ObjectStream = s3Client.getObject(getObjectRequest);
        } catch (Exception e) {
            // fazer uma mensagem de erro aqui!
            // fazer uma mensagem de erro aqui!
            // fazer uma mensagem de erro aqui!
            // fazer uma mensagem de erro aqui!
            // fazer uma mensagem de erro aqui!
            // fazer uma mensagem de erro aqui!
            throw new RuntimeException("Error fetching ULR data from s3 bucket: " + e.getMessage(), e.getCause());
        }

        UrlDto urlDto;

        try {
            urlDto = objectMapper.readValue(s3ObjectStream, UrlDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing data: " + e.getMessage(), e.getCause());
        }

        long currentTimeinSeconds = System.currentTimeMillis()/1000;

        System.out.println("GET EXPIRATION TIME: ");
        System.out.println(urlDto.getExpirationTime());
        System.out.println("CURRENT TIME: ");
        System.out.println(currentTimeinSeconds);

        if(urlDto.getExpirationTime() < currentTimeinSeconds) {
            
            Map<String, Object> errorMessage = new HashMap<String, Object>();
            errorMessage.put("statusCode", 410);
            Map<String, String> errorBody = new HashMap<String, String>();
            errorBody.put("message", "This URL has expired");
            errorMessage.put("body", errorBody);
            return errorMessage;
        }

        Map<String, Object> response = new HashMap<String, Object>();
        response.put("statusCode", 302);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Location", urlDto.getOriginalUrl());

        response.put("headers", headers);

        return response;
    }
}