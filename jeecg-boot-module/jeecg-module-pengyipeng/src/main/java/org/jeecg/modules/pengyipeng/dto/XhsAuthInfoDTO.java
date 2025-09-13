package org.jeecg.modules.pengyipeng.dto;

/*
 * ClassName: XhsAuthInfoDTO
 * Package: org.jeecg.modules.pengyipeng.dto
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/27 - 9:23
 * @Version: v1.0
 */

import lombok.Data;

@Data
public class XhsAuthInfoDTO {
    private String appKey;
    private String nonce;
    private String timestamp;
    private String signature;
}
