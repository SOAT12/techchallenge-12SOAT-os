package com.fiap.soat12.os.cleanarch.infrastructure.messaging.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.soat12.os.cleanarch.infrastructure.web.presenter.dto.StockItemsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;


@Slf4j
@Component
@RequiredArgsConstructor
public class SqsEventPublisher {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.queue.stock-remove}")
    private String stockRemoveQueueUrl;

    @Value("${aws.sqs.queue.stock-add-event}")
    private String stockAddQueueUrl;

    public void publishRemoveStock(StockItemsDto stockRemoveItemsDto) {
        try {

            String message = objectMapper.writeValueAsString(stockRemoveItemsDto);

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(stockRemoveQueueUrl)
                    .messageBody(message)
                    .build();

            SendMessageResponse response = sqsClient.sendMessage(request);
            log.info("[SQS_EVENT_PUBLISHER] Mensagem publicada: {}", response);
        } catch (Exception e) {
            log.error("[SQS_EVENT_PUBLISHER] Erro ao publicar mensagem para atualizar status da OS", e);
            throw new RuntimeException("Erro ao publicar status da OS", e);
        }
    }

    public void publishAddStock(StockItemsDto stockAddItemsDto) {
        try {

            String message = objectMapper.writeValueAsString(stockAddItemsDto);

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(stockAddQueueUrl)
                    .messageBody(message)
                    .build();

            SendMessageResponse response = sqsClient.sendMessage(request);
            log.info("[SQS_EVENT_PUBLISHER] Mensagem publicada: {}", response);
        } catch (Exception e) {
            log.error("[SQS_EVENT_PUBLISHER] Erro ao publicar mensagem para atualizar status da OS", e);
            throw new RuntimeException("Erro ao publicar status da OS", e);
        }
    }
}