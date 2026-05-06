package com.nihongodev.platform.application.command;

public record CorrectTextCommand(
        String text,
        String textType,
        String targetLevel
) {}
