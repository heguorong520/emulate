 

package com.emulate.backend.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.emulate.backend.dao.BackendMenuDao;
import com.emulate.backend.dto.NavDTO;
import com.emulate.backend.emums.MenuTypeEnum;
import com.emulate.backend.entity.BackendMenuEntity;
import com.emulate.backend.entity.BackendRoleMenuEntity;
import io.prometheus.client.Collector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class BackendMenuService extends ServiceImpl<BackendMenuDao, BackendMenuEntity> {

	@Autowired
	private BackendUserService backendUserService;

	@Autowired
	private BackendRoleMenuService backendRoleMenuService;
	
	public List<BackendMenuEntity> queryListParentId(Long parentId, List<Long> menuIdList) {
		List<BackendMenuEntity> menuList = queryListParentId(parentId);
		if(menuIdList == null){
			return menuList;
		}
		
		List<BackendMenuEntity> userMenuList = new ArrayList<>();
		for(BackendMenuEntity menu : menuList){
			if(menuIdList.contains(menu.getMenuId())){
				userMenuList.add(menu);
			}
		}
		return userMenuList;
	}

	public List<BackendMenuEntity> queryListParentId(Long parentId) {
		return baseMapper.queryListParentId(parentId);
	}

	public List<BackendMenuEntity> queryNotButtonList() {
		return baseMapper.queryNotButtonList();
	}

	public List<NavDTO> getUserMenuList(Long userId) {
		//系统管理员，拥有最高权限
		if(userId == 1){
			return getAllMenuList(null);
		}
		//用户菜单列表
		List<Long> menuIdList = backendUserService.queryAllMenuId(userId);
		return getAllMenuList(menuIdList);
	}

	public void delete(Long menuId){
		//删除菜单
		this.removeById(menuId);
		//删除菜单与角色关联
		backendRoleMenuService.remove(new QueryWrapper<BackendRoleMenuEntity>().eq("menu_id", menuId));
	}

	/**
	 * 获取所有菜单列表
	 */
	private List<NavDTO> getAllMenuList(List<Long> menuIdList){
		QueryWrapper<BackendMenuEntity> queryWrapper = new QueryWrapper<>();
		if(ObjectUtil.isNotEmpty(menuIdList)){
			queryWrapper.in("menuId",menuIdList);
		}
		List<BackendMenuEntity> list = baseMapper.selectList(queryWrapper);
		//查询根菜单列表
		List<BackendMenuEntity> parentList = list.stream().
				filter(e->e.getParentId().intValue() == MenuTypeEnum.目录.getValue()).
				collect(Collectors.toList());


		List<BackendMenuEntity> menuList = list.stream().
				filter(e->e.getParentId().intValue() == MenuTypeEnum.菜单.getValue()).
				collect(Collectors.toList());

		//转换
		List<NavDTO> navList = toNavList(parentList);
		//递归获取子菜单
		menuTreeList(navList, menuList);
		return navList;
	}

	private List<NavDTO> toNavList(List<BackendMenuEntity> menuList){
		List<NavDTO> navList = new ArrayList<>();
		if(ObjectUtil.isEmpty(menuList)){
			return navList;
		}
		for(BackendMenuEntity entity : menuList) {
			NavDTO navDTO = new NavDTO();
			navDTO.setHref(entity.getUrl());
			navDTO.setIcon(entity.getIcon());
			navDTO.setTitle(entity.getName());
			navDTO.setId(entity.getMenuId());
			navList.add(navDTO);
		}
		return navList;
	}

	/**
	 * 递归引用设置子节点的值
	 */
	private void menuTreeList(List<NavDTO> navList,List<BackendMenuEntity> menuList){
		List<NavDTO> subMenuList = new ArrayList<>();
		for(NavDTO navDTO : navList){
			List<BackendMenuEntity> list = menuList.stream().filter(e ->e.getParentId().equals(navDTO.getId())).collect(Collectors.toList());
			navDTO.setChild(toNavList(list));
			if(navDTO.getChild().size()>0){
				menuTreeList(navDTO.getChild(),menuList);
			}
		}
	}
}
