package org.jeecg.modules.pengyipeng.dto;

/*
 * ClassName: WifiDTO
 * Package: org.jeecg.modules.pengyipeng.dto
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/20 - 12:52
 * @Version: v1.0
 */

import lombok.Data;

@Data
public class WifiDTO {
    private Integer merchantId;
    private String wifiUser;
    private String wifiPwd;
}
