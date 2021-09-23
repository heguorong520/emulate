/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.emulate.backend.controller;

import com.emulate.backend.dto.NavDTO;
import com.emulate.backend.emums.MenuTypeEnum;
import com.emulate.backend.entity.BackendMenuEntity;
import com.emulate.backend.service.BackendMenuService;
import com.emulate.core.controller.BaseController;
import com.emulate.core.enums.GlobalErrorEnum;
import com.emulate.core.excetion.CustomizeException;
import com.emulate.core.filter.AuthFilter;
import com.emulate.core.result.ResultBody;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * 系统菜单
 */
@RestController
public class MenuController extends BaseController {
    @Autowired
    private BackendMenuService backendMenuService;

    /**
     * 导航菜单
     */
    @GetMapping("menu/nav")
    public ResultBody<NavDTO> nav() {
        Long userId = AuthFilter.backendLoginUserDTO().getUserId();
        List<NavDTO> menuList = backendMenuService.getUserMenuList(userId);
        return ResultBody.ok(menuList);
    }

    /**
     * 所有菜单列表
     */
    @GetMapping("menu/list")
    public ResultBody<List<BackendMenuEntity>> list() {
        List<BackendMenuEntity> menuList = backendMenuService.list();
        for (BackendMenuEntity sysMenuEntity : menuList) {
            BackendMenuEntity parentMenuEntity = backendMenuService.getById(sysMenuEntity.getParentId());
            if (parentMenuEntity != null) {
                sysMenuEntity.setParentName(parentMenuEntity.getName());
            }
        }

        return ResultBody.ok(menuList);
    }

    /**
     * 选择菜单(添加、修改菜单)
     */
    @GetMapping("menu/select")
    public ResultBody<List<BackendMenuEntity>> select() {
        //查询列表数据
        List<BackendMenuEntity> menuList = backendMenuService.queryNotButtonList();

        //添加顶级菜单
        BackendMenuEntity root = new BackendMenuEntity();
        root.setMenuId(0L);
        root.setName("一级菜单");
        root.setParentId(-1L);
        root.setOpen(true);
        menuList.add(root);

        return ResultBody.ok(menuList);
    }

    /**
     * 菜单信息
     */
    @GetMapping("menu/info")
    public ResultBody<BackendMenuEntity> info(Long menuId) {
		BackendMenuEntity menu = backendMenuService.getById(menuId);
        return ResultBody.ok(menu);
    }

    /**
     * 保存
     */
    @PostMapping("menu/save")
    public ResultBody<?> save(@Valid  @RequestBody BackendMenuEntity menu) {
        verifyForm(menu);

        backendMenuService.save(menu);

        return ResultBody.ok();
    }

    /**
     * 修改
     */
    @PostMapping("menu/update")
    public ResultBody<?> update(@Valid @RequestBody BackendMenuEntity menu) {
        verifyForm(menu);

        backendMenuService.updateById(menu);

        return ResultBody.ok();
    }

    /**
     * 删除
     */
    @GetMapping("menu/delete")
    public ResultBody<?> delete(long menuId) {
        if (menuId <= 31) {
            return ResultBody.error(GlobalErrorEnum.不能删除);
        }

        //判断是否有子菜单或按钮
        List<BackendMenuEntity> menuList = backendMenuService.queryListParentId(menuId);
        if (menuList.size() > 0) {
            return ResultBody.error(GlobalErrorEnum.存在子节点);
        }

        backendMenuService.delete(menuId);

        return ResultBody.ok();
    }

    /**
     * 验证参数是否正确
     */
    private void verifyForm(BackendMenuEntity menu) {
        if (StringUtils.isBlank(menu.getName())) {
            throw new CustomizeException("菜单名称不能为空");
        }

        if (menu.getParentId() == null) {
            throw new CustomizeException("上级菜单不能为空");
        }

        //菜单
        if (Objects.equals(menu.getType(), MenuTypeEnum.菜单.getValue())) {
            if (StringUtils.isBlank(menu.getUrl())) {
                throw new CustomizeException("菜单URL不能为空");
            }
        }

        //上级菜单类型
        int parentType = MenuTypeEnum.目录.getValue();
        if (menu.getParentId() != 0) {
            BackendMenuEntity parentMenu = backendMenuService.getById(menu.getParentId());
            parentType = parentMenu.getType();
        }

        //目录、菜单
        if (menu.getType() == MenuTypeEnum.目录.getValue() ||
                menu.getType() == MenuTypeEnum.菜单.getValue()) {
            if (parentType != MenuTypeEnum.目录.getValue()) {
                throw new CustomizeException("上级菜单只能为目录类型");
            }
            return;
        }

    }
}
