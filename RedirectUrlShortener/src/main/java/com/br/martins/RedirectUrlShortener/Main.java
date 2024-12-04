package com.br.martins.RedirectUrlShortener;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import software.amazon.awssdk.services.s3.S3Client;

public class Main implements RequestHandler<Map<String, Object>, Map<String, String>> {

    private final S3Client s3Client = S3Client.builder().build();

    @Override
    public Map<String, String> handleRequest(Map<String, Object> input, Context context) {
        
        final String pathParameter = input.get("rawPath").toString(); // em uma URL http://site.com/UUID, extrairá o seguinte: "/UUID"
        final String urlCode = pathParameter.replace("/", "");

        if(urlCode == null || urlCode.isEmpty()) {
            throw new IllegalArgumentException("Invalid input: 'shortUrlCode is required.'");
        }
        return null;
    }
}