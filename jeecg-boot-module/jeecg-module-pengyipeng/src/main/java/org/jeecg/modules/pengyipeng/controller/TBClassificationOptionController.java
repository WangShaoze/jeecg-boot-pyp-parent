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
import org.jeecg.modules.pengyipeng.entity.TBClassificationOption;
import org.jeecg.modules.pengyipeng.service.ITBClassificationOptionService;

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
 * @Description: 商家标签分类可选项
 * @Author: jeecg-boot
 * @Date:   2025-09-16
 * @Version: V1.0
 */
@Tag(name="商家标签分类可选项")
@RestController
@RequestMapping("/pengyipeng/tBClassificationOption")
@Slf4j
public class TBClassificationOptionController extends JeecgController<TBClassificationOption, ITBClassificationOptionService> {
	@Autowired
	private ITBClassificationOptionService tBClassificationOptionService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tBClassificationOption
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "商家标签分类可选项-分页列表查询")
	@Operation(summary="商家标签分类可选项-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TBClassificationOption>> queryPageList(TBClassificationOption tBClassificationOption,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<TBClassificationOption> queryWrapper = QueryGenerator.initQueryWrapper(tBClassificationOption, req.getParameterMap());
		Page<TBClassificationOption> page = new Page<TBClassificationOption>(pageNo, pageSize);
		IPage<TBClassificationOption> pageList = tBClassificationOptionService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tBClassificationOption
	 * @return
	 */
	@AutoLog(value = "商家标签分类可选项-添加")
	@Operation(summary="商家标签分类可选项-添加")
	@RequiresPermissions("pengyipeng:t_b_classification_option:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TBClassificationOption tBClassificationOption) {
		tBClassificationOptionService.save(tBClassificationOption);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tBClassificationOption
	 * @return
	 */
	@AutoLog(value = "商家标签分类可选项-编辑")
	@Operation(summary="商家标签分类可选项-编辑")
	@RequiresPermissions("pengyipeng:t_b_classification_option:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TBClassificationOption tBClassificationOption) {
		tBClassificationOptionService.updateById(tBClassificationOption);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "商家标签分类可选项-通过id删除")
	@Operation(summary="商家标签分类可选项-通过id删除")
	@RequiresPermissions("pengyipeng:t_b_classification_option:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tBClassificationOptionService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "商家标签分类可选项-批量删除")
	@Operation(summary="商家标签分类可选项-批量删除")
	@RequiresPermissions("pengyipeng:t_b_classification_option:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tBClassificationOptionService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "商家标签分类可选项-通过id查询")
	@Operation(summary="商家标签分类可选项-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TBClassificationOption> queryById(@RequestParam(name="id",required=true) String id) {
		TBClassificationOption tBClassificationOption = tBClassificationOptionService.getById(id);
		if(tBClassificationOption==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tBClassificationOption);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tBClassificationOption
    */
    @RequiresPermissions("pengyipeng:t_b_classification_option:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TBClassificationOption tBClassificationOption) {
        return super.exportXls(request, tBClassificationOption, TBClassificationOption.class, "商家标签分类可选项");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("pengyipeng:t_b_classification_option:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TBClassificationOption.class);
    }

}
