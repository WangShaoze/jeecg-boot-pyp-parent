package org.jeecg.modules.pengyipeng.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.pengyipeng.dto.MerchantServiceInfoDTO;
import org.jeecg.modules.pengyipeng.entity.TBMerchants;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 店铺表
 * @Author: jeecg-boot
 * @Date: 2025-08-12
 * @Version: V1.0
 */

@Mapper
public interface TBMerchantsMapper extends BaseMapper<TBMerchants> {
    @Select("""
            SELECT
              m.merchant_name AS merchantName,
              m.contact_person AS contactPerson,
              m.contact_phone AS contactPhone,
              l.status AS licenseStatus,
              l.end_date AS serviceEndDate
            FROM t_b_merchants m
              LEFT JOIN t_b_agent a ON a.id = m.agent_id
              LEFT JOIN t_b_licenses l ON l.agent_id = a.id AND m.id = l.merchant_id
            WHERE a.sys_uid = #{agentSysUid} and 
                  l.`status` = #{licenseStatus}
            ORDER BY m.create_time  DESC;
            """)
    IPage<MerchantServiceInfoDTO> getMerchantServiceInfo(
            Page<MerchantServiceInfoDTO> page,
            @Param("agentSysUid") String agentSysUid,
            @Param("licenseStatus") Integer licenseStatus,
            @Param("column") String column,
            @Param("order") String order);


    @Select("select m.* from t_b_merchants m left join  t_b_licenses l on l.merchant_id = m.id  where l.license_key = #{merchantLicense}")
    TBMerchants selectByLicenseKey(@Param("merchantLicense") String merchantLicense);

    // 自定义插入语句
    @Insert("INSERT INTO sys_user (id, username, realname, work_no, user_identity, email, phone, activiti_sync, `password`, salt, depart_ids) value (#{id},     #{username},     #{realname},     #{workNo},     #{userIdentity},     #{email},     #{phone},     #{activitiSync},     #{password},     #{salt},     #{departIds})")
    int saveSysUser(
            @Param("id") String id,
            @Param("username") String username,
            @Param("realname") String realname,
            @Param("workNo") String workNo,
            @Param("userIdentity") Integer userIdentity,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("activitiSync") Integer activitiSync,
            @Param("password") String password,
            @Param("salt") String salt,
            @Param("departIds") String departIds);

    // 通过 手机号获取 系统用户
    @Select("select id from sys_user where phone=#{phone}")
    String selectSysUserByPhone(@Param("phone") String phone);


    // 插入数据到 sys_user_role 用户角色表
    @Insert("INSERT INTO sys_user_role (id, user_id, role_id, tenant_id) value (#{id}, #{userId}, #{roleId}, 0);")
    int insertSysUserRole(@Param("id") String id, @Param("userId") String userId, @Param("roleId") String roleId);

}
