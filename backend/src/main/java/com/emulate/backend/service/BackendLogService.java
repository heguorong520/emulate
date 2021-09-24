 

package com.emulate.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.emulate.backend.dao.BackendLogDao;
import com.emulate.backend.dto.QueryLogDTO;
import com.emulate.backend.entity.BackendLogEntity;
import com.emulate.core.util.PageData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class BackendLogService extends ServiceImpl<BackendLogDao, BackendLogEntity> {

    public PageData<BackendLogEntity> queryPage(QueryLogDTO queryLogDTO) {
        IPage<BackendLogEntity> page = this.page(
            new Page<>(queryLogDTO.getPage(),queryLogDTO.getLimit()),
            new QueryWrapper<BackendLogEntity>()
        );
        return new PageData<>(page);
    }
}
