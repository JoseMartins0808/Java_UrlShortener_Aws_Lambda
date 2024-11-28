package com.br.martins.CreateUrlAwsShortener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main implements RequestHandler<Map<String, Object>, Map<String, String>> {

    final ObjectMapper objectMapper = new ObjectMapper();

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

        final Map<String, String> response = new HashMap<String,String>();
        response.put("code", shortUrlCode);


        return response;
    }
}