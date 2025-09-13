package org.jeecg.modules.pengyipeng.service.impl;


import com.volcengine.ark.runtime.model.bot.completion.chat.BotChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionResult;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.service.ArkService;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.jeecg.modules.pengyipeng.dto.GenerationRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Component
public class ArkAiClient {

    private final ArkService service;


    public ArkAiClient(
            @Value("${doubaoai.apiKey}") String apiKey,
            @Value("${doubaoai.modelBaseUrl}") String baseUrl
    ) {
        ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
        Dispatcher dispatcher = new Dispatcher();

        this.service = ArkService.builder()
                .dispatcher(dispatcher)
                .connectionPool(connectionPool)
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
    }

    public ChatCompletionResult chat(ChatCompletionRequest request) {
        return service.createChatCompletion(request);
    }

    public void streamBotChat(String botId, List<ChatMessage> messages,
                              GenerationRequestDTO generationRequestDTO,
                              Consumer<String> onData,
                              Runnable onComplete,
                              Consumer<Throwable> onError) {

        BotChatCompletionRequest request = BotChatCompletionRequest.builder()
                .botId(botId)
                .maxTokens(generationRequestDTO.getMaxTokens())
                .temperature(generationRequestDTO.getTemperature())
                .topP(generationRequestDTO.getTopP())
                .messages(messages)
                .stream(true) // 确保启用流式传输
                .build();

        try {
            service.streamBotChatCompletion(request)
                    .doOnError(onError::accept)
                    .blockingForEach(choice -> {
                        if (choice != null && !choice.getChoices().isEmpty()) {
                            String delta = choice.getChoices().get(0).getMessage().getContent().toString();
                            if (delta != null && !delta.isEmpty()) {
                                onData.accept(delta);
                            }
                        }
                    });

            onComplete.run();
        } catch (Exception e) {
            onError.accept(e);
        }
    }
}