

package com.emulate.backend.service;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emulate.backend.dao.BackendUserDao;
import com.emulate.backend.dto.BackendLoginDTO;
import com.emulate.backend.dto.BackendLoginResultDTO;
import com.emulate.backend.dto.BackendUserDTO;
import com.emulate.backend.dto.QueryUserDTO;
import com.emulate.backend.entity.BackendUserEntity;
import com.emulate.core.enums.BaseErrorEnum;
import com.emulate.core.enums.RedisCacheKeyEnum;
import com.emulate.core.excetion.CustomizeException;
import com.emulate.core.filter.AuthFilter;
import com.emulate.core.jwt.TokenUtil;
import com.emulate.core.user.LoginUserDTO;
import com.emulate.core.util.AESUtil;
import com.emulate.core.util.PageData;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
@Transactional(readOnly = true)
public class BackendUserService extends ServiceImpl<BackendUserDao, BackendUserEntity> {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private BackendUserRoleService backendUserRoleService;

    public List<Long> queryAllMenuId(Long userId) {
        return baseMapper.queryAllMenuId(userId);
    }

    public PageData<BackendUserEntity> queryPage(QueryUserDTO userBodyDTO) {
        IPage<BackendUserEntity> page = this.page(
                new Page<>(userBodyDTO.getPage(), userBodyDTO.getPageSize()),
                new QueryWrapper<BackendUserEntity>()
                        .like(StringUtils.isNotBlank(userBodyDTO.getUserName()), "username", userBodyDTO.getUserName())
        );
        return new PageData(page);
    }


    @Transactional(rollbackFor = Exception.class)
    public void saveUser(@Valid BackendUserDTO userDTO) {
        BackendUserEntity user = Convert.convert(BackendUserEntity.class, userDTO);
        user.setPassword(AESUtil.encrypt(user.getPassword(), AESUtil.PASSWORD_KEY));
        this.save(user);
        //保存用户与角色关系
        backendUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
    }


    @Transactional(rollbackFor = Exception.class)
    public void update(BackendUserDTO user) throws Exception {
        if (ObjectUtil.isEmpty(user.getPassword())) {
            user.setPassword(null);
        } else {
            user.setPassword(AESUtil.encrypt(user.getPassword(), AESUtil.PASSWORD_KEY));
        }
        BackendUserEntity backendUserEntity = Convert.convert(BackendUserEntity.class, user);
        this.updateById(backendUserEntity);
        //保存用户与角色关系
        backendUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
    }


    @Transactional
    public boolean updatePassword(Long userId, String password, String newPassword) throws Exception {
        BackendUserEntity userEntity = new BackendUserEntity();
        userEntity.setPassword(AESUtil.encrypt(newPassword,AESUtil.PASSWORD_KEY));
        String aesPwd = AESUtil.encrypt(password, AESUtil.PASSWORD_KEY);
        BackendUserEntity backendUserEntity = this.baseMapper.selectById(userId);
        if (!backendUserEntity.getPassword().equals(aesPwd)) {
            //旧密码校验通过
            throw new CustomizeException(BaseErrorEnum.旧密码错误);
        }
        return this.update(userEntity,
                new QueryWrapper<BackendUserEntity>().eq("user_id", userId).eq("password", aesPwd));
    }

    /**
     * 登录
     * @param backendLoginDTO
     * @throws Exception
     */
    public BackendLoginResultDTO login(@Valid BackendLoginDTO backendLoginDTO) throws Exception {
        QueryWrapper<BackendUserEntity> userEntityQueryWrapper = new QueryWrapper<>();
        userEntityQueryWrapper.eq("username", backendLoginDTO.getUsername());
        BackendUserEntity backendUserEntity = this.baseMapper.selectOne(userEntityQueryWrapper);
        if (backendUserEntity == null) {
            throw new CustomizeException(BaseErrorEnum.用户不存在);
        }

        if (!backendUserEntity.getPassword().equals(AESUtil.encrypt(backendLoginDTO.getPassword(), AESUtil.PASSWORD_KEY))) {
            throw new CustomizeException(BaseErrorEnum.密码错误);
        }
        String tokenCacheKey = RedisCacheKeyEnum.后台TOKEN.getCacheKey() + backendUserEntity.getUserId();
        String token = (String) redisTemplate.opsForValue().get(tokenCacheKey);

        if (token == null) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", backendUserEntity.getUsername());
            userInfo.put("userId", backendUserEntity.getUserId());
            token = TokenUtil.createTokenBackend(JSONObject.toJSONString(userInfo));
            //保存TOKEN在缓存中做登录权限进行对比
            redisTemplate.opsForValue().set(tokenCacheKey, token, RedisCacheKeyEnum.后台TOKEN.getCacheTime(), TimeUnit.SECONDS);
        }

        BackendLoginResultDTO resultDTO = new BackendLoginResultDTO();
        resultDTO.setUserId(backendUserEntity.getUserId());
        resultDTO.setUsername(backendUserEntity.getUsername());
        resultDTO.setToken(token);
        return resultDTO;
    }

    /**
     * 注销
     */
    public void logout() {
        LoginUserDTO userDTO = AuthFilter.backendLoginUserDTO();
        if(userDTO == null) {
            throw new CustomizeException(BaseErrorEnum.未登录);
        }
        //清理缓存中的TOKEN即可
        String tokenCacheKey = RedisCacheKeyEnum.后台TOKEN.getCacheKey() + userDTO.getUserId();
        redisTemplate.delete(tokenCacheKey);
    }
}
