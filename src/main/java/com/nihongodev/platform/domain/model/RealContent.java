package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RealContent {

    private UUID id;
    private String title;
    private String titleReading;
    private String body;
    private String summary;
    private String sourceUrl;
    private ContentSource source;
    private ContentDomain domain;
    private JapaneseLevel difficulty;
    private ReadingDifficulty readingDifficulty;
    private List<ContentAnnotation> annotations;
    private List<String> tags;
    private List<String> keyVocabulary;
    private int wordCount;
    private int kanjiCount;
    private int estimatedReadingMinutes;
    private ContentStatus status;
    private String authorName;
    private LocalDateTime publishedAt;
    private LocalDateTime ingestedAt;
    private LocalDateTime annotatedAt;

    public RealContent() {
        this.annotations = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.keyVocabulary = new ArrayList<>();
    }

    public static RealContent ingest(String title, String body, String sourceUrl,
                                     ContentSource source, ContentDomain domain) {
        RealContent content = new RealContent();
        content.id = UUID.randomUUID();
        content.title = title;
        content.body = body;
        content.sourceUrl = sourceUrl;
        content.source = source;
        content.domain = domain;
        content.status = ContentStatus.INGESTED;
        content.wordCount = countWords(body);
        content.kanjiCount = countKanji(body);
        content.estimatedReadingMinutes = Math.max(1, content.wordCount / 200);
        content.ingestedAt = LocalDateTime.now();
        return content;
    }

    public void addAnnotation(ContentAnnotation annotation) {
        this.annotations.add(annotation);
    }

    public void markAnnotated() {
        this.status = ContentStatus.ANNOTATED;
        this.annotatedAt = LocalDateTime.now();
    }

    public void publish() {
        if (this.status != ContentStatus.ANNOTATED) {
            throw new IllegalStateException("Content must be annotated before publishing");
        }
        this.status = ContentStatus.PUBLISHED;
    }

    public void archive() {
        this.status = ContentStatus.ARCHIVED;
    }

    private static int countWords(String text) {
        if (text == null || text.isBlank()) return 0;
        return text.length();
    }

    private static int countKanji(String text) {
        if (text == null) return 0;
        int count = 0;
        for (char c : text.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                count++;
            }
        }
        return count;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTitleReading() { return titleReading; }
    public void setTitleReading(String titleReading) { this.titleReading = titleReading; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public ContentSource getSource() { return source; }
    public void setSource(ContentSource source) { this.source = source; }
    public ContentDomain getDomain() { return domain; }
    public void setDomain(ContentDomain domain) { this.domain = domain; }
    public JapaneseLevel getDifficulty() { return difficulty; }
    public void setDifficulty(JapaneseLevel difficulty) { this.difficulty = difficulty; }
    public ReadingDifficulty getReadingDifficulty() { return readingDifficulty; }
    public void setReadingDifficulty(ReadingDifficulty readingDifficulty) { this.readingDifficulty = readingDifficulty; }
    public List<ContentAnnotation> getAnnotations() { return annotations; }
    public void setAnnotations(List<ContentAnnotation> annotations) { this.annotations = annotations; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public List<String> getKeyVocabulary() { return keyVocabulary; }
    public void setKeyVocabulary(List<String> keyVocabulary) { this.keyVocabulary = keyVocabulary; }
    public int getWordCount() { return wordCount; }
    public void setWordCount(int wordCount) { this.wordCount = wordCount; }
    public int getKanjiCount() { return kanjiCount; }
    public void setKanjiCount(int kanjiCount) { this.kanjiCount = kanjiCount; }
    public int getEstimatedReadingMinutes() { return estimatedReadingMinutes; }
    public void setEstimatedReadingMinutes(int estimatedReadingMinutes) { this.estimatedReadingMinutes = estimatedReadingMinutes; }
    public ContentStatus getStatus() { return status; }
    public void setStatus(ContentStatus status) { this.status = status; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    public LocalDateTime getIngestedAt() { return ingestedAt; }
    public void setIngestedAt(LocalDateTime ingestedAt) { this.ingestedAt = ingestedAt; }
    public LocalDateTime getAnnotatedAt() { return annotatedAt; }
    public void setAnnotatedAt(LocalDateTime annotatedAt) { this.annotatedAt = annotatedAt; }
}
