package com.nihongodev.platform.domain.model;

public class CorrectionContext {

    private TextType textType;
    private JapaneseLevel targetLevel;
    private boolean strictKeigo;
    private boolean itDomain;

    public CorrectionContext() {}

    public static CorrectionContext of(TextType textType, JapaneseLevel targetLevel) {
        CorrectionContext ctx = new CorrectionContext();
        ctx.textType = textType;
        ctx.targetLevel = targetLevel;
        ctx.strictKeigo = requiresStrictKeigo(textType);
        ctx.itDomain = true;
        return ctx;
    }

    public static CorrectionContext defaultContext() {
        return of(TextType.FREE_TEXT, JapaneseLevel.N3);
    }

    private static boolean requiresStrictKeigo(TextType type) {
        return type == TextType.EMAIL_TO_CLIENT || type == TextType.INTERVIEW_ANSWER;
    }

    public TextType getTextType() { return textType; }
    public void setTextType(TextType textType) { this.textType = textType; }
    public JapaneseLevel getTargetLevel() { return targetLevel; }
    public void setTargetLevel(JapaneseLevel targetLevel) { this.targetLevel = targetLevel; }
    public boolean isStrictKeigo() { return strictKeigo; }
    public void setStrictKeigo(boolean strictKeigo) { this.strictKeigo = strictKeigo; }
    public boolean isItDomain() { return itDomain; }
    public void setItDomain(boolean itDomain) { this.itDomain = itDomain; }
}
