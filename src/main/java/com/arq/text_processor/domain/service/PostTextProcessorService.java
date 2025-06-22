package com.arq.text_processor.domain.service;

import com.arq.text_processor.api.model.PostProcessingResult;
import com.arq.text_processor.api.model.PostTextProcessorInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.arq.text_processor.rabbitmq.RabbitMQConfig.EXCHANGE_TEXT_PROCESSOR_RESULT;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostTextProcessorService {

    private final RabbitTemplate rabbitTemplate;

    public void processingText(PostTextProcessorInput input) {
        if (input == null || input.getPostId() == null || input.getPostBody() == null) {
            log.warn("🚫 Entrada inválida para processamento: input nulo ou incompleto");
            return;
        }

        log.info("🔧 Iniciando processamento do texto para postId={}", input.getPostId());

        int wordCount = countWords(input.getPostBody());
        double calculatedValue = wordCount * 0.10;

        log.debug("📊 postId={} | wordCount={} | calculatedValue={}",
                input.getPostId(), wordCount, calculatedValue);

        PostProcessingResult result = new PostProcessingResult(
                input.getPostId(),
                wordCount,
                calculatedValue
        );

        try {
            rabbitTemplate.convertAndSend(EXCHANGE_TEXT_PROCESSOR_RESULT, "", result);
            log.info("📤 Resultado enviado para exchange '{}': {}", EXCHANGE_TEXT_PROCESSOR_RESULT, result);
        } catch (Exception e) {
            log.error("❌ Falha ao enviar resultado para o RabbitMQ: {}", e.getMessage(), e);
            throw e;
        }
    }

    private int countWords(String text) {
        return text == null || text.isBlank() ? 0 : text.trim().split("\\s+").length;
    }
}
