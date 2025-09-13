package org.jeecg.modules.pengyipeng.service.impl;

import org.jeecg.modules.pengyipeng.dto.GenerationRequestDTO;
import org.jeecg.modules.pengyipeng.entity.TBCopywritings;
import org.jeecg.modules.pengyipeng.mapper.TBCopywritingsMapper;
import org.jeecg.modules.pengyipeng.service.AiService;
import org.jeecg.modules.pengyipeng.service.ITBCopywritingsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

/**
 * @Description: 文案表
 * @Author: jeecg-boot
 * @Date:   2025-08-16
 * @Version: V1.0
 */
@Service
public class TBCopywritingsServiceImpl extends ServiceImpl<TBCopywritingsMapper, TBCopywritings> implements ITBCopywritingsService {

}
