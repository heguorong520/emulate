 

package com.emulate.backend.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.emulate.backend.dao.BackendMenuDao;
import com.emulate.backend.dto.BackendMenuNavDTO;
import com.emulate.backend.emums.MenuTypeEnum;
import com.emulate.backend.entity.BackendMenuEntity;
import com.emulate.backend.entity.BackendRoleMenuEntity;
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
	


	public List<BackendMenuEntity> findListParentId(Long parentId) {
		return baseMapper.queryListParentId(parentId);
	}

	public List<BackendMenuEntity> findNotButtonList() {
		return baseMapper.queryNotButtonList();
	}

	public List<BackendMenuNavDTO> findUserMenuList(Long userId) {
		//系统管理员，拥有最高权限
		if(userId == 1){
			return findMenuNav(null);
		}
		//用户菜单列表
		List<Long> menuIdList = backendUserService.queryAllMenuId(userId);
		return findMenuNav(menuIdList);
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
	private List<BackendMenuNavDTO> findMenuNav(List<Long> menuIdList){
		QueryWrapper<BackendMenuEntity> queryWrapper = new QueryWrapper<>();
		if(ObjectUtil.isNotEmpty(menuIdList)){
			queryWrapper.in("menuId",menuIdList);
		}
		List<BackendMenuEntity> list = baseMapper.selectList(queryWrapper);
		return menuDeal(list);
	}

	private List<BackendMenuNavDTO> toNavList(List<BackendMenuEntity> menuList){
		List<BackendMenuNavDTO> navList = new ArrayList<>();
		if(ObjectUtil.isEmpty(menuList)){
			return navList;
		}
		for(BackendMenuEntity entity : menuList) {
			BackendMenuNavDTO navDTO = BeanUtil.toBean(entity,BackendMenuNavDTO.class);
			navDTO.setHref(entity.getUrl());
			navDTO.setTitle(entity.getName());
			navList.add(navDTO);
		}
		return navList;
	}

	/**
	 * 递归引用设置子节点的值
	 */
	private void menuTreeList(List<BackendMenuNavDTO> navList, List<BackendMenuEntity> menuList){
		for(BackendMenuNavDTO navDTO : navList){
			List<BackendMenuEntity> list = menuList.stream().
					filter(e ->e.getParentId().equals(navDTO.getMenuId())).
					collect(Collectors.toList());
			//设置父菜单名字
			list.forEach(e->e.setParentName(navDTO.getName()));
			//设置子菜单
			navDTO.setChild(toNavList(list));
			if(navDTO.getChild().size()>0){
				menuTreeList(navDTO.getChild(),menuList);
			}
		}
	}

	/**
	 * 处理菜单数据
	 * @param list
	 * @return
	 */
	private List<BackendMenuNavDTO> menuDeal(List<BackendMenuEntity>  list){
		//查询根菜单列表
		List<BackendMenuEntity> parentList = list.stream().
				filter(e->e.getParentId().intValue() == MenuTypeEnum.目录.getValue()).
				collect(Collectors.toList());


		List<BackendMenuEntity> menuList = list.stream().
				filter(e->e.getType().intValue() == MenuTypeEnum.菜单.getValue()).
				collect(Collectors.toList());

		//转换
		List<BackendMenuNavDTO> navList = toNavList(parentList);
		//递归获取子菜单
		menuTreeList(navList, menuList);

		return  navList;
	}

}
