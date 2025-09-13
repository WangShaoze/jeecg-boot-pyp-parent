package org.jeecg.modules.pengyipeng.dto;

/*
 * ClassName: MerchantServiceInfoDTO
 * Package: org.jeecg.modules.pengyipeng.dto
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/29 - 11:29
 * @Version: v1.0
 */


import lombok.Data;

import java.util.Date;

@Data
public class MerchantServiceInfoDTO {
    private String merchantName;
    private String licenseStatus;
    private Date serviceEndDate;
    private String contactPerson;
    private String contactPhone;
}
