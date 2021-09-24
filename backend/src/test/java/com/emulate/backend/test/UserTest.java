package com.emulate.backend.test;

import com.alibaba.fastjson.JSONObject;
import com.emulate.backend.BackendApplication;
import com.emulate.backend.dto.BackendLoginDTO;
import com.emulate.backend.dto.BackendLoginResultDTO;
import com.emulate.backend.dto.BackendUserDTO;
import com.emulate.backend.dto.QueryUserDTO;
import com.emulate.backend.entity.BackendUserEntity;
import com.emulate.backend.service.BackendUserService;
import com.emulate.core.util.PageData;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BackendApplication.class)
@Slf4j
public class UserTest {

    @Autowired
    private BackendUserService  userService;

    @Test
    public void addUser() throws Exception {
        BackendUserDTO userDTO = new BackendUserDTO();
        userDTO.setPassword("admin");
        userDTO.setMobile("1388888888");
        userDTO.setUsername("admin");
        userService.saveUser(userDTO);
    }

    @Test
    public void queryPage(){
        QueryUserDTO queryUserBodyDTO = new QueryUserDTO();
        queryUserBodyDTO.setPage(1);
        queryUserBodyDTO.setLimit(10);
        PageData<BackendUserEntity> result = userService.queryPage(queryUserBodyDTO);
        log.info("查询结果{}", JSONObject.toJSONString(result));
        TestCase.assertNotNull(result);
    }


    @Test
    public void updateUser() throws Exception {
        BackendUserDTO userDTO = new BackendUserDTO();
        userDTO.setPassword("123456");
        userDTO.setMobile("1388888888");
        userDTO.setUsername("99967");
        userDTO.setUserId(1L);
        userService.update(userDTO);
    }

    @Test
    public void login() throws Exception {
        BackendLoginDTO userDTO = new BackendLoginDTO();
        userDTO.setUsername("99967");
        userDTO.setPassword("123456");
        userDTO.setCode("1234");
        BackendLoginResultDTO resultDTO = userService.login(userDTO);
        log.info("登录结果:{}",JSONObject.toJSONString(resultDTO));
        TestCase.assertNotNull(resultDTO);
    }

    @Test
    public void logout() {
        userService.logout();
    }


}
