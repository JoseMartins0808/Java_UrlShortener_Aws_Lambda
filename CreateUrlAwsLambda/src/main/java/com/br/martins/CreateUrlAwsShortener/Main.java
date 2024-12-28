package com.br.martins.CreateUrlAwsShortener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class Main implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final S3Client s3Client = S3Client.builder().build();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {

        Map<String, Object> response = new HashMap<String, Object>();

        try {
            Main.verifyIsBodyRequestSentMiddleware(input);
            
        } catch (Exception exception) {
            Map<String, String> body = new HashMap<String, String>();

            try {
                body.put("message", "Body request must be sent");
                final String bodyResponse = objectMapper.writeValueAsString(body);
                response.put("body", bodyResponse);
                response.put("statusCode", 400);
                return response;

            } catch (Exception e) {
                throw new RuntimeException("Error serializing value as String: " + e.getMessage());
            }
        }
        
        final String body = input.get("body").toString();
        
        Map <String, String> bodyMap;

        try{
            bodyMap = objectMapper.readValue(body, Map.class);

        }catch(Exception exception) {
            throw new RuntimeException("Error parsing JSON body: " + exception.getMessage(), exception);
        }

        final String originalUrl = bodyMap.get("originalUrl");
        final String expirationTime = bodyMap.get("expirationTime");

        if(originalUrl == null && expirationTime == null) {
            Map<String, String> errorBody = new HashMap<String, String>();

            try {
                errorBody.put("message", "Expiration Time and Original Url must be sent");
                final String bodyResponse = objectMapper.writeValueAsString(errorBody);
                response.put("body", bodyResponse);
                response.put("statusCode", 400);
                return response;

            } catch (Exception e) {
                throw new RuntimeException("Error serializing value as String: " + e.getMessage());
            }

        } else if(expirationTime == null || expirationTime.isBlank()) {
            Map<String, String> errorBody = new HashMap<String, String>();

            try {
                errorBody.put("message", "Expiration Time must be sent");
                final String bodyResponse = objectMapper.writeValueAsString(errorBody);
                response.put("body", bodyResponse);
                response.put("statusCode", 400);
                return response;

            } catch (Exception e) {
                throw new RuntimeException("Error serializing value as String: " + e.getMessage());
            }
            
        } else if(originalUrl == null || originalUrl.isBlank()) {
            Map<String, String> errorBody = new HashMap<String, String>();

            try {
                errorBody.put("message", "Original Url must be sent");
                final String bodyResponse = objectMapper.writeValueAsString(errorBody);
                response.put("body", bodyResponse);
                response.put("statusCode", 400);
                return response;

            } catch (Exception e) {
                throw new RuntimeException("Error serializing value as String: " + e.getMessage());
            }
        }

        Pattern pattern = Pattern.compile("[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)");
        Matcher match = pattern.matcher(originalUrl);

        if(!match.matches()) {
            
            Map<String, String> errorBody = new HashMap<String, String>();

            try {
                errorBody.put("message", "Oiginal Url must be a valid URL");
                final String bodyResponse = objectMapper.writeValueAsString(errorBody);
                response.put("body", bodyResponse);
                response.put("statusCode", 400);
                return response;

            } catch (Exception e) {
                throw new RuntimeException("Error serializing value as String: " + e.getMessage());
            }
        }

        final long expirationTimeInSeconds = Long.parseLong(expirationTime);
        final long currentTimeinSeconds = System.currentTimeMillis()/1000;
        System.out.println("CURRENT TIME: " + currentTimeinSeconds);

        if(expirationTimeInSeconds < currentTimeinSeconds) {
            Map<String, String> errorBody = new HashMap<String, String>();

            try {
                errorBody.put("message", "Expiration Time must be after Current Time: " + currentTimeinSeconds);
                final String bodyResponse = objectMapper.writeValueAsString(errorBody);
                response.put("body", bodyResponse);
                response.put("statusCode", 400);
                return response;

            } catch (Exception e) {
                throw new RuntimeException("Error serializing value as String: " + e.getMessage());
            }
        }

        final String shortUrlCode = UUID.randomUUID().toString().substring(0, 8);
        final String s3BucketName = System.getenv("BUCKET_S3_URL_SHORTENER");

        final UrlDto urlDto = new UrlDto(originalUrl, expirationTimeInSeconds);

        try {
            
            final String urlDtoToJson = objectMapper.writeValueAsString(urlDto);

            final PutObjectRequest request = PutObjectRequest.builder()
                .bucket(s3BucketName)
                .key(shortUrlCode + ".json")
                .build();

            s3Client.putObject(request, RequestBody.fromString(urlDtoToJson));

        } catch (Exception exception) {
            
            throw new RuntimeException("Error saving URL data to S3: " + exception.getMessage(), exception);
        }

        Map<String, String> successBody = new HashMap<String, String>();

        try {
            successBody.put("code", shortUrlCode);
            final String bodyResponse = objectMapper.writeValueAsString(successBody);
            response.put("body", bodyResponse);
            response.put("statusCode", 200);
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Error serializing value as String: " + e.getMessage());
        }
    }

    private static void verifyIsBodyRequestSentMiddleware(Map<String, Object> input) throws RuntimeException {

        if(input.get("body").equals(null)) {
            throw new RuntimeException("Body request must be sent.");
        } 
    }
}