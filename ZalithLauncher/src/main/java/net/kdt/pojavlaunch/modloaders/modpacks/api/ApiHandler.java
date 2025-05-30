package net.kdt.pojavlaunch.modloaders.modpacks.api;

import android.util.ArrayMap;

import com.google.gson.Gson;
import com.movtery.zalithlauncher.feature.log.Logging;
import com.movtery.zalithlauncher.utils.path.UrlManager;

import net.kdt.pojavlaunch.Tools;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class ApiHandler {
    public final String baseUrl;
    public final Map<String, String> additionalHeaders = new ArrayMap<>();

    public ApiHandler(String url) {
        baseUrl = url;
    }

    public ApiHandler(String url, String apiKey) {
        this(url);
        additionalHeaders.put("x-api-key", apiKey);
    }

    public <T> T get(String endpoint, Class<T> tClass) {
        return getFullUrl(additionalHeaders, baseUrl + "/" + endpoint, tClass);
    }

    public <T> T get(String endpoint, HashMap<String, Object> query, Class<T> tClass) {
        return getFullUrl(additionalHeaders, baseUrl + "/" + endpoint, query, tClass);
    }

    public <T> T post(String endpoint, T body, Class<T> tClass) {
        return postFullUrl(additionalHeaders, baseUrl + "/" + endpoint, body, tClass);
    }

    public <T> T post(String endpoint, HashMap<String, Object> query, T body, Class<T> tClass) {
        return postFullUrl(additionalHeaders, baseUrl + "/" + endpoint, query, body, tClass);
    }

    //Make a get request and return the response as a raw string;
    public static String getRaw(String url) {
        return getRaw(null, url);
    }

    public static String getRaw(Map<String, String> headers, String url) {
        Logging.d("ApiHandler", url);
        HttpURLConnection conn = null;
        try {
            conn = UrlManager.createHttpConnection(new URL(url));
            addHeaders(conn, headers);
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            String data = Tools.read(inputStream);

            inputStream.close();
            conn.disconnect();
            return data;
        } catch (FileNotFoundException e) {
            Logging.d("ApiHandler", "File Not Found! " + Tools.printToString(e));
            return null;
        } catch(InterruptedIOException e) {
            Logging.d("ApiHandler", "The connection has been interrupted, or has been canceled.\n" + Tools.printToString(e));
            return null;
        } catch (Exception e) {
            Logging.e("ApiHandler", Tools.printToString(e));
            if (conn != null) {
                conn.disconnect();
            }
            throw new RuntimeException(e);
        }
    }

    public static String postRaw(String url, String body) {
        return postRaw(null, url, body);
    }

    public static String postRaw(Map<String, String> headers, String url, String body) {
        try {
            HttpURLConnection conn = UrlManager.createHttpConnection(new URL(url));
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            addHeaders(conn, headers);
            conn.setDoOutput(true);

            OutputStream outputStream = conn.getOutputStream();
            byte[] input = body.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
            outputStream.close();

            InputStream inputStream = conn.getInputStream();
            String data = Tools.read(inputStream);
            inputStream.close();

            conn.disconnect();
            return data;
        } catch (IOException e) {
            Logging.e("ApiHandler", Tools.printToString(e));
        }
        return null;
    }

    private static void addHeaders(HttpURLConnection connection, Map<String, String> headers) {
        if(headers != null) {
            for(String key : headers.keySet())
                connection.addRequestProperty(key, headers.get(key));
        }
    }

    private static String parseQueries(HashMap<String, Object> query) {
        StringBuilder params = new StringBuilder("?");
        for (String param : query.keySet()) {
            String value = Objects.toString(query.get(param));
            params.append(urlEncodeUTF8(param))
                    .append("=")
                    .append(urlEncodeUTF8(value))
                    .append("&");
        }
        return params.substring(0, params.length() - 1);
    }

    public static <T> T getFullUrl(String url, Class<T> tClass) {
        return getFullUrl(null, url, tClass);
    }

    public static <T> T getFullUrl(String url, HashMap<String, Object> query, Class<T> tClass) {
        return getFullUrl(null, url, query, tClass);
    }

    public static <T> T postFullUrl(String url, T body, Class<T> tClass) {
        return postFullUrl(null, url, body, tClass);
    }

    public static <T> T postFullUrl(String url, HashMap<String, Object> query, T body, Class<T> tClass) {
        return postFullUrl(null, url, query, body, tClass);
    }

    public static <T> T getFullUrl(Map<String, String> headers, String url, Class<T> tClass) {
        return new Gson().fromJson(getRaw(headers, url), tClass);
    }

    public static <T> T getFullUrl(Map<String, String> headers, String url, HashMap<String, Object> query, Class<T> tClass) {
        return getFullUrl(headers, url + parseQueries(query), tClass);
    }

    public static <T> T postFullUrl(Map<String, String> headers, String url, T body, Class<T> tClass) {
        return new Gson().fromJson(postRaw(headers, url, body.toString()), tClass);
    }

    public static <T> T postFullUrl(Map<String, String> headers, String url, HashMap<String, Object> query, T body, Class<T> tClass) {
        return new Gson().fromJson(postRaw(headers, url + parseQueries(query), body.toString()), tClass);
    }

    private static String urlEncodeUTF8(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        }catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is required");
        }
    }
}
