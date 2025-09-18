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
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.pengyipeng.entity.TBClassificationMerchantMiddle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class TagClassificationVO {
    private String Id;
    private String classificationOptionId;
    private List<String> picList;

    public void transPicList(String picListStr) {
        if (!StringUtils.isEmpty(picListStr)) {
            picList = Arrays.asList(picListStr.split(","));
        } else {
            picList = new ArrayList<>();
        }
    }

    public static TagClassificationVO create(TBClassificationMerchantMiddle m) {
        TagClassificationVO vo = new TagClassificationVO();
        if (m != null){
            vo.setId(m.getId());
            vo.setClassificationOptionId(m.getClassificationOptionId());
            vo.transPicList(m.getPicList());
        }
        return vo;
    }
}
