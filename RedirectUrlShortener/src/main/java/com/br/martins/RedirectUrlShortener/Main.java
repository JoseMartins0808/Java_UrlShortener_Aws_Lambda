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
        // em uma URL http://site.com/UUID, extrairá o seguinte: "/UUID"
        final String urlCode = pathParameter.replace("/", "");

        System.out.println("URL CODE: ");
        System.out.println(urlCode);
        
        if(urlCode == null || urlCode.isBlank()) {

            Map<String, Object> errorMessage = new HashMap<String, Object>();
            errorMessage.put("statusCode", "400");
            errorMessage.put("body", "Invalid input: shortUrlCode is required.");
            return errorMessage;
        }

        final String s3BucketName = System.getenv("BUCKET_S3_URL_SHORTENER");

        final GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(s3BucketName)
            .key(".json")
            .build();

        InputStream s3ObjectStream;

        try {
            s3ObjectStream = s3Client.getObject(getObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching ULR data from s3 bucket: " + e.getMessage(), e.getCause());
        }

        UrlDto urlDto;

        try {
            urlDto = objectMapper.readValue(s3ObjectStream, UrlDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing data: " + e.getMessage(), e.getCause());
        }

        long currentTimeinSeconds = System.currentTimeMillis();

        if(urlDto.getExpirationTime() < currentTimeinSeconds) {
            
            Map<String, Object> errorMessage = new HashMap<String, Object>();
            errorMessage.put("statusCode", 410);
            errorMessage.put("body", "This URL has expired");
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