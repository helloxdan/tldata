/**
 * Copyright &copy; 2017-2020 <a href="https://www.gzruimin.com">gzruimin</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.plugins.xuser.entity.ChatImpl;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.tl.entity.Chat;
import com.thinkgem.jeesite.modules.tl.dao.ChatDao;

/**
 * 群组会话记录Service
 * 
 * @author admin
 * @version 2018-06-02
 */
@Service
@Transactional(readOnly = true)
public class ChatService extends CrudService<ChatDao, Chat> {

	public Chat get(String id) {
		return super.get(id);
	}

	public List<Chat> findList(Chat chat) {
		return super.findList(chat);
	}

	public Page<Chat> findPage(Page<Chat> page, Chat chat) {
		return super.findPage(page, chat);
	}

	@Transactional(readOnly = false)
	public void save(Chat chat) {
		super.save(chat);
	}

	@Transactional(readOnly = false)
	public void delete(Chat chat) {
		super.delete(chat);
	}

	@Transactional(readOnly = false)
	public void del(String ids) {
		if (StringUtils.isNoneBlank(ids)) {
			String[] delIds = ids.split(",");
			for (String id : delIds) {
				Chat chat = new Chat(id);
				super.delete(chat);
			}
		}
		// 删除缓存
		// IcareUtils.removeCache();
	}

	public org.telegram.bot.structure.@Nullable Chat getChatById(String phone,
			int chatId) {
		ChatImpl channel = null;
		// Chat chat = new Chat();
		// chat.setAccount(phone);
		// chat.setChatid(chatId + "");
		// List<Chat> list = findList(chat);
		Chat chat = get(phone + chatId);
		if (chat != null) {
			channel = new ChatImpl(chatId);
			channel.setAccessHash(chat.getAccesshash());
			channel.setChannel(1 == chat.getIsChannel());
		} else {
			logger.debug("{}会话id={}不存在", phone, chatId);
		}
		return channel;
	}
}