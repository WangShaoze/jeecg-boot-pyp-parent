package org.jeecg.modules.pengyipeng.service.impl;

/*
 * ClassName: Dict
 * Package: org.jeecg.modules.pengyipeng.service.impl
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/17 - 14:37
 * @Version: v1.0
 */

import org.jeecg.modules.pengyipeng.entity.SysDict;
import org.jeecg.modules.pengyipeng.entity.SysDictItem;
import org.jeecg.modules.pengyipeng.mapper.DictItemMapper;
import org.jeecg.modules.pengyipeng.mapper.DictMapper;
import org.jeecg.modules.pengyipeng.service.DictCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DictCacheServiceImpl implements DictCacheService {
    private final Map<String, List<SysDictItem>> cache = new ConcurrentHashMap<>();

    @Autowired
    private DictMapper dictMapper;
    @Autowired
    private DictItemMapper dictItemMapper;


    @PostConstruct
    public void loadAll() {
        List<SysDict> dicts = dictMapper.findAll();
        for (SysDict dict : dicts) {
            List<SysDictItem> items = dictItemMapper.findByDictId(dict.getId());
            cache.put(dict.getDictCode(), items);
        }
    }

    public List<SysDictItem> getDict(String dictCode) {
        return cache.get(dictCode);
    }

    public void refresh(String dictCode) {
        SysDict dict = dictMapper.findByDictCode(dictCode);
        if (dict != null) {
            List<SysDictItem> items = dictItemMapper.findByDictId(dict.getId());
            cache.put(dictCode, items);
        }
    }

    @Override
    public List<SysDict> findPypAllDict(){
        return dictMapper.findPypAllDict();
    }

    @Override
    public List<SysDictItem> findKeyCodeById(String id) {
        return dictItemMapper.findByDictId(id);
    }

    @Override
    public List<SysDictItem> findKeyCodeByDictCode(String dictCode) {
        SysDict sysDict = dictMapper.findByDictCode(dictCode);
        return dictItemMapper.findByDictId(sysDict.getId());
    }
}
