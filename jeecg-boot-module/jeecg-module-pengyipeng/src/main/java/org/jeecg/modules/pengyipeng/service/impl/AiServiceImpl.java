package org.jeecg.modules.pengyipeng.service.impl;

/*
 * ClassName: AiServiceImpl
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/15 - 15:00
 * @Version: v1.0
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mchange.lang.IntegerUtils;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import org.jeecg.modules.pengyipeng.dto.GenerationRequestDTO;
import org.jeecg.modules.pengyipeng.dto.GenerationResponseDTO;
import org.jeecg.modules.pengyipeng.entity.SysDictItem;
import org.jeecg.modules.pengyipeng.entity.TBCopywritings;
import org.jeecg.modules.pengyipeng.service.AiService;
import org.jeecg.modules.pengyipeng.service.ITBCopywritingsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import javax.annotation.Resource;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AiServiceImpl implements AiService {

    @Value("${doubaoai.model}")
    private String doubaoaiModel;

    @Value("${doubaoai.apiKey}")
    private String doubaoaiApiKey;

    @Value("${doubaoai.botId}")
    private String botId;


    @Resource
    private ITBCopywritingsService copywritingsService;

    @Autowired
    private ArkAiClient arkAiClient;

    @Autowired
    private DictCacheServiceImpl dictCacheService;


    @Override
    public Flux<String> getAiGenerateStream(String question) {
        ArkService service = ArkService.builder().apiKey(doubaoaiApiKey).build();

        final List<ChatMessage> messages = new ArrayList<>();
        messages.add(ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是豆包，是由字节跳动开发的 AI 人工智能助手").build());
        messages.add(ChatMessage.builder().role(ChatMessageRole.USER).content(question).build());

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder().model(doubaoaiModel).messages(messages).stream(true) // 必须是流式
                .thinking(new ChatCompletionRequest.ChatCompletionRequestThinking("disabled")).maxTokens(3000).build();

        // 创建一个单播 Sinks，适合单个订阅者
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();

        // 调用 ArkService 的流式方法
        new Thread(() -> {
            try {
                service.streamChatCompletion(chatCompletionRequest).doOnError(Throwable::printStackTrace).blockingForEach(response -> {
                    if (response.getChoices() != null && !response.getChoices().isEmpty()) {
                        String content = String.valueOf(response.getChoices().get(0).getMessage().getContent());
                        if (content != null) {
                            System.out.print(content);
                            sink.tryEmitNext(content);
                        }
                    }
                });
            } catch (Exception e) {
                sink.tryEmitError(e);
            } finally {
                sink.tryEmitComplete();
                service.shutdownExecutor();
            }
        }).start();

        // 每个 SSE 事件之间增加一点延迟，让浏览器更容易渲染
        return sink.asFlux().delayElements(Duration.ofMillis(10));
    }

    @Override
    public String getAiGenerate(String question) {
        ArkService service = ArkService.builder().apiKey(doubaoaiApiKey).build();
        final List<ChatMessage> messages = new ArrayList<>();
        messages.add(ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是豆包，是由字节跳动开发的 AI 人工智能助手").build());
        messages.add(ChatMessage.builder().role(ChatMessageRole.USER).content(question).build());

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder().model(doubaoaiModel).messages(messages).stream(false) // 接口调用时，我们直接拿一次性完整返回
                .thinking(new ChatCompletionRequest.ChatCompletionRequestThinking("disabled")).maxTokens(3000).build();

        StringBuilder sb = new StringBuilder();
        service.createChatCompletion(chatCompletionRequest).getChoices().forEach(choice -> sb.append(choice.getMessage().getContent().toString()));
        // shutdown service
        service.shutdownExecutor();
        System.out.println(sb);
        return sb.toString();
    }


    @Override
    public void getAiGenerate(HttpServletRequest request, HttpServletResponse response, String question) {

        // 启用异步模式
        AsyncContext asyncContext = request.startAsync();
        // 单独设置此接口超时（毫秒）
        asyncContext.setTimeout(300000); // 5分钟

        // 异步执行任务
        asyncContext.start(() -> {
            ArkService service = ArkService.builder().apiKey(doubaoaiApiKey).build();
            try {

                final List<ChatMessage> messages = new ArrayList<>();
                messages.add(ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是豆包，是由字节跳动开发的 AI 人工智能助手").build());
                messages.add(ChatMessage.builder().role(ChatMessageRole.USER).content(question).build());

                ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder().model(doubaoaiModel).messages(messages).stream(false) // 接口调用时，我们直接拿一次性完整返回
                        .thinking(new ChatCompletionRequest.ChatCompletionRequestThinking("disabled")).maxTokens(3000).build();

                StringBuilder sb = new StringBuilder();
                service.createChatCompletion(chatCompletionRequest).getChoices().forEach(choice -> sb.append(choice.getMessage().getContent().toString()));

                System.out.println(sb);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                asyncContext.complete();
                // shutdown service
                service.shutdownExecutor();
            }
        });

    }

    @Override
    public GenerationResponseDTO aiGeneByPrompt(GenerationRequestDTO generationRequestDTO) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SseEmitter aiGeneByPrompt(GenerationRequestDTO generationRequestDTO, Boolean streamBack) {
        TBCopywritings tbCopywritings = getTbCopywritings(generationRequestDTO);
        tbCopywritings.setId(generationRequestDTO.getId());
        tbCopywritings.setSessionId(generationRequestDTO.getSessionId());
        tbCopywritings.setPrompt(generationRequestDTO.getPrompt());
        tbCopywritings.setProvider("火山方舟-DouBao1.6");
        tbCopywritings.setTone(generationRequestDTO.getTone());
        tbCopywritings.setPlatform(generationRequestDTO.getPlatform());
        tbCopywritings.setTemperature(generationRequestDTO.getTemperature());
        tbCopywritings.setTopP(generationRequestDTO.getTopP());
        tbCopywritings.setMaxTokens(generationRequestDTO.getMaxTokens());


        // 构建模型
        final List<ChatMessage> messages = new ArrayList<>();
        StringBuilder aiGenContent = new StringBuilder();
        StringBuilder systemContent = new StringBuilder();
        systemContent.append("你是豆包，是由字节跳动开发的 AI 人工智能助手");
        // 根据平台和语气调整提示词
        if (generationRequestDTO.getPrompt() != null && generationRequestDTO.getTone() != null) {
            systemContent
                    .append("，请以[").append(generationRequestDTO.getPrompt()).append("] 作为关键词，并以")
                    .append(getPlatformDescription(generationRequestDTO.getPlatform())).append("为发布平台")
                    .append("写一篇字数在")
                    .append(generationRequestDTO.getMaxTokens()).append("以下的评论。要求: 1.语气要")
                    .append(getToneDescription(generationRequestDTO.getTone())).append("\n2.文案中不使用任何markdown的语法");
        }

        messages.add(ChatMessage.builder().role(ChatMessageRole.SYSTEM)
                .content(systemContent.toString()).build());

        messages.add(ChatMessage.builder().role(ChatMessageRole.USER).content(generationRequestDTO.getPrompt()).build());


        SseEmitter emitter = new SseEmitter(0L); // 永远不超时
        // 使用CompletableFuture避免阻塞主线程
        CompletableFuture.runAsync(() -> {
            try {
                arkAiClient.streamBotChat(
                        botId, // 你的 Bot ID
                        messages,
                        generationRequestDTO,
                        chunk -> { // 每段数据推送
                            try {
                                if (chunk != null) {
                                    aiGenContent.append(chunk);
                                    emitter.send(chunk);
                                }
                            } catch (IOException e) {
                                clearAndGetErrInfo(aiGenContent, false, "AI生成错误，请重试！");
                                emitter.completeWithError(e);
                            }
                        },
                        () -> { // 完成
                            try {
                                emitter.send(SseEmitter.event().name("end").data("[DONE]"));
                            } catch (IOException ignored) {
                            } finally {
                                saveTbCopywritingsToDB(tbCopywritings);
                            }
                            emitter.complete();
                        },
                        error -> { // 出错
                            try {
                                clearAndGetErrInfo(aiGenContent, true, "AI生成错误，请重试！");
                                emitter.send(SseEmitter.event().name("error").data(error.getMessage()));
                            } catch (IOException ignored) {
                            }
                            emitter.completeWithError(error);
                        }
                );
            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event().name("error").data(e.getMessage()));
                } catch (IOException ioException) {
                    clearAndGetErrInfo(aiGenContent, true, "数据传输有问题，请重试！");
                }
                emitter.completeWithError(e);
            } finally {
                tbCopywritings.setContent(aiGenContent.toString());
                saveTbCopywritingsToDB(tbCopywritings);
            }
        });

        return emitter;
    }

    private TBCopywritings getTbCopywritings(GenerationRequestDTO generationRequestDTO) {
        TBCopywritings tbCopywritings = null;
        if (generationRequestDTO.getId() == null) {
            // 根据sessionId去数据库中查询一次，如果没有在创建新的对象【对应的情况是 新增的时候换一换 的场景】
            QueryWrapper<TBCopywritings> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("session_id", generationRequestDTO.getSessionId());
            TBCopywritings tbCopywritingsDb = copywritingsService.getBaseMapper().selectOne(queryWrapper);
            if (tbCopywritingsDb == null) {
                tbCopywritings = new TBCopywritings();
            } else {
                tbCopywritings = tbCopywritingsDb;
            }
        } else {
            // 编辑的时候
            QueryWrapper<TBCopywritings> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", generationRequestDTO.getId());
            TBCopywritings tbCopywritingsDb = copywritingsService.getBaseMapper().selectOne(queryWrapper);
            if (tbCopywritingsDb == null) {
                tbCopywritings = new TBCopywritings();
            } else {
                tbCopywritings = tbCopywritingsDb;
            }
        }
        return tbCopywritings;
    }


    public void saveTbCopywritingsToDB(TBCopywritings tbCopywritings) {
        copywritingsService.saveOrUpdate(tbCopywritings);
    }

    private static void clearAndGetErrInfo(StringBuilder aiGenContent, Boolean clear, String msg) {
        if (clear) {
            aiGenContent.delete(0, aiGenContent.length());
        }
        aiGenContent.append(msg);
    }


    /**
     * 根据语气代码获取描述
     */
    private String getToneDescription(Integer tone) {
        List<SysDictItem> sysDictItems = dictCacheService.getDict("tone_status");
        if (sysDictItems != null && !sysDictItems.isEmpty()) {
            for (SysDictItem sysDictItem : sysDictItems) {
                if (IntegerUtils.parseInt(sysDictItem.getItemValue(), 0) == tone) {
                    return sysDictItem.getItemText();
                }
            }
        }
        return "积极";
    }

    /**
     * 根据平台代码获取描述  ---> 平台代码与平台 需要从数据库的字典中获取
     */
    private String getPlatformDescription(Integer platform) {
        List<SysDictItem> sysDictItems = dictCacheService.getDict("platforms");
        if (sysDictItems != null && !sysDictItems.isEmpty()) {
            for (SysDictItem sysDictItem : sysDictItems) {
                if (IntegerUtils.parseInt(sysDictItem.getItemValue(), 3) == platform) {
                    return sysDictItem.getItemText();
                }
            }
        }
        return "小红书";
    }

}
