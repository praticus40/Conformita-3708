package it.frank.conformita.core.validation;

public record ValidationIssue(ValidationContext context, String message) {}
