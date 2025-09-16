package org.jeecg.modules.pengyipeng.dto;

/*
 * ClassName: MerchantLoginDto
 * Package: org.jeecg.modules.pengyipeng.dto
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/9/8 - 14:55
 * @Version: v1.0
 */

import lombok.Data;

@Data
public class MerchantLoginDto {
    private String id;
    private String username;
    private String realname;
    private String workNo;
    private String selectedroles;
    private Integer userIdentity;
    private String email;
    private String phone;
    private Integer activitiSync;
    private String password;
    private String salt;
    private String departIds;
    private Integer status;
    private Integer delFlag;
}
