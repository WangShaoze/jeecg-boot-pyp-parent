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
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.pengyipeng.entity.SysDict;

import java.util.List;

@Mapper
public interface DictMapper {
    @Select("SELECT * FROM sys_dict WHERE dict_code = #{dictCode}")
    SysDict findByDictCode(String dictCode);

    @Select("SELECT * FROM sys_dict")
    List<SysDict> findAll();

    @Select("SELECT id, dict_name, dict_code, description FROM sys_dict where dict_name like concat('碰一碰', '%')")
    List<SysDict> findPypAllDict();

}