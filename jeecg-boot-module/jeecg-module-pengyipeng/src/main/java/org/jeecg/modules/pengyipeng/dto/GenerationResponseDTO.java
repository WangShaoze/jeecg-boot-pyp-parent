package org.jeecg.modules.pengyipeng.dto;

/*
 * ClassName: kop
 * Package: org.jeecg.modules.pengyipeng.dto
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/16 - 16:36
 * @Version: v1.0
 */

import lombok.Data;


@Data
public class GenerationResponseDTO {

    private String content;

    private String provider;

    private Boolean success;

    private String errorMessage;
}
