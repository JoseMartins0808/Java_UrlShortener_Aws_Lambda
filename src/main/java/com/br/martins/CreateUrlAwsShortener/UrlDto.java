package com.br.martins.CreateUrlAwsShortener;

public class UrlDto {
    
    private String originalUrl;

    private long expirationTime;

    public UrlDto(){}

    public UrlDto(String originalUrl, long expirationTime) {
        this.originalUrl = originalUrl;
        this.expirationTime = expirationTime;
    }

    public String getOriginalUrl() {
        return this.originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public long getExpirationTime() {
        return this.expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }
}
