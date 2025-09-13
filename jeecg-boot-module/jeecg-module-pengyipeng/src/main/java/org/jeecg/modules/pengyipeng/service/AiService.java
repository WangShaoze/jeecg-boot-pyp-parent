package org.jeecg.modules.pengyipeng.service;


/*
 * ClassName: AiService
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/15 - 14:59
 * @Version: v1.0
 */


import org.jeecg.modules.pengyipeng.dto.GenerationRequestDTO;
import org.jeecg.modules.pengyipeng.dto.GenerationResponseDTO;
import org.jeecg.modules.pengyipeng.entity.TBCopywritings;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AiService {
    Flux<String> getAiGenerateStream( String question);
    String getAiGenerate(String question);
    void getAiGenerate(HttpServletRequest request, HttpServletResponse response, String question);

    GenerationResponseDTO aiGeneByPrompt(GenerationRequestDTO generationRequestDTO);
    SseEmitter aiGeneByPrompt(GenerationRequestDTO generationRequestDTO, Boolean streamBack);
}
