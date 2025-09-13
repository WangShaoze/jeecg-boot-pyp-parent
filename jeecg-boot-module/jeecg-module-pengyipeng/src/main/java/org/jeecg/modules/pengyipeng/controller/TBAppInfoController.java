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
import org.jeecg.modules.pengyipeng.entity.TBAppInfo;
import org.jeecg.modules.pengyipeng.service.ITBAppInfoService;

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
 * @Description: APP相关信息
 * @Author: jeecg-boot
 * @Date:   2025-09-09
 * @Version: V1.0
 */
@Tag(name="APP相关信息")
@RestController
@RequestMapping("/pengyipeng/tBAppInfo")
@Slf4j
public class TBAppInfoController extends JeecgController<TBAppInfo, ITBAppInfoService> {
	@Autowired
	private ITBAppInfoService tBAppInfoService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tBAppInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "APP相关信息-分页列表查询")
	@Operation(summary="APP相关信息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TBAppInfo>> queryPageList(TBAppInfo tBAppInfo,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<TBAppInfo> queryWrapper = QueryGenerator.initQueryWrapper(tBAppInfo, req.getParameterMap());
		Page<TBAppInfo> page = new Page<TBAppInfo>(pageNo, pageSize);
		IPage<TBAppInfo> pageList = tBAppInfoService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tBAppInfo
	 * @return
	 */
	@AutoLog(value = "APP相关信息-添加")
	@Operation(summary="APP相关信息-添加")
	@RequiresPermissions("pengyipeng:t_b_app_info:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TBAppInfo tBAppInfo) {
		tBAppInfoService.save(tBAppInfo);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tBAppInfo
	 * @return
	 */
	@AutoLog(value = "APP相关信息-编辑")
	@Operation(summary="APP相关信息-编辑")
	@RequiresPermissions("pengyipeng:t_b_app_info:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TBAppInfo tBAppInfo) {
		tBAppInfoService.updateById(tBAppInfo);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "APP相关信息-通过id删除")
	@Operation(summary="APP相关信息-通过id删除")
	@RequiresPermissions("pengyipeng:t_b_app_info:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tBAppInfoService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "APP相关信息-批量删除")
	@Operation(summary="APP相关信息-批量删除")
	@RequiresPermissions("pengyipeng:t_b_app_info:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tBAppInfoService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "APP相关信息-通过id查询")
	@Operation(summary="APP相关信息-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TBAppInfo> queryById(@RequestParam(name="id",required=true) String id) {
		TBAppInfo tBAppInfo = tBAppInfoService.getById(id);
		if(tBAppInfo==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tBAppInfo);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tBAppInfo
    */
    @RequiresPermissions("pengyipeng:t_b_app_info:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TBAppInfo tBAppInfo) {
        return super.exportXls(request, tBAppInfo, TBAppInfo.class, "APP相关信息");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("pengyipeng:t_b_app_info:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TBAppInfo.class);
    }

}
