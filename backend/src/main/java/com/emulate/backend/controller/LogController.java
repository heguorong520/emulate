
package com.emulate.backend.controller;

import com.emulate.backend.dto.QueryLogDTO;
import com.emulate.backend.entity.BackendLogEntity;
import com.emulate.backend.service.BackendLogService;

import com.emulate.core.annotation.BackendShiroValidate;
import com.emulate.core.controller.BaseController;
import com.emulate.core.result.ResultBody;
import com.emulate.core.utils.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 * 系统日志
 * @author Mark sunlightcs@gmail.com
 */
@Controller
public class LogController  extends BaseController {
	@Autowired
	private BackendLogService sysLogService;


	@ResponseBody
	@GetMapping("/log/list")
	@BackendShiroValidate(perms = "list:log")
	public ResultBody<BackendLogEntity> list(@ModelAttribute QueryLogDTO queryLogDTO){
		PageData<BackendLogEntity> page = sysLogService.findPage(queryLogDTO);
		return ResultBody.ok(page);
	}

}
