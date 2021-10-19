
package com.emulate.backend.controller;

import com.emulate.backend.dto.BackendRoleDTO;
import com.emulate.backend.dto.QueryRoleDTO;
import com.emulate.backend.entity.BackendRoleEntity;
import com.emulate.backend.service.BackendRoleMenuService;
import com.emulate.backend.service.BackendRoleService;
import com.emulate.core.controller.BaseApiController;
import com.emulate.core.result.ResultBody;
import com.emulate.database.page.PageData;
import com.emulate.permissions.annotation.Permissions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "角色管理")
@RestController
public class RoleController extends BaseApiController {

    @Autowired
    private BackendRoleService backendRoleService;

    @Permissions(perms = "role:list")
    @ApiOperation("角色列表")
    @GetMapping("role/list")
    public ResultBody<?> list(@ModelAttribute QueryRoleDTO queryRoleDTO) {
        PageData page = backendRoleService.findPage(queryRoleDTO);
        return ResultBody.ok(page);
    }

    @Permissions(perms = "role:select")
    @ApiOperation("用户分配角色")
    @GetMapping("role/select")
    public ResultBody<?> select() {
        List<BackendRoleEntity> list = backendRoleService.list();
        return ResultBody.ok(list);
    }

    @Permissions(perms = "role:save")
    @ApiOperation("保存角色")
    @PostMapping("role/save")
    public ResultBody<?> save(@Valid @RequestBody BackendRoleDTO role) {
        backendRoleService.saveRole(role);
        return ResultBody.ok();
    }

    @Permissions(perms = "role:delete")
    @ApiOperation("删除角色")
    @PostMapping("role/delete")
    public ResultBody delete(@RequestBody Long[] roleIds) {
        backendRoleService.deleteBatch(roleIds);

        return ResultBody.ok();
    }
}
