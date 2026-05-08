package com.nihongodev.platform.infrastructure.security;

import org.springframework.stereotype.Component;

@Component
public class InputSanitizer {

    private static final String[] XSS_PATTERNS = {
            "<script", "</script", "javascript:", "onerror=", "onload=",
            "onclick=", "onmouseover=", "onfocus=", "eval(", "expression(",
            "vbscript:", "data:text/html"
    };

    public String sanitize(String input, int maxLength) {
        if (input == null) {
            return null;
        }
        String sanitized = input.trim();
        if (sanitized.length() > maxLength) {
            sanitized = sanitized.substring(0, maxLength);
        }
        sanitized = stripXss(sanitized);
        return sanitized;
    }

    public String sanitizeForLog(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("[\\r\\n\\t]", "_")
                .replaceAll("[^\\x20-\\x7E\\u3000-\\u9FFF\\uFF00-\\uFFEF]", "");
    }

    private String stripXss(String value) {
        String result = value;
        for (String pattern : XSS_PATTERNS) {
            result = result.replaceAll("(?i)" + escapeRegex(pattern), "");
        }
        result = result.replaceAll("<[^>]*>", "");
        return result;
    }

    private String escapeRegex(String literal) {
        return literal.replaceAll("([\\\\\\[\\]{}()*+?.^$|])", "\\\\$1");
    }
}
