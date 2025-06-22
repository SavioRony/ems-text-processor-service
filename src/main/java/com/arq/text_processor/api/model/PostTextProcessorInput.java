package com.arq.text_processor.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostTextProcessorInput {
    private String postId;
    private String postBody;
}
