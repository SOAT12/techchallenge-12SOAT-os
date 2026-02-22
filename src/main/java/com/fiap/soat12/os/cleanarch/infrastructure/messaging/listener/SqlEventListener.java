package com.fiap.soat12.os.cleanarch.infrastructure.messaging.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;


@Slf4j
@Component
@RequiredArgsConstructor
public class SqlEventListener {

    @Value("${aws.sqs.os-status-queue-url}")
    private String queueOs;

    private final SqsClient sqsClient;

    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    public void listen() {
        var request = ReceiveMessageRequest.builder()
                .queueUrl(queueOs)
                .maxNumberOfMessages(5)
                .waitTimeSeconds(10)
                .build();

        var response = sqsClient.receiveMessage(request);

        response.messages().

        sqsClient.deleteMessage(builder -> builder
                .queueUrl(queueOs)
                .receiptHandle(msg.receiptHandle()));


        response.messages().forEach(msg -> {
            try {



            } catch (Exception e) {
                log.error("Erro ao processar evento", e);
            }
        });
    }
}
