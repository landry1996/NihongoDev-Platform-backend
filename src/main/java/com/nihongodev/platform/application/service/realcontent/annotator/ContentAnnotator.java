package com.nihongodev.platform.application.service.realcontent.annotator;

import com.nihongodev.platform.domain.model.ContentAnnotation;
import com.nihongodev.platform.domain.model.AnnotationType;

import java.util.List;

public interface ContentAnnotator {
    AnnotationType getType();
    List<ContentAnnotation> annotate(String text);
}
