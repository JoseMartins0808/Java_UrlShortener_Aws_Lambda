package com.br.martins.RedirectUrlShortener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

public class Main implements RequestHandler<Map<String, Object>, Map<String, String>> {

    private final S3Client s3Client = S3Client.builder().build();

    public static Properties getProperties () throws IOException {
        Properties properties = new Properties();
        FileInputStream propertiesFile = new FileInputStream("/messages.properties");
        properties.load(propertiesFile);
        System.out.println(properties.getProperty("message.requiredShortUrl"));
        return properties;
    }

    @Override
    public Map<String, String> handleRequest(Map<String, Object> input, Context context) {
        
        final String pathParameter = input.get("rawPath").toString(); 
        // em uma URL http://site.com/UUID, extrairá o seguinte: "/UUID"
        final String urlCode = pathParameter.replace("/", "");

        System.out.println("URL CODE: ");
        System.out.println(urlCode);

        Properties properties;

        try {
            properties = getProperties();
        } catch (IOException e) {
            System.out.println("PROPERTIES NOT FOUND!");
            throw new RuntimeException("Error Reading Properties");
        }

        final String firstProperty = properties.getProperty("message.requiredShortUrl");
        final String secondProperty = properties.getProperty("message.requiredShortUrl");

        System.out.println("PRIMEIRA: ");
        System.out.println(firstProperty);
        System.out.println("SEGUNDA: ");
        System.out.println(secondProperty);

        Map<String, String> errorMessage = new HashMap<String, String>();

        if(urlCode == null || urlCode.isBlank()) {
            // throw new IllegalArgumentException("Invalid input: 'shortUrlCode is required.'");
            errorMessage.put("message", "Invalid input: 'shortUrlCode is required.'");
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


        return null;
    }
}