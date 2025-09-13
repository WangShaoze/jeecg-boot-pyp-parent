package org.jeecg.modules.pengyipeng.service;

import org.jeecg.modules.pengyipeng.dto.GenerationRequestDTO;
import org.jeecg.modules.pengyipeng.entity.TBCopywritings;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

/**
 * @Description: 文案表
 * @Author: jeecg-boot
 * @Date:   2025-08-16
 * @Version: V1.0
 */
public interface ITBCopywritingsService extends IService<TBCopywritings> {
}
