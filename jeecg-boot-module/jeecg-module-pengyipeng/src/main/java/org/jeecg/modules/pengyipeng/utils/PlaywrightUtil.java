package org.jeecg.modules.pengyipeng.utils;

/*
 * ClassName: PlaywrightConfig
 * Package: org.jeecg.modules.pengyipeng.config
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/8/26 - 9:07
 * @Version: v1.0
 */

import com.microsoft.playwright.*;

public class PlaywrightUtil {
    private static final Playwright playwright;
    private static Browser browser;

    private static Browser createBrowser() {
        if (browser == null) {
            browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true)
            );
        }
        return browser;
    }

    static {
        playwright = Playwright.create();

    }

    public static Browser getBrowser() {
        return createBrowser();
    }


}
