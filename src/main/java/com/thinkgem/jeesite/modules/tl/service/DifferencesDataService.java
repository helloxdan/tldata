/**
 * Copyright &copy; 2017-2020 <a href="https://www.gzruimin.com">gzruimin</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.tl.entity.DifferencesData;
import com.thinkgem.jeesite.modules.tl.dao.DifferencesDataDao;

/**
 * 差异数据Service
 * 
 * @author admin
 * @version 2018-06-02
 */
@Service
@Transactional(readOnly = true)
public class DifferencesDataService extends
		CrudService<DifferencesDataDao, DifferencesData> {

	public DifferencesData get(String id) {
		return super.get(id);
	}

	public List<DifferencesData> findList(DifferencesData differencesData) {
		return super.findList(differencesData);
	}

	public Page<DifferencesData> findPage(Page<DifferencesData> page,
			DifferencesData differencesData) {
		return super.findPage(page, differencesData);
	}

	@Transactional(readOnly = false)
	public void save(DifferencesData differencesData) {
		super.save(differencesData);
	}

	@Transactional(readOnly = false)
	public void delete(DifferencesData differencesData) {
		super.delete(differencesData);
	}

	@Transactional(readOnly = false)
	public void del(String ids) {
		if (StringUtils.isNoneBlank(ids)) {
			String[] delIds = ids.split(",");
			for (String id : delIds) {
				DifferencesData differencesData = new DifferencesData(id);
				super.delete(differencesData);
			}
		}
		// 删除缓存
		// IcareUtils.removeCache();
	}

	public @NotNull Map<Integer, int[]> getDifferencesData(String phone) {
		final HashMap<Integer, int[]> differencesDatas = new HashMap<>();

		DifferencesData diff = new DifferencesData();
		diff.setAccount(phone);
		List<DifferencesData> list = findList(diff);
		for (DifferencesData dd : list) {
			final int[] differencesData = new int[3];
			differencesData[0] = dd.getPts();
			differencesData[1] = dd.getDate();
			differencesData[2] = dd.getSeq();
			differencesDatas.put(dd.getBotid(), differencesData);
		}

		return differencesDatas;
	}
}