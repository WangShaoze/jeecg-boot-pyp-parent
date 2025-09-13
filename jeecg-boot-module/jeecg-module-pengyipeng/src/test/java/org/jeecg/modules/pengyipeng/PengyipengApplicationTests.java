package org.jeecg.modules.pengyipeng;

import org.jeecg.modules.pengyipeng.entity.TBLicenses;
import org.jeecg.modules.pengyipeng.service.ITBLicensesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class PengyipengApplicationTests {

    @Resource
    private ITBLicensesService itbLicensesService;

    @Test
    void contextLoads() {
        String license = "399780b0-28ef-4466-ae32-c36b2abc48f2";
        itbLicensesService.hasFourHyphens(license);
    }

}
