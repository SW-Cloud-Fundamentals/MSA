package com.example.sentimentservcie.Dto.Sentiment;


import com.example.sentimentservcie.Model.Sentiment;
import lombok.Data;

import java.io.Serializable;

@Data
public class SentimentDto implements Serializable {
    private Long articleId;
    private Long commentId;
    private Sentiment sentiment;
}
