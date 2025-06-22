package com.arq.text_processor.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostProcessingResult {
    private String postId;
    private int wordCount;
    private double calculatedValue;
}