package com.nihongodev.platform.application.service.realcontent.pipeline;

import com.nihongodev.platform.domain.model.RealContent;
import org.springframework.stereotype.Component;

@Component
public class TextNormalizationStep implements IngestionStep {

    @Override
    public int order() {
        return 1;
    }

    @Override
    public void process(RealContent content) {
        String body = content.getBody();
        if (body == null) return;

        body = normalizeWhitespace(body);
        body = normalizeFullWidth(body);
        content.setBody(body);
    }

    private String normalizeWhitespace(String text) {
        return text.replaceAll("\\r\\n", "\n")
                   .replaceAll("[ \\t]+\\n", "\n")
                   .replaceAll("\\n{3,}", "\n\n")
                   .strip();
    }

    private String normalizeFullWidth(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (char c : text.toCharArray()) {
            if (c >= '！' && c <= '～') {
                sb.append((char) (c - 0xFEE0));
            } else if (c == '　') {
                sb.append(' ');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
