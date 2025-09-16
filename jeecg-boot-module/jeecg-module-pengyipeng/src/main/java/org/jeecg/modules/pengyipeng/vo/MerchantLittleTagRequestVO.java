package org.jeecg.modules.pengyipeng.vo;

/*
 * ClassName: KeywordClassificationVO
 * Package: org.jeecg.modules.pengyipeng.vo
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/9/16 - 10:10
 * @Version: v1.0
 */

import lombok.Data;

import java.util.List;

@Data
public class MerchantLittleTagRequestVO {
    private Integer merchantId;
    private String bigTagId;
    private List<String> keywordList;
    //private List<String> picList;
}
