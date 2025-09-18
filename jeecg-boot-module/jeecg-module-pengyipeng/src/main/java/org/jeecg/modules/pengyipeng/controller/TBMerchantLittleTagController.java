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
import org.jeecg.modules.pengyipeng.entity.TBMerchantLittleTag;
import org.jeecg.modules.pengyipeng.service.ITBMerchantLittleTagService;

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
 * @Description: 商家的小标签表
 * @Author: jeecg-boot
 * @Date:   2025-09-18
 * @Version: V1.0
 */
@Tag(name="商家的小标签表")
@RestController
@RequestMapping("/pengyipeng/tBMerchantLittleTag")
@Slf4j
public class TBMerchantLittleTagController extends JeecgController<TBMerchantLittleTag, ITBMerchantLittleTagService> {
	@Autowired
	private ITBMerchantLittleTagService tBMerchantLittleTagService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tBMerchantLittleTag
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "商家的小标签表-分页列表查询")
	@Operation(summary="商家的小标签表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TBMerchantLittleTag>> queryPageList(TBMerchantLittleTag tBMerchantLittleTag,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<TBMerchantLittleTag> queryWrapper = QueryGenerator.initQueryWrapper(tBMerchantLittleTag, req.getParameterMap());
		Page<TBMerchantLittleTag> page = new Page<TBMerchantLittleTag>(pageNo, pageSize);
		IPage<TBMerchantLittleTag> pageList = tBMerchantLittleTagService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tBMerchantLittleTag
	 * @return
	 */
	@AutoLog(value = "商家的小标签表-添加")
	@Operation(summary="商家的小标签表-添加")
	@RequiresPermissions("pengyipeng:t_b_merchant_little_tag:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TBMerchantLittleTag tBMerchantLittleTag) {
		tBMerchantLittleTagService.save(tBMerchantLittleTag);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tBMerchantLittleTag
	 * @return
	 */
	@AutoLog(value = "商家的小标签表-编辑")
	@Operation(summary="商家的小标签表-编辑")
	@RequiresPermissions("pengyipeng:t_b_merchant_little_tag:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TBMerchantLittleTag tBMerchantLittleTag) {
		tBMerchantLittleTagService.updateById(tBMerchantLittleTag);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "商家的小标签表-通过id删除")
	@Operation(summary="商家的小标签表-通过id删除")
	@RequiresPermissions("pengyipeng:t_b_merchant_little_tag:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tBMerchantLittleTagService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "商家的小标签表-批量删除")
	@Operation(summary="商家的小标签表-批量删除")
	@RequiresPermissions("pengyipeng:t_b_merchant_little_tag:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tBMerchantLittleTagService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "商家的小标签表-通过id查询")
	@Operation(summary="商家的小标签表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TBMerchantLittleTag> queryById(@RequestParam(name="id",required=true) String id) {
		TBMerchantLittleTag tBMerchantLittleTag = tBMerchantLittleTagService.getById(id);
		if(tBMerchantLittleTag==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tBMerchantLittleTag);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tBMerchantLittleTag
    */
    @RequiresPermissions("pengyipeng:t_b_merchant_little_tag:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TBMerchantLittleTag tBMerchantLittleTag) {
        return super.exportXls(request, tBMerchantLittleTag, TBMerchantLittleTag.class, "商家的小标签表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("pengyipeng:t_b_merchant_little_tag:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TBMerchantLittleTag.class);
    }

}
