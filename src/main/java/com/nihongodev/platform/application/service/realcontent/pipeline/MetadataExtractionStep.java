package com.nihongodev.platform.application.service.realcontent.pipeline;

import com.nihongodev.platform.domain.model.RealContent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MetadataExtractionStep implements IngestionStep {

    private static final Map<String, String> DOMAIN_TAGS = Map.of(
        "API", "api",
        "REST", "api",
        "Docker", "container",
        "Kubernetes", "orchestration",
        "React", "frontend",
        "Spring", "backend",
        "PostgreSQL", "database",
        "AWS", "cloud",
        "CI/CD", "devops",
        "セキュリティ", "security"
    );

    @Override
    public int order() {
        return 2;
    }

    @Override
    public void process(RealContent content) {
        String body = content.getBody();
        if (body == null) return;

        List<String> tags = extractTags(body);
        content.setTags(tags);

        List<String> keyVocab = extractKeyVocabulary(body);
        content.setKeyVocabulary(keyVocab);

        if (content.getTitleReading() == null) {
            content.setTitleReading(generateTitleReading(content.getTitle()));
        }
    }

    private List<String> extractTags(String text) {
        List<String> tags = new ArrayList<>();
        for (Map.Entry<String, String> entry : DOMAIN_TAGS.entrySet()) {
            if (text.contains(entry.getKey())) {
                if (!tags.contains(entry.getValue())) {
                    tags.add(entry.getValue());
                }
            }
        }
        return tags;
    }

    private List<String> extractKeyVocabulary(String text) {
        List<String> vocab = new ArrayList<>();
        Pattern kanjiPattern = Pattern.compile("[\\u4e00-\\u9faf]{2,4}");
        Matcher matcher = kanjiPattern.matcher(text);
        int count = 0;
        while (matcher.find() && count < 10) {
            String word = matcher.group();
            if (!vocab.contains(word)) {
                vocab.add(word);
                count++;
            }
        }
        return vocab;
    }

    private String generateTitleReading(String title) {
        return title;
    }
}
