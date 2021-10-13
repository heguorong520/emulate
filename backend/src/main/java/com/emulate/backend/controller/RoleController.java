
package com.emulate.backend.controller;


import com.emulate.backend.dto.BackendRoleDTO;
import com.emulate.backend.dto.QueryRoleDTO;
import com.emulate.backend.entity.BackendRoleEntity;
import com.emulate.backend.service.BackendRoleMenuService;
import com.emulate.backend.service.BackendRoleService;
import com.emulate.core.controller.BaseApiController;
import com.emulate.core.result.ResultBody;
import com.emulate.database.page.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 角色管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
public class RoleController extends BaseApiController {
    @Autowired
    private BackendRoleService backendRoleService;
    @Autowired
    private BackendRoleMenuService backendRoleMenuService;


    /**
     * 角色列表
     *
     * @return
     */
    @GetMapping("role/list")
    public ResultBody<?> list(@ModelAttribute QueryRoleDTO queryRoleDTO) {
        PageData page = backendRoleService.findPage(queryRoleDTO);
        return ResultBody.ok(page);
    }

    /**
     * 角色列表
     */
    @GetMapping("role/select")
    public ResultBody<?> select() {
        List<BackendRoleEntity> list = backendRoleService.list();
        return ResultBody.ok(list);
    }

    /**
     * 角色信息
     */
    @GetMapping("role/info")
    public ResultBody<?> info(Long roleId) {
        BackendRoleEntity role = backendRoleService.getById(roleId);
        //查询角色对应的菜单
        List<Long> menuIdList = backendRoleMenuService.findMenuIdList(roleId);
        role.setMenuIdList(menuIdList);
        return ResultBody.ok(role);
    }

    /**
     * 保存角色
     */
    @PostMapping("role/save")
    public ResultBody<?> save(@Valid @RequestBody BackendRoleDTO role) {

        backendRoleService.saveRole(role);

        return ResultBody.ok();
    }


    /**
     * 删除角色
     */
    @PostMapping("role/delete")
    public ResultBody delete(@RequestBody Long[] roleIds) {
        backendRoleService.deleteBatch(roleIds);

        return ResultBody.ok();
    }
}
