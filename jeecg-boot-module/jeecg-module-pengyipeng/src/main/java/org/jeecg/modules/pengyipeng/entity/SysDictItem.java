package org.jeecg.modules.pengyipeng.entity;

/*
 * ClassName: SysDictItem
 * Package: org.jeecg.modules.pengyipeng.entity
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/17 - 14:27
 * @Version: v1.0
 */

import lombok.Data;

@Data
public class SysDictItem {
    private String id;
    private String dictId;
    private String itemText;
    private String itemValue;
}
