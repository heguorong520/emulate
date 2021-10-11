/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.emulate.backend.controller;

import cn.hutool.core.util.ObjectUtil;
import com.emulate.backend.dto.BackendMenuNavDTO;
import com.emulate.backend.emums.MenuTypeEnum;
import com.emulate.backend.entity.BackendMenuEntity;
import com.emulate.backend.service.BackendMenuService;
import com.emulate.core.controller.BaseController;
import com.emulate.core.enums.GlobalErrorEnum;
import com.emulate.core.excetion.CustomizeException;
import com.emulate.core.result.ResultBody;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 系统菜单
 */
@RestController
public class MenuController extends BaseController {
    @Autowired
    private BackendMenuService backendMenuService;

    @GetMapping("menu/nav")
    public ResultBody<BackendMenuNavDTO> nav() {
        List<BackendMenuNavDTO> menuList = backendMenuService.findUserMenuList(getBackendUserId());
        return ResultBody.ok(menuList);
    }

    @GetMapping("menu/list")
    public ResultBody<List<BackendMenuEntity>> list() {
        List<BackendMenuEntity> menuList = backendMenuService.list(null);
        return ResultBody.ok(menuList);
    }

    @GetMapping("menu/role/select")
    public ResultBody<List<BackendMenuNavDTO>> roleSelect() {
        List<BackendMenuNavDTO> menuList = backendMenuService.findUserMenuList(getBackendUserId());
        return ResultBody.ok(menuList);
    }

    /**
     * 选择菜单(添加、修改菜单)
     */
    @GetMapping("menu/select")
    public ResultBody<List<BackendMenuNavDTO>> select() {
        //查询列表数据
        List<BackendMenuNavDTO> menuList = backendMenuService.findNotButtonList();
        //添加顶级菜单
        BackendMenuNavDTO root = new BackendMenuNavDTO();
        root.setMenuId(0L);
        root.setName("目录上级");
        root.setParentId(0L);
        root.setChild(menuList);
        List<BackendMenuNavDTO> result = new ArrayList<>();
        result.add(root);
        return ResultBody.ok(result);
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

        backendMenuService.saveOrUpdate(menu);

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
    public ResultBody<?> delete(Long id) {
        if (id <= 31) {
            return ResultBody.error(GlobalErrorEnum.不能删除);
        }
        //判断是否有子菜单或按钮
        List<BackendMenuEntity> menuList = backendMenuService.findListParentId(id);
        if (menuList.size() > 0) {
            return ResultBody.error(GlobalErrorEnum.存在子节点);
        }
        backendMenuService.delete(id);
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
