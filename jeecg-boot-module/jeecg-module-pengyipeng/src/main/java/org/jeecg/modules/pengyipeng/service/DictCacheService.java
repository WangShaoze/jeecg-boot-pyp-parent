package org.jeecg.modules.pengyipeng.service;

/*
 * ClassName: DictCacheService
 * Package: org.jeecg.modules.pengyipeng.service
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/21 - 14:14
 * @Version: v1.0
 */

import org.jeecg.modules.pengyipeng.entity.SysDict;
import org.jeecg.modules.pengyipeng.entity.SysDictItem;

import java.util.List;

public interface DictCacheService {
    List<SysDict> findPypAllDict();

    List<SysDictItem> findKeyCodeById(String id);
    List<SysDictItem> findKeyCodeByDictCode(String dictCode);
}
