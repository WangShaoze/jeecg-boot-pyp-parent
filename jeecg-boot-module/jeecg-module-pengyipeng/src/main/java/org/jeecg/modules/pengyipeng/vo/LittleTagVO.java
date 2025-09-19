package org.jeecg.modules.pengyipeng.vo;

/*
 * ClassName: LittleTagVO
 * Package: org.jeecg.modules.pengyipeng.vo
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/9/16 - 15:25
 * @Version: v1.0
 */

import lombok.Data;
import org.jeecg.modules.pengyipeng.entity.TBMerchantLittleTag;

import java.util.ArrayList;
import java.util.List;

@Data
public class LittleTagVO {
    private String id;
    private String tagName;
    private String classificationMiddleId;


    public static LittleTagVO create(TBMerchantLittleTag littleTag) {
        LittleTagVO vo = new LittleTagVO();
        vo.setId(littleTag.getId());
        vo.setTagName(littleTag.getTagName());
        vo.setClassificationMiddleId(littleTag.getClassificationMiddleId());
        return vo;
    }

    public static List<LittleTagVO> create(List<TBMerchantLittleTag> littleTagList) {
        List<LittleTagVO> voList = new ArrayList<>(littleTagList.size());
        littleTagList.forEach(littleTag -> voList.add(create(littleTag)));
        return voList;
    }
}
