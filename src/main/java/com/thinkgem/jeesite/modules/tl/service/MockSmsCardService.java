package com.thinkgem.jeesite.modules.tl.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;

public class MockSmsCardService implements SmsCardService {

	@Override
	public List<String> getPhoneList() {
		List<String> list = new ArrayList<String>();
		list.add("1375186" + RandomUtils.nextInt(1000, 9999));
		// 5秒内
		try {
			Thread.sleep(RandomUtils.nextInt(1, 5) * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<String[]> getPhoneCode(String phone) {
		List<String[]> list = new ArrayList<String[]>();
		boolean success = RandomUtils.nextInt(1, 1000) % 2 == 0;
		if (success) {
			String code = "" + RandomUtils.nextInt(10000, 99999);
			list.add(new String[] { phone, code });
		} else {
			try {
				Thread.sleep(RandomUtils.nextInt(1, 5) * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	@Override
	public void setForbidden(String phone) {
		System.out.println("----------------拉黑---------");
	}

	@Override
	public void freePhone(String phone) {
		// TODO Auto-generated method stub

	}

	@Override
	public void freeAllPhone() {
		// TODO Auto-generated method stub
		
	}

}
