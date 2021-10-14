

package com.emulate.backend.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.emulate.backend.dao.BackendMenuDao;
import com.emulate.backend.dto.BackendMenuDTO;
import com.emulate.backend.emums.MenuTypeEnum;
import com.emulate.backend.entity.BackendMenuEntity;
import com.emulate.backend.entity.BackendRoleMenuEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class BackendMenuService extends ServiceImpl<BackendMenuDao, BackendMenuEntity> {

    @Autowired
    private BackendUserService backendUserService;

    @Autowired
    private BackendRoleMenuService backendRoleMenuService;


    public List<BackendMenuEntity> findListParentId(Long parentId) {
        return baseMapper.queryListParentId(parentId);
    }

    public List<BackendMenuDTO> findNotButtonList() {
        List<BackendMenuEntity> list = baseMapper.queryNotButtonList();
        return menuDeal(list);
    }

    public List<BackendMenuDTO> findUserMenuAndButtonList(Long userId) {
        //系统管理员，拥有最高权限
        if (userId == 1) {
            return findMenu(null, Boolean.TRUE);
        }
        //用户菜单列表
        List<Long> menuIdList = backendUserService.queryAllMenuId(userId);
        return findMenu(menuIdList,Boolean.TRUE);
    }

    public void delete(Long menuId) {
        //删除菜单
        this.removeById(menuId);
        //删除菜单与角色关联
        backendRoleMenuService.remove(new QueryWrapper<BackendRoleMenuEntity>().eq("menu_id", menuId));
    }


    private List<BackendMenuDTO> findMenu(List<Long> menuIdList,Boolean isNotButton) {
        return menuDeal(findByAllOrMenuIds(menuIdList,isNotButton));
    }

    private List<BackendMenuDTO> toNavList(List<BackendMenuEntity> menuList) {
        List<BackendMenuDTO> navList = new ArrayList<>();
        if (ObjectUtil.isEmpty(menuList)) {
            return navList;
        }
        for (BackendMenuEntity entity : menuList) {
            BackendMenuDTO navDTO = BeanUtil.toBean(entity, BackendMenuDTO.class);
            navDTO.setHref(entity.getUrl());
            navDTO.setTitle(entity.getName());
            navList.add(navDTO);
        }
        return navList;
    }

    private void menuTreeList(List<BackendMenuDTO> navList, List<BackendMenuEntity> menuList) {
        for (BackendMenuDTO navDTO : navList) {
            List<BackendMenuEntity> list = menuList.stream().
                    filter(e -> e.getParentId().equals(navDTO.getMenuId())).
                    collect(Collectors.toList());
            //设置父菜单名字
            list.forEach(e -> e.setParentName(navDTO.getName()));
            //设置子菜单
            navDTO.setChild(toNavList(list));
            if (navDTO.getChild().size() > 0) {
                menuTreeList(navDTO.getChild(), menuList);
            }
        }
    }


    private List<BackendMenuDTO> menuDeal(List<BackendMenuEntity> list) {
        //查询根菜单列表
        List<BackendMenuEntity> parentList = list.stream().
                filter(e -> e.getParentId().intValue() == MenuTypeEnum.目录.getValue()).
                collect(Collectors.toList());

        List<BackendMenuEntity> menuList = list.stream().
                filter(e -> e.getType().intValue() == MenuTypeEnum.菜单.getValue() || e.getType().intValue() == MenuTypeEnum.按钮.getValue()).
                collect(Collectors.toList());

        //转换
        List<BackendMenuDTO> navList = toNavList(parentList);
        //递归获取子菜单
        menuTreeList(navList, menuList);

        return navList;
    }

    public Map<Long, String> findMenuNameMap(List<Long> ids) {
        Map<Long, String> result = new HashMap<>();
        if (ObjectUtil.isEmpty(ids)) {
            return result;
        }
        QueryWrapper<BackendMenuEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("menu_id", ids);
        List<BackendMenuEntity> menuList = this.baseMapper.selectList(queryWrapper);
        result = menuList.stream().
                collect(
                        Collectors.
                                toMap(
                                        BackendMenuEntity::getMenuId,
                                        BackendMenuEntity::getName
                                )
                );
        return result;
    }

    public List<BackendMenuEntity> findByAllOrMenuIds(List<Long> menuIdList,boolean isNotButton) {
        QueryWrapper<BackendMenuEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(ObjectUtil.isNotEmpty(menuIdList),"menu_id", menuIdList);
        queryWrapper.in(!isNotButton,"type",0,1);
        return baseMapper.selectList(queryWrapper);
    }

    public List<BackendMenuDTO> findUserMenuNav(Long userId) {
        //系统管理员，拥有最高权限
        if (userId == 1) {
            return findMenu(null, Boolean.FALSE);
        }
        //用户菜单列表
        List<Long> menuIdList = backendUserService.queryAllMenuId(userId);
        return findMenu(menuIdList,Boolean.FALSE);
    }
}
