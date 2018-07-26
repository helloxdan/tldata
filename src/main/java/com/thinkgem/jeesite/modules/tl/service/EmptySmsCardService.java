package com.thinkgem.jeesite.modules.tl.service;

import java.util.ArrayList;
import java.util.List;

public class EmptySmsCardService implements SmsCardService {

	@Override
	public void setForbidden(String phone) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getPhoneList() {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}

	@Override
	public List<String[]> getPhoneCode(String phone) {
		// TODO Auto-generated method stub
		return new ArrayList<String[]>();
	}

	@Override
	public void freePhone(String phone) {
		// TODO Auto-generated method stub
		
	}

}
