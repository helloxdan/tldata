/**
 * Copyright &copy; 2017-2020 <a href="https://www.gzruimin.com">gzruimin</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.tl.entity.Account;
import com.thinkgem.jeesite.modules.tl.vo.RequestData;
import com.thinkgem.jeesite.modules.tl.dao.AccountDao;

/**
 * 登录账号Service
 * 
 * @author admin
 * @version 2018-06-02
 */
@Service
@Transactional(readOnly = true)
public class AccountService extends CrudService<AccountDao, Account> {

	public Account get(String id) {
		return super.get(id);
	}

	public List<Account> findList(Account account) {
		return super.findList(account);
	}

	public Page<Account> findPage(Page<Account> page, Account account) {
		return super.findPage(page, account);
	}

	@Transactional(readOnly = false)
	public void save(Account account) {
		super.save(account);
	}

	@Transactional(readOnly = false)
	public void delete(Account account) {
		super.delete(account);
	}

	@Transactional(readOnly = false)
	public void del(String ids) {
		if (StringUtils.isNoneBlank(ids)) {
			String[] delIds = ids.split(",");
			for (String id : delIds) {
				Account account = new Account(id);
				super.delete(account);
			}
		}
		// 删除缓存
		// IcareUtils.removeCache();
	}

	/**
	 * 批量新增账号
	 * 
	 * @param data
	 * @return
	 */
	@Transactional(readOnly = false)
	public String addBatch(RequestData data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional(readOnly = false)
	public void resetAccountStatus() {
		logger.info("恢复账号状态");
		this.dao.resetAccountStatus();
	}

	/**
	 * 计算账号关联的群组数和用户数数。
	 */
	@Transactional(readOnly = false)
	public void updateAccountData() {
		this.dao.updateUsernum();
		this.dao.updateGroupnum();
	}

	public List<Account> findUnfullUserAccount(Account account) {
		return this.dao.findUnfullUserAccount(account);
	}

	public List<Account> findAccountForJob(Account account) {
		return this.dao.findAccountForJob(account);
	}

	public Account findAccountInHis(String phone) {
		Account account = new Account();
		account.setId(phone);
		return this.dao.findAccountInHis(account);
	}

	@Transactional(readOnly = false)
	public void insertAccountHis(Account account) {
		this.dao.insertAccountHis(account);
	}

	@Transactional(readOnly = false)
	public void updateAccountHis(Account account) {
		this.dao.updateAccountHis(account);
	}
}