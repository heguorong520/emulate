
package com.emulate.backend.controller;

import com.emulate.backend.dto.QueryLogDTO;
import com.emulate.backend.entity.BackendLogEntity;
import com.emulate.backend.service.BackendLogService;

import com.emulate.core.result.ResultBody;
import com.emulate.core.util.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 * 系统日志
 * @author Mark sunlightcs@gmail.com
 */
@Controller
@RequestMapping
public class LogController {
	@Autowired
	private BackendLogService sysLogService;

	@ResponseBody
	@GetMapping("/log/list")
	public ResultBody<BackendLogEntity> list(@ModelAttribute QueryLogDTO queryLogDTO){
		PageData<BackendLogEntity> page = sysLogService.queryPage(queryLogDTO);
		return ResultBody.ok(page);
	}

}
