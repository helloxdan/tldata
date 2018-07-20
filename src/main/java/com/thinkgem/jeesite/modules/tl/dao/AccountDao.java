/**
 * Copyright &copy; 2017-2020 <a href="https://www.zakm.com">zakm</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.dao;

import java.util.List;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.tl.entity.Account;

/**
 * 登录账号DAO接口
 * 
 * @author admin
 * @version 2018-06-02
 */
@MyBatisDao
public interface AccountDao extends CrudDao<Account> {

	void resetAccountStatus();

	void updateUsernum();

	void updateGroupnum();

	List<Account> findUnfullUserAccount(Account account);

	List<Account> findAccountForJob(Account account);

	Account findAccountInHis(Account account);

	void insertAccountHis(Account account);

	void updateAccountHis(Account account);

	void updatePwdLock(Account account);
}