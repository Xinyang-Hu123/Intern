package com.sky.controller.admin;

import com.sky.dto.ManagementPageQueryDTO;
import com.sky.entity.SystemMenu;
import com.sky.entity.SystemRole;
import com.sky.entity.SystemUser;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.ManagementCatalogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/system")
@Api(tags = "系统管理接口")
public class SystemManagementController {

    @Autowired
    private ManagementCatalogService managementCatalogService;

    @GetMapping("/user/page")
    @ApiOperation("系统用户分页查询")
    public Result<PageResult> pageUsers(ManagementPageQueryDTO queryDTO) {
        return Result.success(managementCatalogService.pageUsers(queryDTO));
    }

    @PostMapping("/user")
    public Result<SystemUser> saveUser(@RequestBody SystemUser user) {
        return Result.success(managementCatalogService.saveUser(user));
    }

    @PutMapping("/user")
    public Result<SystemUser> updateUser(@RequestBody SystemUser user) {
        return Result.success(managementCatalogService.saveUser(user));
    }

    @DeleteMapping("/user/{id}")
    public Result deleteUser(@PathVariable Long id) {
        managementCatalogService.deleteUser(id);
        return Result.success();
    }

    @PostMapping("/user/status/{status}")
    public Result setUserStatus(@PathVariable Integer status, Long id) {
        managementCatalogService.setUserStatus(id, status);
        return Result.success();
    }

    @PostMapping("/user/{id}/roles")
    public Result assignUserRoles(@PathVariable Long id, @RequestBody List<String> roles) {
        managementCatalogService.assignUserRoles(id, roles);
        return Result.success();
    }

    @GetMapping("/role/page")
    public Result<PageResult> pageRoles(ManagementPageQueryDTO queryDTO) {
        return Result.success(managementCatalogService.pageRoles(queryDTO));
    }

    @PostMapping("/role")
    public Result<SystemRole> saveRole(@RequestBody SystemRole role) {
        return Result.success(managementCatalogService.saveRole(role));
    }

    @PutMapping("/role")
    public Result<SystemRole> updateRole(@RequestBody SystemRole role) {
        return Result.success(managementCatalogService.saveRole(role));
    }

    @DeleteMapping("/role/{id}")
    public Result deleteRole(@PathVariable Long id) {
        managementCatalogService.deleteRole(id);
        return Result.success();
    }

    @PostMapping("/role/status/{status}")
    public Result setRoleStatus(@PathVariable Integer status, Long id) {
        managementCatalogService.setRoleStatus(id, status);
        return Result.success();
    }

    @PostMapping("/role/{id}/menus")
    public Result assignRoleMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        managementCatalogService.assignRoleMenus(id, menuIds);
        return Result.success();
    }

    @GetMapping("/menu/page")
    public Result<PageResult> pageMenus(ManagementPageQueryDTO queryDTO) {
        return Result.success(managementCatalogService.pageMenus(queryDTO));
    }

    @PostMapping("/menu")
    public Result<SystemMenu> saveMenu(@RequestBody SystemMenu menu) {
        return Result.success(managementCatalogService.saveMenu(menu));
    }

    @PutMapping("/menu")
    public Result<SystemMenu> updateMenu(@RequestBody SystemMenu menu) {
        return Result.success(managementCatalogService.saveMenu(menu));
    }

    @DeleteMapping("/menu/{id}")
    public Result deleteMenu(@PathVariable Long id) {
        managementCatalogService.deleteMenu(id);
        return Result.success();
    }

    @PostMapping("/menu/status/{status}")
    public Result setMenuStatus(@PathVariable Integer status, Long id) {
        managementCatalogService.setMenuStatus(id, status);
        return Result.success();
    }
}
