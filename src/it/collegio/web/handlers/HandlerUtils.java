package it.collegio.web.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility comuni a tutti gli handler HTTP.
 */
public final class HandlerUtils {

    private HandlerUtils() {
    }

    public static void sendHtml(HttpExchange ex, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        ex.sendResponseHeaders(200, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.getResponseBody().close();
    }

    public static void redirect(HttpExchange ex, String location) throws IOException {
        ex.getResponseHeaders().set("Location", location);
        ex.sendResponseHeaders(302, -1);
        ex.close();
    }

    public static void notFound(HttpExchange ex) throws IOException {
        byte[] body = "404 Not Found".getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(404, body.length);
        ex.getResponseBody().write(body);
        ex.getResponseBody().close();
    }

    public static Map<String, String> parseForm(HttpExchange ex) throws IOException {
        InputStream in = ex.getRequestBody();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        int n;
        while ((n = in.read(chunk)) > 0) {
            buf.write(chunk, 0, n);
        }
        String body = buf.toString(StandardCharsets.UTF_8.name());
        Map<String, String> result = new HashMap<>();
        if (body.isEmpty()) return result;
        for (String pair : body.split("&")) {
            int eq = pair.indexOf('=');
            if (eq < 0) continue;
            String k = URLDecoder.decode(pair.substring(0, eq), StandardCharsets.UTF_8.name());
            String v = URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8.name());
            result.put(k, v);
        }
        return result;
    }
}
