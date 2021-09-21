
package com.emulate.backend.controller;


import com.emulate.backend.entity.BackendRoleEntity;
import com.emulate.backend.service.BackendRoleMenuService;
import com.emulate.backend.service.BackendRoleService;
import com.emulate.core.result.ResultBody;
import com.emulate.core.util.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 角色管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping
public class RoleController {
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
    public ResultBody<?> list(@RequestParam Map<String, Object> params) {
        PageData page = backendRoleService.queryPage(params);

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
        List<Long> menuIdList = backendRoleMenuService.queryMenuIdList(roleId);
        role.setMenuIdList(menuIdList);


        return ResultBody.ok(role);
    }

    /**
     * 保存角色
     */
    @PostMapping("/save")
    public ResultBody<?> save(@Valid @RequestBody BackendRoleEntity role) {

        backendRoleService.saveRole(role);

        return ResultBody.ok();
    }

    /**
     * 修改角色
     */
    @PostMapping("/update")
    public  ResultBody<?>  update(@RequestBody BackendRoleEntity role) {

        backendRoleService.update(role);

        return ResultBody.ok();
    }

    /**
     * 删除角色
     */
    @PostMapping("/delete")
    public ResultBody delete(@RequestBody Long[] roleIds) {
        backendRoleService.deleteBatch(roleIds);

        return ResultBody.ok();
    }
}
