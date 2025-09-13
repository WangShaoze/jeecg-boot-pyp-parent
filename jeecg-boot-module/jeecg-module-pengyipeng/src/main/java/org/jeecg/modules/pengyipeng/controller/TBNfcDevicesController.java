package org.jeecg.modules.pengyipeng.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.pengyipeng.entity.TBNfcDevices;
import org.jeecg.modules.pengyipeng.service.ITBNfcDevicesService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 设备表
 * @Author: jeecg-boot
 * @Date:   2025-08-13
 * @Version: V1.0
 */
@Tag(name="设备表")
@RestController
@RequestMapping("/pengyipeng/tBNfcDevices")
@Slf4j
public class TBNfcDevicesController extends JeecgController<TBNfcDevices, ITBNfcDevicesService> {
	@Autowired
	private ITBNfcDevicesService tBNfcDevicesService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tBNfcDevices
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "设备表-分页列表查询")
	@Operation(summary="设备表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TBNfcDevices>> queryPageList(TBNfcDevices tBNfcDevices,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<TBNfcDevices> queryWrapper = QueryGenerator.initQueryWrapper(tBNfcDevices, req.getParameterMap());
		Page<TBNfcDevices> page = new Page<TBNfcDevices>(pageNo, pageSize);
		IPage<TBNfcDevices> pageList = tBNfcDevicesService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tBNfcDevices
	 * @return
	 */
	@AutoLog(value = "设备表-添加")
	@Operation(summary="设备表-添加")
	@RequiresPermissions("pengyipeng:t_b_nfc_devices:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TBNfcDevices tBNfcDevices) {
		tBNfcDevicesService.save(tBNfcDevices);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tBNfcDevices
	 * @return
	 */
	@AutoLog(value = "设备表-编辑")
	@Operation(summary="设备表-编辑")
	@RequiresPermissions("pengyipeng:t_b_nfc_devices:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TBNfcDevices tBNfcDevices) {
		tBNfcDevicesService.updateById(tBNfcDevices);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "设备表-通过id删除")
	@Operation(summary="设备表-通过id删除")
	@RequiresPermissions("pengyipeng:t_b_nfc_devices:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tBNfcDevicesService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "设备表-批量删除")
	@Operation(summary="设备表-批量删除")
	@RequiresPermissions("pengyipeng:t_b_nfc_devices:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tBNfcDevicesService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "设备表-通过id查询")
	@Operation(summary="设备表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TBNfcDevices> queryById(@RequestParam(name="id",required=true) String id) {
		TBNfcDevices tBNfcDevices = tBNfcDevicesService.getById(id);
		if(tBNfcDevices==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tBNfcDevices);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tBNfcDevices
    */
    @RequiresPermissions("pengyipeng:t_b_nfc_devices:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TBNfcDevices tBNfcDevices) {
        return super.exportXls(request, tBNfcDevices, TBNfcDevices.class, "设备表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("pengyipeng:t_b_nfc_devices:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TBNfcDevices.class);
    }

}
