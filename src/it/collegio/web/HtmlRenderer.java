package it.collegio.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Carica template HTML dal classpath e fa sostituzione di placeholder nel formato {{chiave}}.
 *
 * Non e' un template engine completo (niente if/for): per cicli e condizioni si compone
 * la stringa nel handler e la si inietta come placeholder. Mantenere sempliciotto e didattico.
 */
public final class HtmlRenderer {

    private static final String TEMPLATES_PATH = "/it/collegio/viewsHtml/templates/";

    private HtmlRenderer() {
    }

    public static String render(String templateName, Map<String, String> values) {
        String html = loadTemplate(templateName);
        for (Map.Entry<String, String> e : values.entrySet()) {
            html = html.replace("{{" + e.getKey() + "}}", e.getValue() != null ? e.getValue() : "");
        }
        // Rimuove eventuali placeholder rimasti senza valore
        html = html.replaceAll("\\{\\{[a-zA-Z0-9_]+\\}\\}", "");
        return html;
    }

    public static String renderInLayout(String contentTemplate, String pageTitle,
                                        Map<String, String> contentValues,
                                        Map<String, String> layoutValues) {
        String content = render(contentTemplate, contentValues);
        layoutValues.put("title", pageTitle);
        layoutValues.put("content", content);
        return render("layout.html", layoutValues);
    }

    private static String loadTemplate(String name) {
        String path = TEMPLATES_PATH + name;
        try (InputStream in = HtmlRenderer.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IOException("Template non trovato sul classpath: " + path);
            }
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int n;
            while ((n = in.read(chunk)) > 0) {
                buf.write(chunk, 0, n);
            }
            return buf.toString(StandardCharsets.UTF_8.name());
        } catch (IOException ex) {
            throw new RuntimeException("Errore lettura template " + name, ex);
        }
    }

    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
