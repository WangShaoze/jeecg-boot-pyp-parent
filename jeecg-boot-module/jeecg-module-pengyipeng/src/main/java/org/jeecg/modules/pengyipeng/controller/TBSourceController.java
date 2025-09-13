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
import org.jeecg.modules.pengyipeng.entity.TBSource;
import org.jeecg.modules.pengyipeng.service.ITBSourceService;

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
 * @Description: t_b_source
 * @Author: jeecg-boot
 * @Date:   2025-08-24
 * @Version: V1.0
 */
@Tag(name="前后端资源表")
@RestController
@RequestMapping("/pengyipeng/tBSource")
@Slf4j
public class TBSourceController extends JeecgController<TBSource, ITBSourceService> {
	@Autowired
	private ITBSourceService tBSourceService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tBSource
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "t_b_source-分页列表查询")
	@Operation(summary="t_b_source-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TBSource>> queryPageList(TBSource tBSource,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<TBSource> queryWrapper = QueryGenerator.initQueryWrapper(tBSource, req.getParameterMap());
		Page<TBSource> page = new Page<TBSource>(pageNo, pageSize);
		IPage<TBSource> pageList = tBSourceService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tBSource
	 * @return
	 */
	@AutoLog(value = "t_b_source-添加")
	@Operation(summary="t_b_source-添加")
	@RequiresPermissions("pengyipeng:t_b_source:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TBSource tBSource) {
		tBSourceService.save(tBSource);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tBSource
	 * @return
	 */
	@AutoLog(value = "t_b_source-编辑")
	@Operation(summary="t_b_source-编辑")
	@RequiresPermissions("pengyipeng:t_b_source:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TBSource tBSource) {
		tBSourceService.updateById(tBSource);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "t_b_source-通过id删除")
	@Operation(summary="t_b_source-通过id删除")
	@RequiresPermissions("pengyipeng:t_b_source:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tBSourceService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "t_b_source-批量删除")
	@Operation(summary="t_b_source-批量删除")
	@RequiresPermissions("pengyipeng:t_b_source:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tBSourceService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "t_b_source-通过id查询")
	@Operation(summary="t_b_source-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TBSource> queryById(@RequestParam(name="id",required=true) String id) {
		TBSource tBSource = tBSourceService.getById(id);
		if(tBSource==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tBSource);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tBSource
    */
    @RequiresPermissions("pengyipeng:t_b_source:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TBSource tBSource) {
        return super.exportXls(request, tBSource, TBSource.class, "t_b_source");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("pengyipeng:t_b_source:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TBSource.class);
    }

}
