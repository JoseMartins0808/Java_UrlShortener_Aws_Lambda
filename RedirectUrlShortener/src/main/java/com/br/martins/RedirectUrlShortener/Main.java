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
        System.out.println("PATH: " + pathParameter);
        final String urlCode = pathParameter.replace("/", "");
        System.out.println("URL CODE: " + urlCode);
        Map<String, Object> response = new HashMap<String, Object>();


        if(urlCode == null || urlCode.isBlank() || urlCode == "") {
            Map<String, String> body = new HashMap<String, String>();

            try {
                body.put("message", "Short URL code is required");
                final String bodyResponse = objectMapper.writeValueAsString(body);
                response.put("body", bodyResponse);
                response.put("statusCode", 400);
                return response;

            } catch (Exception e) {
                throw new RuntimeException("Error serializing value as String: " + e.getMessage());
            }
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
            Map<String, String> body = new HashMap<String, String>();

            try {
                body.put("message", "Short Url not found");
                final String bodyResponse = objectMapper.writeValueAsString(body);
                response.put("body", bodyResponse);
                response.put("statusCode", 400);
                return response;

            } catch (Exception exception) {
                throw new RuntimeException("Error serializing value as String: " + exception.getMessage());
            }
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

            Map<String, String> body = new HashMap<String, String>();

            try {
                body.put("message", "");
            } catch (Exception e) {
                // TODO: handle exception
            }
            
            // Map<String, Object> errorMessage = new HashMap<String, Object>();
            // errorMessage.put("statusCode", 410);
            // Map<String, String> errorBody = new HashMap<String, String>();
            // errorBody.put("message", "This URL has expired");
            // errorMessage.put("body", errorBody);
            // return errorMessage;
        }

        response.put("statusCode", 302);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Location", urlDto.getOriginalUrl());
        response.put("headers", headers);

        return response;
    }
}