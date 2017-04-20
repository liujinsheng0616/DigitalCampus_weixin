package com.cas.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.cas.dao.UserDao;
import com.cas.entity.account.User;

@Repository
public class UserDaoImpl implements UserDao{

	@Resource(name = "weixinSqlSession")
	private SqlSessionTemplate weixinSqlSession;
	
	@Override
	public List<User> getUser() {
		return weixinSqlSession.selectList("UserDao.getUserInfo");
	}

}
