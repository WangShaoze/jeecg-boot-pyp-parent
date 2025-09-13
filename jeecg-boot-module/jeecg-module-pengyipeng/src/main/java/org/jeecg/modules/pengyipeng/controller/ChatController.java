package org.jeecg.modules.pengyipeng.controller;


import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.modules.pengyipeng.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/pengyipeng/aiChat")

public class ChatController {
    @Autowired
    private AiService aiService;

    /**
     * AI 以流的方式响应 生成的数据
     */
    @Operation(summary = "App接口-流式生成")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getAiGenerateByStream(@RequestParam String question) {
        return aiService.getAiGenerateStream(question);
    }

    /**
     * AI一次性返回结果，适合简单问题的回答
     */
    @Operation(summary = "App接口-普通生成（适合字数较少的生成）")
    @GetMapping(value = "/generate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public String getAiGenerate(@RequestParam String question) {
        return aiService.getAiGenerate(question);
    }

    /**
     * AI一次性返回结果，请求超时时间是 300000 ms,也就是 5 minute
     */
    @Operation(summary = "App接口-异步生成（非流式）")
    @GetMapping(value = "/asyncGenerate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void getAiGenerate(HttpServletRequest request, HttpServletResponse response, @RequestParam String question) {
        aiService.getAiGenerate(request, response, question);
    }
}
