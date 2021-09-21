 

package com.emulate.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.emulate.backend.dao.BackendMenuDao;
import com.emulate.backend.entity.BackendMenuEntity;
import com.emulate.backend.entity.BackendRoleMenuEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


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

	public List<BackendMenuEntity> getUserMenuList(Long userId) {
		/*//系统管理员，拥有最高权限
		if(userId == Constant.SUPER_ADMIN){
			return getAllMenuList(null);
		}*/
		
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
	private List<BackendMenuEntity> getAllMenuList(List<Long> menuIdList){
		//查询根菜单列表
		List<BackendMenuEntity> menuList = queryListParentId(0L, menuIdList);
		//递归获取子菜单
		getMenuTreeList(menuList, menuIdList);
		
		return menuList;
	}

	/**
	 * 递归
	 */
	private List<BackendMenuEntity> getMenuTreeList(List<BackendMenuEntity> menuList, List<Long> menuIdList){
		List<BackendMenuEntity> subMenuList = new ArrayList<BackendMenuEntity>();
		
		for(BackendMenuEntity entity : menuList){
			/*//目录
			if(entity.getType() == Constant.MenuType.CATALOG.getValue()){
				entity.setList(getMenuTreeList(queryListParentId(entity.getMenuId(), menuIdList), menuIdList));
			}*/
			subMenuList.add(entity);
		}
		
		return subMenuList;
	}
}
