package com.example.sentimentservcie.Dto;

public record ArticleSentimentSummary(
        String userRole,          // POLICE or USER
        int positive,
        int negative,
        int neutral
) {}