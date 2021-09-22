

package com.emulate.backend.controller;


import com.emulate.backend.dto.BackendUpdatePwdDTO;
import com.emulate.backend.dto.BackendUserDTO;
import com.emulate.backend.dto.QueryUserDTO;
import com.emulate.backend.entity.BackendUserEntity;
import com.emulate.backend.service.BackendUserRoleService;
import com.emulate.backend.service.BackendUserService;
import com.emulate.core.controller.BaseController;
import com.emulate.core.excetion.CustomizeException;
import com.emulate.core.filter.AuthFilter;
import com.emulate.core.result.ResultBody;
import com.emulate.core.util.PageData;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * 系统用户
 */
@RestController
@RequestMapping
public class UserController extends BaseController {
    @Autowired
    private BackendUserService backendUserService;
    @Autowired
    private BackendUserRoleService backendUserRoleService;

    /**
     * 所有用户列表
     */
    @GetMapping("user/list")
    public ResultBody<?> list(@ModelAttribute QueryUserDTO queryUserDTO) {
        PageData page = backendUserService.queryPage(queryUserDTO);

        return ResultBody.ok(page);
    }


    /**
     * 修改登录用户密码
     */
    @PostMapping("user/password")
    public ResultBody<?> password(@RequestBody @Valid BackendUpdatePwdDTO backendUpdatePwdDTO) throws Exception {
        //更新密码
        boolean flag = backendUserService.updatePassword(AuthFilter.backendLoginUserDTO().getUserId(), backendUpdatePwdDTO.getPassword(), backendUpdatePwdDTO.getNewPassword());
        if (!flag) {
            throw new CustomizeException("原密码不正确");
        }

        return ResultBody.ok();
    }

    /**
     * 用户信息
     */
    @GetMapping("user/info")
    public ResultBody<?> info(Long userId) {
        BackendUserEntity user = backendUserService.getById(userId);

        //获取用户所属的角色列表
        List<Long> roleIdList = backendUserRoleService.queryRoleIdList(userId);
        user.setRoleIdList(roleIdList);

        return ResultBody.ok(user);
    }

    /**
     * 保存用户
     */
    @PostMapping("user/save")
    public ResultBody<?> save(@RequestBody BackendUserDTO user) throws Exception {

        backendUserService.saveUser(user);

        return ResultBody.ok();
    }

    /**
     * 修改用户
     */
    @PostMapping("user/update")
    public ResultBody<?> update(@RequestBody BackendUserDTO user) throws Exception {

        backendUserService.update(user);

        return ResultBody.ok();
    }

    /**
     * 删除用户
     */
    @PostMapping("user/delete")
    public ResultBody<?> delete(@RequestBody Long[] userIds) {
        if (ArrayUtils.contains(userIds, 1L)) {
            throw new CustomizeException("系统管理员不能删除");
        }

        if (ArrayUtils.contains(userIds, AuthFilter.backendLoginUserDTO().getUserId())) {
            throw new CustomizeException("当前用户不能删除");
        }

        backendUserService.removeByIds(Arrays.asList(userIds));

        return ResultBody.ok();
    }
}
