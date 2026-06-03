package it.collegio.web.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Serve risorse statiche (CSS, immagini) dal classpath sotto
 * /it/collegio/web/css/.
 */
public class StaticHandler implements HttpHandler {

    private static final String BASE = "/it/collegio/web/css/";

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        if (!path.startsWith("/static/")) {
            HandlerUtils.notFound(ex);
            return;
        }
        String resource = BASE + path.substring("/static/".length());
        try (InputStream in = StaticHandler.class.getResourceAsStream(resource)) {
            if (in == null) {
                HandlerUtils.notFound(ex);
                return;
            }
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int n;
            while ((n = in.read(chunk)) > 0) {
                buf.write(chunk, 0, n);
            }
            byte[] data = buf.toByteArray();
            ex.getResponseHeaders().set("Content-Type", contentType(resource));
            ex.sendResponseHeaders(200, data.length);
            ex.getResponseBody().write(data);
            ex.getResponseBody().close();
        }
    }

    private String contentType(String path) {
        String p = path.toLowerCase();
        if (p.endsWith(".css"))  return "text/css; charset=utf-8";
        if (p.endsWith(".js"))   return "application/javascript; charset=utf-8";
        if (p.endsWith(".png"))  return "image/png";
        if (p.endsWith(".jpg"))  return "image/jpeg";
        if (p.endsWith(".svg"))  return "image/svg+xml";
        return "application/octet-stream";
    }
}
