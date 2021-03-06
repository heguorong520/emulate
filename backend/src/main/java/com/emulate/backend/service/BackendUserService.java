
package com.emulate.backend.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emulate.backend.dao.BackendUserDao;
import com.emulate.backend.dto.*;
import com.emulate.backend.entity.BackendUserEntity;
import com.emulate.core.enums.GlobalErrorEnum;
import com.emulate.core.enums.RedisCacheKeyEnum;
import com.emulate.core.excetion.CustomizeException;
import com.emulate.core.jwt.TokenUtil;
import com.emulate.core.utils.AESUtil;
import com.emulate.database.page.PageData;
import com.emulate.permissions.util.PermissionsUserUtil;
import com.emulate.cache.redis.service.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BackendUserService extends ServiceImpl<BackendUserDao, BackendUserEntity> {

    @Resource
    private RedisService redisService;

    @Resource
    private BackendUserRoleService backendUserRoleService;

    @Resource
    private BackendRoleService backendRoleService;

    public List<Long> queryAllMenuId(Long userId) {
        return baseMapper.queryAllMenuId(userId);
    }

    public PageData<BackendUserEntity> findPage(QueryUserDTO userBodyDTO) {
        IPage<BackendUserEntity> page = this.page(new Page<>(userBodyDTO.getPage(), userBodyDTO.getLimit()),
            new QueryWrapper<BackendUserEntity>()
                .eq(StringUtils.isNotBlank(userBodyDTO.getUsername()), "username", userBodyDTO.getUsername())
                .eq(StringUtils.isNotBlank(userBodyDTO.getNickname()), "nickname", userBodyDTO.getNickname())
                .orderByDesc("create_time"));
        // ????????????
        if (page.getRecords().size() > 0) {
            List<Long> userIds =
                page.getRecords().stream().map(BackendUserEntity::getUserId).collect(Collectors.toList());
            // ????????????????????????ID
            Map<Long, List<Long>> userRoleIdMaps =
                backendUserRoleService.findRoleIdList(userIds.toArray(new Long[userIds.size() - 1]));
            List<Long> roleIds = new ArrayList<>();
            userRoleIdMaps.forEach((k, v) -> {
                roleIds.addAll(v);
            });
            // ????????????????????????
            Map<Long, String> roleNameMap = backendRoleService.findRoleNameMap(roleIds);
            for (BackendUserEntity u : page.getRecords()) {
                if (!userRoleIdMaps.containsKey(u.getUserId())) {
                    continue;
                }
                List<Long> userRoleIds = userRoleIdMaps.get(u.getUserId());
                for (Long roleId : userRoleIds) {
                    if (!roleNameMap.containsKey(roleId)) {
                        continue;
                    }
                    u.getRoleNameList().add(roleNameMap.get(roleId));
                    u.getRoleIdList().add(roleId);
                }
            }
        }
        return new PageData<>(page);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveUser(BackendUserDTO userDTO) {
        BackendUserEntity user = Convert.convert(BackendUserEntity.class, userDTO);
        if (user.getUserId() == null) {
            user.setPassword(AESUtil.encrypt("123456", AESUtil.PASSWORD_KEY));
            user.setSalt(AESUtil.PASSWORD_KEY);
        }
        user.setStatus(0);
        this.saveOrUpdate(user);
        // ???????????????????????????
        backendUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
    }

    @Transactional
    public boolean updatePassword(Long userId, String password, String newPassword) {
        BackendUserEntity userEntity = new BackendUserEntity();
        userEntity.setPassword(AESUtil.encrypt(newPassword, AESUtil.PASSWORD_KEY));
        String aesPwd = AESUtil.encrypt(password, AESUtil.PASSWORD_KEY);
        BackendUserEntity backendUserEntity = this.baseMapper.selectById(userId);
        if (!backendUserEntity.getPassword().equals(aesPwd)) {
            // ?????????????????????
            throw new CustomizeException(GlobalErrorEnum.???????????????);
        }
        return this.update(userEntity,
            new QueryWrapper<BackendUserEntity>().eq("user_id", userId).eq("password", aesPwd));
    }

    /**
     * ??????
     *
     * @param backendLoginDTO
     */
    public BackendLoginResultDTO login(@Valid BackendLoginDTO backendLoginDTO) {
        QueryWrapper<BackendUserEntity> userEntityQueryWrapper = new QueryWrapper<>();
        userEntityQueryWrapper.eq("username", backendLoginDTO.getUsername());
        BackendUserEntity backendUserEntity = this.baseMapper.selectOne(userEntityQueryWrapper);
        if (backendUserEntity == null) {
            throw new CustomizeException(GlobalErrorEnum.???????????????);
        }

        if (!backendUserEntity.getPassword()
            .equals(AESUtil.encrypt(backendLoginDTO.getPassword(), AESUtil.PASSWORD_KEY))) {
            throw new CustomizeException(GlobalErrorEnum.????????????);
        }
        String tokenCacheKey = RedisCacheKeyEnum.BACKEND_TOKEN_KEY.getCacheKey() + backendUserEntity.getUserId();
        String permsCacheKey = RedisCacheKeyEnum.USER_SHIRO_PERMS_KEY.getCacheKey() + backendUserEntity.getUserId();
        String token = (String)redisService.get(tokenCacheKey);

        if (token == null) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", backendUserEntity.getUsername());
            userInfo.put("userId", backendUserEntity.getUserId());
            token = TokenUtil.createTokenBackend(JSONObject.toJSONString(userInfo));
            // ??????TOKEN???????????????????????????????????????
            redisService.set(tokenCacheKey, token, RedisCacheKeyEnum.BACKEND_TOKEN_KEY.getCacheTime());
        }
        BackendLoginResultDTO resultDTO = new BackendLoginResultDTO();
        resultDTO.setUserId(backendUserEntity.getUserId());
        resultDTO.setUsername(backendUserEntity.getUsername());
        resultDTO.setToken(token);
        resultDTO.setPerms(findUserPerms(backendUserEntity.getUserId()));
        redisService.set(permsCacheKey, resultDTO.getPerms(), RedisCacheKeyEnum.USER_SHIRO_PERMS_KEY.getCacheTime());
        return resultDTO;
    }

    /**
     * ??????
     */
    public void logout() {

        if (PermissionsUserUtil.getUserDetail() == null) {
            throw new CustomizeException(GlobalErrorEnum.????????????);
        }
        // ??????????????????TOKEN??????
        String tokenCacheKey = RedisCacheKeyEnum.BACKEND_TOKEN_KEY.getCacheKey() + PermissionsUserUtil.getUserId();
        redisService.del(tokenCacheKey);
    }

    @Transactional
    public void deleteByUserId(Long... id) {
        baseMapper.deleteBatchIds(Arrays.stream(id).collect(Collectors.toList()));
        backendUserRoleService.deleteRoleByUserId(id);
    }

    @Transactional
    public void setUserStatus(BackendUserStatusDTO userStatusDTO) {
        // ??????????????????e
        UpdateWrapper<BackendUserEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", userStatusDTO.getStatus());
        updateWrapper.eq("user_id", userStatusDTO.getUserId());
        baseMapper.update(null, updateWrapper);
    }

    public List<String> findUserPerms(Long userId) {
        List<String> permsList = baseMapper.queryAllPerms(userId);
        List<String> result = new ArrayList<>();
        for (String p : permsList) {
            if (ObjectUtil.isEmpty(p)) {
                continue;
            }
            result.addAll(CollUtil.toList(p.split(",")));
        }
        return result;
    }

}
