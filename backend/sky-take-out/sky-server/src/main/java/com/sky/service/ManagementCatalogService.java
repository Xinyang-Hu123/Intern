package com.sky.service;

import com.sky.dto.ManagementPageQueryDTO;
import com.sky.entity.*;
import com.sky.result.PageResult;

import java.util.List;

public interface ManagementCatalogService {

    PageResult pageMarketing(ManagementPageQueryDTO queryDTO);

    MarketingActivity saveMarketing(MarketingActivity activity);

    void deleteMarketing(Long id);

    void setMarketingStatus(Long id, Integer status);

    PageResult pageCoupons(ManagementPageQueryDTO queryDTO);

    CouponActivity saveCoupon(CouponActivity coupon);

    void deleteCoupon(Long id);

    void setCouponStatus(Long id, Integer status);

    PageResult pageUsers(ManagementPageQueryDTO queryDTO);

    SystemUser saveUser(SystemUser user);

    void deleteUser(Long id);

    void setUserStatus(Long id, Integer status);

    void assignUserRoles(Long id, List<String> roles);

    PageResult pageRoles(ManagementPageQueryDTO queryDTO);

    SystemRole saveRole(SystemRole role);

    void deleteRole(Long id);

    void setRoleStatus(Long id, Integer status);

    void assignRoleMenus(Long id, List<Long> menuIds);

    PageResult pageMenus(ManagementPageQueryDTO queryDTO);

    SystemMenu saveMenu(SystemMenu menu);

    void deleteMenu(Long id);

    void setMenuStatus(Long id, Integer status);
}
