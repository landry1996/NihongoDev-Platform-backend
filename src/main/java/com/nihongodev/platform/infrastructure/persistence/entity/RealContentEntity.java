package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "real_contents")
public class RealContentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(name = "title_reading", length = 500)
    private String titleReading;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "source_url", length = 1000)
    private String sourceUrl;

    @Column(nullable = false, length = 30)
    private String source;

    @Column(nullable = false, length = 30)
    private String domain;

    @Column(length = 10)
    private String difficulty;

    @Column(name = "reading_difficulty", length = 20)
    private String readingDifficulty;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<AnnotationJson> annotations;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> tags;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "key_vocabulary", columnDefinition = "jsonb")
    private List<String> keyVocabulary;

    @Column(name = "word_count")
    private int wordCount;

    @Column(name = "kanji_count")
    private int kanjiCount;

    @Column(name = "estimated_reading_minutes")
    private int estimatedReadingMinutes;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "author_name", length = 200)
    private String authorName;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "ingested_at")
    private LocalDateTime ingestedAt;

    @Column(name = "annotated_at")
    private LocalDateTime annotatedAt;

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
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getReadingDifficulty() { return readingDifficulty; }
    public void setReadingDifficulty(String readingDifficulty) { this.readingDifficulty = readingDifficulty; }
    public List<AnnotationJson> getAnnotations() { return annotations; }
    public void setAnnotations(List<AnnotationJson> annotations) { this.annotations = annotations; }
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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    public LocalDateTime getIngestedAt() { return ingestedAt; }
    public void setIngestedAt(LocalDateTime ingestedAt) { this.ingestedAt = ingestedAt; }
    public LocalDateTime getAnnotatedAt() { return annotatedAt; }
    public void setAnnotatedAt(LocalDateTime annotatedAt) { this.annotatedAt = annotatedAt; }

    public record AnnotationJson(
        String id,
        int startOffset,
        int endOffset,
        String surfaceForm,
        String reading,
        String meaning,
        String annotationType,
        String requiredLevel,
        String grammarNote,
        String culturalNote
    ) {}
}
