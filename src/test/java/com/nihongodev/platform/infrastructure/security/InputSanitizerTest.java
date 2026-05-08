package com.nihongodev.platform.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InputSanitizerTest {

    private InputSanitizer sanitizer;

    @BeforeEach
    void setUp() {
        sanitizer = new InputSanitizer();
    }

    @Test
    void sanitize_shouldReturnNull_whenInputIsNull() {
        assertThat(sanitizer.sanitize(null, 100)).isNull();
    }

    @Test
    void sanitize_shouldTrimWhitespace() {
        assertThat(sanitizer.sanitize("  hello  ", 100)).isEqualTo("hello");
    }

    @Test
    void sanitize_shouldTruncateToMaxLength() {
        String longInput = "a".repeat(200);
        assertThat(sanitizer.sanitize(longInput, 50)).hasSize(50);
    }

    @Test
    void sanitize_shouldRemoveScriptTags() {
        String xss = "<script>alert('xss')</script>Hello";
        String result = sanitizer.sanitize(xss, 1000);
        assertThat(result).doesNotContain("<script");
        assertThat(result).doesNotContain("</script");
        assertThat(result).contains("Hello");
    }

    @Test
    void sanitize_shouldRemoveJavascriptProtocol() {
        String xss = "javascript:alert(1)";
        String result = sanitizer.sanitize(xss, 1000);
        assertThat(result).doesNotContain("javascript:");
    }

    @Test
    void sanitize_shouldRemoveEventHandlers() {
        String xss = "text onerror=alert(1) more";
        String result = sanitizer.sanitize(xss, 1000);
        assertThat(result).doesNotContain("onerror=");
    }

    @Test
    void sanitize_shouldRemoveHtmlTags() {
        String html = "<div><b>bold</b></div>";
        String result = sanitizer.sanitize(html, 1000);
        assertThat(result).doesNotContain("<div>");
        assertThat(result).doesNotContain("<b>");
        assertThat(result).contains("bold");
    }

    @Test
    void sanitize_shouldPreserveJapaneseCharacters() {
        String japanese = "日本語のテスト";
        assertThat(sanitizer.sanitize(japanese, 100)).isEqualTo(japanese);
    }

    @Test
    void sanitize_shouldHandleSqlInjectionStrings() {
        String sql = "'; DROP TABLE users; --";
        String result = sanitizer.sanitize(sql, 1000);
        assertThat(result).isEqualTo("'; DROP TABLE users; --");
    }

    @Test
    void sanitizeForLog_shouldReplaceNewlines() {
        String input = "line1\nline2\rline3\ttab";
        String result = sanitizer.sanitizeForLog(input);
        assertThat(result).doesNotContain("\n");
        assertThat(result).doesNotContain("\r");
        assertThat(result).doesNotContain("\t");
    }

    @Test
    void sanitizeForLog_shouldReturnNull_whenInputIsNull() {
        assertThat(sanitizer.sanitizeForLog(null)).isNull();
    }
}
