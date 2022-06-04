package me.ikevoodoo.networkils.http.client;

import me.ikevoodoo.networkils.http.HttpMode;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("unused")
public class HttpClient {

    private static final int HTTPS_LENGTH = 8;
    private static final int HTTP_LENGTH = 7;

    private String url;
    private String path;
    private boolean https;
    private boolean followRedirects = true;
    private final HttpMode mode;
    private final HashMap<String, String> parameters;
    private final HashMap<String, String> headers;

    private HttpClient(String url, HttpMode mode) {
        this.url = url;
        this.getPath();
        this.getHttps();
        this.mode = mode;
        this.parameters = new HashMap<>();
        this.headers = new HashMap<>();
        acceptAll();
    }

    public static HttpClient post(String url) {
        return new HttpClient(url, HttpMode.POST);
    }

    public static HttpClient get(String url) {
        return new HttpClient(url, HttpMode.GET);
    }

    public HttpClient path(String path) {
        int index = url.indexOf("/", getSchemeLength());
        String newUrl = this.url.substring(0, index > 0 ? index : url.length());
        this.url = newUrl + path;
        return this;
    }

    public HttpClient param(String key, String value) throws UnsupportedEncodingException {
        parameters.put(URLEncoder.encode(key, "UTF-8"), URLEncoder.encode(value, "UTF-8"));
        return this;
    }

    public HttpClient silentParam(String key, String value) {
        try {
            parameters.put(URLEncoder.encode(key, "UTF-8"), URLEncoder.encode(value, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this;
    }

    public HttpClient header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public HttpClient accept(String accept) {
        headers.put("Accept", accept);
        return this;
    }

    public HttpClient acceptAll() {
        return accept("*/*");
    }

    public HttpClient acceptJson() {
        return accept("application/json");
    }

    public HttpClient printRequest(PrintStream out) {
        out.println(asHttpRequest());
        return this;
    }

    public HttpClient https() {
        this.https = true;
        return this;
    }

    public HttpClient http() {
        this.https = false;
        return this;
    }

    public HttpClient followRedirects() {
        this.followRedirects = true;
        return this;
    }

    public HttpClient ignoreRedirects() {
        this.followRedirects = false;
        return this;
    }

    public CompletableFuture<ResponseData> request() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(getFullUrl());
                if(this.https) {
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod(mode.toString());
                    if(followRedirects) {
                        connection.setInstanceFollowRedirects(true);
                    }
                    for (String key : headers.keySet()) {
                        connection.setRequestProperty(key, headers.get(key));
                    }
                    connection.connect();
                    return new ResponseData(connection);
                }

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                if(followRedirects) {
                    connection.setInstanceFollowRedirects(true);
                }
                connection.setRequestMethod(mode.toString());
                for (String key : headers.keySet()) {
                    connection.setRequestProperty(key, headers.get(key));
                }
                connection.connect();
                return new ResponseData(connection);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseData(400, new byte[0]);
            }
        });
    }

    private String asHttpRequest() {
        StringBuilder request = new StringBuilder();
        request.append(mode.name()).append(" ").append(getFullUrl()).append(" HTTP/1.1\r\nHost: ").append(url).append("\r\n");
        headers.forEach((key, value) -> request.append(key).append(": ").append(value).append("\r\n"));
        request.append("\r\n");
        return request.toString();
    }

    private String getFullUrl() {
        StringBuilder url = new StringBuilder(this.https ? "https://" : "http://");
        url.append(this.url).append(this.path);
        if (parameters.size() > 0) {
            url.append("?");
            parameters.forEach((key, value) -> url.append(key).append("=").append(value).append("&"));
            url.deleteCharAt(url.length() - 1);
        }
        return url.toString();
    }


    private void applyParams() {
        StringBuilder sb = new StringBuilder();
        for (String key : parameters.keySet()) {
            sb.append(key).append("=").append(parameters.get(key)).append("&");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        url += "?" + sb;
    }

    private void getPath() {
        int index = url.indexOf("/", getSchemeLength() + 1);
        this.path = index > 0 ? url.substring(index) : "";
        this.url = this.url.substring(0, index > 0 ? index : url.length());
    }

    private void getHttps() {
        if (url.startsWith("https://")) {
            this.https = true;
            this.url = url.substring(HTTPS_LENGTH);
            return;
        }

        if(url.startsWith("http://")) {
            this.https = false;
            this.url = url.substring(HTTP_LENGTH);
            return;
        }

        this.https = true;
    }

    private int getSchemeLength() {
        return https ? HTTPS_LENGTH : HTTP_LENGTH;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        HttpClient.get("https://google.com/search?q=youtube")
                .header("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.61 Safari/537.36")
                .request().thenAccept(data -> data.writeToSilent(new File("./website.html"))).get();
    }

}
