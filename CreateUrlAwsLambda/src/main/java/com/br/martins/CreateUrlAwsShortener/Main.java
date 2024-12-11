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

        System.out.println("INPUT: ");
        System.out.println(input);
        
        Map<String, String> errorMessage = new HashMap<String, String>();
        
        try {
            Main.verifyIsBodyRequestSentMiddleware(input);
            
        } catch (Exception exception) {
            errorMessage.put("message", "Body request must be sent.");
            return errorMessage;       
        }
        
        final String body = input.get("body").toString();
        
        Map <String, String> bodyMap;

        try{

            bodyMap = objectMapper.readValue(body, Map.class);

        }catch(Exception exception) {

            throw new RuntimeException("Error parsing JSON body: " + exception.getMessage(), exception);
        }

        System.out.println("BODY_MAP: ");
        System.out.println(bodyMap);

        if(bodyMap.get("expirationTime").equals(null) && bodyMap.get("originalUrl").equals(null)) {
            errorMessage.put("message", "Expiration Time and Original Url must be sent.");
            return errorMessage;

        } else if(bodyMap.get("expirationTime").equals(null)) {
            errorMessage.put("message", "Expiration Time must be sent");
            return errorMessage;
            
        } else if(bodyMap.get("originalUrl").equals(null)) {
            errorMessage.put("message", "Original Url must be sent");
            return errorMessage;
        }

        final String originalUrl = bodyMap.get("originalUrl");
        final String expirationTime = bodyMap.get("expirationTime");

        System.out.println("OriginalUrl: " + originalUrl);
        System.out.println("ExpirationTime: " + expirationTime);

        final String shortUrlCode = UUID.randomUUID().toString().substring(0, 8);
        final long expirationTimeInSeconds = Long.parseLong(expirationTime);


        // final UrlDto urlDto = new UrlDto(originalUrl, expirationTimeInSeconds);

        // try {
            
        //     final String urlDtoToJson = objectMapper.writeValueAsString(urlDto);

        //     final PutObjectRequest request = PutObjectRequest.builder()
        //         .bucket("nome-do-bucket")
        //         .key(shortUrlCode + ".json")
        //         .build();

        //     s3Client.putObject(request, RequestBody.fromString(urlDtoToJson));

        // } catch (Exception exception) {
            
        //     throw new RuntimeException("Error saving URL data to S3: " + exception.getMessage(), exception);
        // }

        String expirationTimeString = String.valueOf(expirationTimeInSeconds);

        final Map<String, String> response = new HashMap<String,String>();
        response.put("code", shortUrlCode);
        response.put("OriginalUrl", originalUrl);
        response.put("ExpirationTime", expirationTimeString);


        return response;
    }

    private static void verifyIsBodyRequestSentMiddleware(Map<String, Object> input) throws RuntimeException {

        if(input.equals(null)) {
            throw new RuntimeException("Body request must be sent.");
        } 
    }
}