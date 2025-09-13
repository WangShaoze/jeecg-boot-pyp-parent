package org.jeecg.modules.pengyipeng.service.impl;

/*
 * ClassName: ds
 * Package: org.jeecg.modules.pengyipeng.service.impl
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/26 - 8:42
 * @Version: v1.0
 */

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;


import com.microsoft.playwright.Request;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;
import org.jeecg.modules.pengyipeng.utils.PlaywrightUtil;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class PlaywrightServiceImpl {
    public String getFinalUrl(String url) {
        StringBuilder newUrl = new StringBuilder();
        Browser browser = PlaywrightUtil.getBrowser();
        Page page = browser.newPage();

        page.onFrameNavigated(frame -> {
            String changeUrl = frame.url();
            System.out.println("changeUrl:" + changeUrl);
            if (changeUrl.contains("?")) {
                newUrl.append(changeUrl);
            }
        });
        page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));
        page.close();
        return newUrl.toString();
    }
}

