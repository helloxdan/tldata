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
import com.thinkgem.jeesite.modules.tl.entity.Group;
import com.thinkgem.jeesite.modules.tl.dao.GroupDao;

/**
 * 群组Service
 * 
 * @author admin
 * @version 2018-06-02
 */
@Service
@Transactional(readOnly = true)
public class GroupService extends CrudService<GroupDao, Group> {

	public Group get(String id) {
		return super.get(id);
	}

	public List<Group> findList(Group group) {
		return super.findList(group);
	}

	public Page<Group> findPage(Page<Group> page, Group group) {
		return super.findPage(page, group);
	}

	@Transactional(readOnly = false)
	public void save(Group group) {
		super.save(group);
	}

	@Transactional(readOnly = false)
	public void delete(Group group) {
		super.delete(group);
	}

	@Transactional(readOnly = false)
	public void del(String ids) {
		if (StringUtils.isNoneBlank(ids)) {
			String[] delIds = ids.split(",");
			for (String id : delIds) {
				Group group = new Group(id);
				super.delete(group);
			}
		}
		// 删除缓存
		// IcareUtils.removeCache();
	}

	@Transactional(readOnly = false)
	public void updateOffset(Group group) {
		group.preUpdate();
		this.dao.updateOffset(group);
	}

	/**
	 * @param group
	 */
	@Transactional(readOnly = false)
	public void insertOrUpdate(Group group) {
		Group g = get(group.getId());
		if (g == null) {
			logger.info("新增群组，{}，{}，{}", group.getId(), group.getName(),
					group.getIsChannel());
			group.setIsNewRecord(true);
			save(group);
		} else {
			group.preUpdate();
			this.dao.updateUpdateNum(group);
			// logger.info("更新群组，{}，{}，{}", group.getId(), group.getName(),
			// group.getIsChannel());
			// group.setIsNewRecord(false);
		}
	}

	/**查找没有usernum 值录的群组，用于更新。
	 * @param group
	 * @return
	 */
	public List<Group> findListWithoutUsernum(Group group) {
		return this.dao.findListWithoutUsernum(group);
	}
}