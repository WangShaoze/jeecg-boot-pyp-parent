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
import org.jeecg.modules.pengyipeng.entity.TBPackageCopywritingMiddle;
import org.jeecg.modules.pengyipeng.service.ITBPackageCopywritingMiddleService;

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
 * @Description: 套餐和文案的中间表
 * @Author: jeecg-boot
 * @Date:   2025-08-28
 * @Version: V1.0
 */
@Tag(name="套餐和文案的中间表")
@RestController
@RequestMapping("/pengyipeng/tBPackageCopywritingMiddle")
@Slf4j
public class TBPackageCopywritingMiddleController extends JeecgController<TBPackageCopywritingMiddle, ITBPackageCopywritingMiddleService> {
	@Autowired
	private ITBPackageCopywritingMiddleService tBPackageCopywritingMiddleService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tBPackageCopywritingMiddle
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "套餐和文案的中间表-分页列表查询")
	@Operation(summary="套餐和文案的中间表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TBPackageCopywritingMiddle>> queryPageList(TBPackageCopywritingMiddle tBPackageCopywritingMiddle,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<TBPackageCopywritingMiddle> queryWrapper = QueryGenerator.initQueryWrapper(tBPackageCopywritingMiddle, req.getParameterMap());
		Page<TBPackageCopywritingMiddle> page = new Page<TBPackageCopywritingMiddle>(pageNo, pageSize);
		IPage<TBPackageCopywritingMiddle> pageList = tBPackageCopywritingMiddleService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tBPackageCopywritingMiddle
	 * @return
	 */
	@AutoLog(value = "套餐和文案的中间表-添加")
	@Operation(summary="套餐和文案的中间表-添加")
	@RequiresPermissions("pengyipeng:t_b_package_copywriting_middle:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TBPackageCopywritingMiddle tBPackageCopywritingMiddle) {
		tBPackageCopywritingMiddleService.save(tBPackageCopywritingMiddle);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tBPackageCopywritingMiddle
	 * @return
	 */
	@AutoLog(value = "套餐和文案的中间表-编辑")
	@Operation(summary="套餐和文案的中间表-编辑")
	@RequiresPermissions("pengyipeng:t_b_package_copywriting_middle:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TBPackageCopywritingMiddle tBPackageCopywritingMiddle) {
		tBPackageCopywritingMiddleService.updateById(tBPackageCopywritingMiddle);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "套餐和文案的中间表-通过id删除")
	@Operation(summary="套餐和文案的中间表-通过id删除")
	@RequiresPermissions("pengyipeng:t_b_package_copywriting_middle:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tBPackageCopywritingMiddleService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "套餐和文案的中间表-批量删除")
	@Operation(summary="套餐和文案的中间表-批量删除")
	@RequiresPermissions("pengyipeng:t_b_package_copywriting_middle:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tBPackageCopywritingMiddleService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "套餐和文案的中间表-通过id查询")
	@Operation(summary="套餐和文案的中间表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TBPackageCopywritingMiddle> queryById(@RequestParam(name="id",required=true) String id) {
		TBPackageCopywritingMiddle tBPackageCopywritingMiddle = tBPackageCopywritingMiddleService.getById(id);
		if(tBPackageCopywritingMiddle==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tBPackageCopywritingMiddle);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tBPackageCopywritingMiddle
    */
    @RequiresPermissions("pengyipeng:t_b_package_copywriting_middle:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TBPackageCopywritingMiddle tBPackageCopywritingMiddle) {
        return super.exportXls(request, tBPackageCopywritingMiddle, TBPackageCopywritingMiddle.class, "套餐和文案的中间表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("pengyipeng:t_b_package_copywriting_middle:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TBPackageCopywritingMiddle.class);
    }

}
