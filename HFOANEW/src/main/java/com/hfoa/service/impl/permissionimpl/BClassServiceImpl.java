package com.hfoa.service.impl.permissionimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfoa.dao.permission.BClassMapper;
import com.hfoa.service.permission.BClassService;

/**
 * 
 * @author wzx
 * 权限类型service实现类
 */
@Service
public class BClassServiceImpl implements BClassService{

	@Autowired
	private BClassMapper bClassMapper;
}
