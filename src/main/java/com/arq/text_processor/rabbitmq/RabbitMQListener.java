package com.arq.text_processor.rabbitmq;

import com.arq.text_processor.api.model.PostTextProcessorInput;
import com.arq.text_processor.domain.service.PostTextProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static com.arq.text_processor.rabbitmq.RabbitMQConfig.QUEUE_TEXT_PROCESSOR;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQListener {

    private final PostTextProcessorService postTextProcessorResult;

    @RabbitListener(queues = QUEUE_TEXT_PROCESSOR, concurrency = "2-3")
    @SneakyThrows
    public void handleProcessingTemperature(@Payload PostTextProcessorInput postTextProcessorInput) {
        log.info("📥 Mensagem recebida da fila '{}': postId={}, tamanho do body={} caracteres",
                QUEUE_TEXT_PROCESSOR,
                postTextProcessorInput.getPostId(),
                postTextProcessorInput.getPostBody() != null ? postTextProcessorInput.getPostBody().length() : 0
        );

        try {
            postTextProcessorResult.processingText(postTextProcessorInput);
            log.info("✅ Processamento concluído para postId={}", postTextProcessorInput.getPostId());
        } catch (Exception e) {
            log.error("❌ Erro ao processar mensagem para postId={}: {}", postTextProcessorInput.getPostId(), e.getMessage(), e);
            throw e; // necessário para redirecionar para DLQ, se configurado
        }
    }
}
