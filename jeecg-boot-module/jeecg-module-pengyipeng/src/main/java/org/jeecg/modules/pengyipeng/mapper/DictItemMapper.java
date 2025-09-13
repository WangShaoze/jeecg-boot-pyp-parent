package org.jeecg.modules.pengyipeng.mapper;

/*
 * ClassName: DictMapper
 * Package: org.jeecg.modules.pengyipeng.mapper
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/17 - 14:31
 * @Version: v1.0
 */

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.pengyipeng.entity.SysDictItem;

import java.util.List;

@Mapper
public interface DictItemMapper {
    @Select("SELECT * FROM sys_dict_item WHERE dict_id = #{dictId} ORDER BY sort_order ASC")
    List<SysDictItem> findByDictId(String dictId);
}