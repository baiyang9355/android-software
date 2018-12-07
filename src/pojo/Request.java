package pojo;

import java.util.Map;

public class Request {
    private String originalUrl;
    private String url;
    private Map<String,String> urlParamsMap;

    public Request(String originalUrl, String url, Map<String, String> urlParamsMap) {
        this.originalUrl = originalUrl;
        this.url = url;
        this.urlParamsMap = urlParamsMap;
    }

    public Request() {
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getUrlParamsMap() {
        return urlParamsMap;
    }

    public void setUrlParamsMap(Map<String, String> urlParamsMap) {
        this.urlParamsMap = urlParamsMap;
    }
}
