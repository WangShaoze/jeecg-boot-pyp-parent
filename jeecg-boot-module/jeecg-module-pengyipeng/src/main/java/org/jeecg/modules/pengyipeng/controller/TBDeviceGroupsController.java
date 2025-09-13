package org.jeecg.modules.pengyipeng.controller;

import java.awt.*;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.pengyipeng.entity.TBDeviceGroups;
import org.jeecg.modules.pengyipeng.entity.TBMerchants;
import org.jeecg.modules.pengyipeng.service.ITBDeviceGroupsService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.pengyipeng.service.ITBMerchantsService;
import org.jeecg.modules.pengyipeng.utils.QRCodeGenerator;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * @Description: 设别分组表
 * @Author: jeecg-boot
 * @Date: 2025-08-13
 * @Version: V1.0
 */
@Tag(name = "设别分组表")
@RestController
@RequestMapping("/pengyipeng/tBDeviceGroups")
@Slf4j
public class TBDeviceGroupsController extends JeecgController<TBDeviceGroups, ITBDeviceGroupsService> {
    @Autowired
    private ITBDeviceGroupsService tBDeviceGroupsService;

    @Value("${user-app.url}")
    private String userAppUrl;
    @Value("${user-app.logoUrl}")
    private String userAppDefaultLogoUrl;

    @Autowired
    @Lazy
    private ITBMerchantsService merchantsService;

    /**
     * 分页列表查询
     *
     * @param tBDeviceGroups
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "设别分组表-分页列表查询")
    @Operation(summary = "设别分组表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<TBDeviceGroups>> queryPageList(TBDeviceGroups tBDeviceGroups,
                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                       HttpServletRequest req) {
        QueryWrapper<TBDeviceGroups> queryWrapper = QueryGenerator.initQueryWrapper(tBDeviceGroups, req.getParameterMap());
        Page<TBDeviceGroups> page = new Page<TBDeviceGroups>(pageNo, pageSize);
        IPage<TBDeviceGroups> pageList = tBDeviceGroupsService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param tBDeviceGroups
     * @return
     */
    @AutoLog(value = "设别分组表-添加")
    @Operation(summary = "设别分组表-添加")
    @RequiresPermissions("pengyipeng:t_b_device_groups:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody TBDeviceGroups tBDeviceGroups) {
        tBDeviceGroupsService.save(tBDeviceGroups);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param tBDeviceGroups
     * @return
     */
    @AutoLog(value = "设别分组表-编辑")
    @Operation(summary = "设别分组表-编辑")
    @RequiresPermissions("pengyipeng:t_b_device_groups:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody TBDeviceGroups tBDeviceGroups) {
        tBDeviceGroupsService.updateById(tBDeviceGroups);
        return Result.OK("编辑成功!");
    }


    /**
     * 生成该商家的二维码
     *
     * @param merchantLicense 证书
     * @param width           宽度，默认300
     * @param height          高度，默认300
     * @return
     */
    @AutoLog(value = "设别分组表-生成该商家的二维码")
    @Operation(summary = "设别分组表-生成该商家的二维码")
    @RequestMapping(value = "/generateQrCode", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<byte[]> generateQrCode(
            @RequestParam(name = "merchantLicense") String merchantLicense,
            @RequestParam(defaultValue = "300", required = false) int width,
            @RequestParam(defaultValue = "300", required = false) int height
    ) {
        try {

            // 创建二维码配置
            QRCodeGenerator.QRCodeConfig config = new QRCodeGenerator.QRCodeConfig();

            // 自定义样式 - 示例配置
            config.setForegroundColor(new Color(0x2C3E50)); // 深蓝色前景
            config.setBackgroundColor(new Color(0xECF0F1)); // 浅灰色背景
            config.setMargin(2); // 边距
            config.setErrorCorrectionLevel(ErrorCorrectionLevel.H); // 高纠错级别（适合添加Logo）
            config.setRoundedCorners(true); // 圆角效果
            /*TBMerchants merchant = merchantsService.getByLicenseKey(merchantLicense);
            String logoUrl = null;
            if (null != merchant) {
                logoUrl = merchant.getLogoUrl();
            }
            if (StringUtils.isEmpty(logoUrl)) {
                logoUrl = userAppDefaultLogoUrl;
            }
            // 如果提供了logoUrl，则从URL获取Logo
            if (logoUrl != null && !logoUrl.isEmpty()) {
                InputStream logoStream = tBDeviceGroupsService.getInputStreamFromUrl(logoUrl);
                if (logoStream != null) {
                    config.setLogoStream(logoStream);
                    config.setLogoSizeRatio(0.25f); // Logo大小为二维码的25%
                }
            }*/

            // 生成自定义二维码
            // https://pyp.ylkj668.com/h5/#/?licenseKey=7c92160b-ae4f-4455-9331-91671b64b4ec
            if (userAppUrl != null && !userAppUrl.isEmpty() && userAppUrl.endsWith("/")) {
                userAppUrl = userAppUrl.substring(0, userAppUrl.length() - 1);
            }
            byte[] qrCodeImage = QRCodeGenerator.generateCustomQRCode(userAppUrl + "/?licenseKey=" + merchantLicense, width, height, config);

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCodeImage.length);
            // 编码文件名，防止中文乱码
            String encodedFilename = URLEncoder.encode(merchantLicense.replace("-", ""), StandardCharsets.UTF_8) + ".png";
            // 添加下载头信息，让浏览器自动下载
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment;filename=" + encodedFilename + ";");
            log.info("二维码生成成功！");
            // 返回二维码图片
            return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);
        } catch (WriterException | IOException e) {
            log.error("二维码生成失败！{}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "设别分组表-通过id删除")
    @Operation(summary = "设别分组表-通过id删除")
    @RequiresPermissions("pengyipeng:t_b_device_groups:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        tBDeviceGroupsService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "设别分组表-批量删除")
    @Operation(summary = "设别分组表-批量删除")
    @RequiresPermissions("pengyipeng:t_b_device_groups:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.tBDeviceGroupsService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "设别分组表-通过id查询")
    @Operation(summary = "设别分组表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<TBDeviceGroups> queryById(@RequestParam(name = "id", required = true) String id) {
        TBDeviceGroups tBDeviceGroups = tBDeviceGroupsService.getById(id);
        if (tBDeviceGroups == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(tBDeviceGroups);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param tBDeviceGroups
     */
    @RequiresPermissions("pengyipeng:t_b_device_groups:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TBDeviceGroups tBDeviceGroups) {
        return super.exportXls(request, tBDeviceGroups, TBDeviceGroups.class, "设别分组表");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("pengyipeng:t_b_device_groups:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TBDeviceGroups.class);
    }

}
