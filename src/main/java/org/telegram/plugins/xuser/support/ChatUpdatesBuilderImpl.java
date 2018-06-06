package org.telegram.plugins.xuser.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Scanner;

import org.telegram.api.channel.TLChannelParticipants;
import org.telegram.api.channel.participants.filters.TLChannelParticipantsFilterRecent;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.chat.TLChat;
import org.telegram.api.chat.channel.TLChannel;
import org.telegram.api.contact.TLContactStatus;
import org.telegram.api.engine.RpcCallback;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.channels.TLRequestChannelsGetFullChannel;
import org.telegram.api.functions.channels.TLRequestChannelsGetParticipants;
import org.telegram.api.functions.channels.TLRequestChannelsInviteToChannel;
import org.telegram.api.functions.contacts.TLRequestContactsGetStatuses;
import org.telegram.api.functions.messages.TLRequestMessagesAddChatUser;
import org.telegram.api.functions.messages.TLRequestMessagesGetDialogs;
import org.telegram.api.functions.messages.TLRequestMessagesGetFullChat;
import org.telegram.api.functions.users.TLRequestUsersGetFullUser;
import org.telegram.api.input.chat.TLInputChannel;
import org.telegram.api.input.peer.TLInputPeerSelf;
import org.telegram.api.input.user.TLAbsInputUser;
import org.telegram.api.input.user.TLInputUser;
import org.telegram.api.messages.TLMessagesChatFull;
import org.telegram.api.messages.dialogs.TLAbsDialogs;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.api.user.TLUserFull;
import org.telegram.bot.ChatUpdatesBuilder;
import org.telegram.bot.handlers.UpdatesHandlerBase;
import org.telegram.bot.handlers.interfaces.IChatsHandler;
import org.telegram.bot.handlers.interfaces.IUsersHandler;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.kernel.differenceparameters.IDifferenceParametersService;
import org.telegram.bot.structure.BotConfig;
import org.telegram.plugins.xuser.IBotDataService;
import org.telegram.plugins.xuser.entity.User;
import org.telegram.plugins.xuser.handler.MessageHandler;
import org.telegram.plugins.xuser.handler.TLMessageHandler;
import org.telegram.tl.TLObject;
import org.telegram.tl.TLVector;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 16 of October of 2016
 */
public class ChatUpdatesBuilderImpl implements ChatUpdatesBuilder {
	private final Class<CustomUpdatesHandler> updatesHandlerBase;
	private IKernelComm kernelComm;
	private IUsersHandler usersHandler;
	private BotConfig botConfig;
	private IChatsHandler chatsHandler;
	private MessageHandler messageHandler;
	private TLMessageHandler tlMessageHandler;
	private IDifferenceParametersService differenceParametersService;
	private DatabaseManager databaseManager;

	public ChatUpdatesBuilderImpl(Class<CustomUpdatesHandler> updatesHandlerBase) {
		this.updatesHandlerBase = updatesHandlerBase;
	}

	@Override
	public void setKernelComm(final IKernelComm kernelComm) {
		this.kernelComm = kernelComm;
		Thread t = new Thread() {
			public void run() {
				try {
					Thread.sleep(8000L);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				gotoCmd(kernelComm);
			}
		};
		t.start();
	}

	private void gotoCmd(IKernelComm kernelComm) {
		Scanner input = new Scanner(System.in);
		String val = null; // 记录输入的字符串
		do {
			try {
				System.out.print(">>");
				val = input.nextLine(); // 等待输入值
				System.out.println("您输入的是：" + val);
				if ("me".equals(val)) {
					System.out.println("show me info");
					requestGetMe(kernelComm);
				} else if ("getDls".equals(val)) {
					System.out.println("get getDls");
					requestGetDialogs(kernelComm);
				} else if (val.startsWith("getChats")) {
					System.out.println("get my all chats list");
					String[] args = val.split(",");
					Integer chatId = Integer.parseInt(args[1].trim());
					requestGetChats(kernelComm, chatId);
				} else if ("getContact".equals(val)) {
					System.out.println("  getContact");

					requestGetStatuses(kernelComm);
				} else if (val.startsWith("getChannel")) {
					System.out.println("get  Channel");
					String[] args = val.split(",");
					Integer cid = Integer.parseInt(args[1].trim());
					long accesshash = Long.parseLong(args[2].trim());
					TLChannel c = new TLChannel();
					c.setId(cid);
					c.setAccessHash(accesshash);
					requestGetChannelPar(kernelComm, c);
				} else if (val.startsWith("getUser")) {
					System.out.println("get user");
					String[] args = val.split(",");
					Integer userId = Integer.parseInt(args[1].trim());
					requestGetUser(kernelComm, userId);
				} else if (val.startsWith("addChatUser")) {
					System.out.println("add User to chat");
					String[] args = val.split(",");
					Integer chatId = Integer.parseInt(args[1].trim());
					Integer userId = Integer.parseInt(args[2].trim());
					long accessHash = Long.parseLong(args[3].trim());
					requestAddChatUser(kernelComm, chatId, userId, accessHash);
				} else if (val.startsWith("addUser")) {
					System.out.println("add User to chat");
					String[] args = val.split(",");
					Integer chatId = Integer.parseInt(args[1].trim());
					long accessHash1 = Long.parseLong(args[2].trim());
					Integer userId = Integer.parseInt(args[3].trim());
					long accessHash = Long.parseLong(args[4].trim());
					requestAddChannelUser(kernelComm, chatId, accessHash1, userId, accessHash);
				} else if (val.startsWith("addMUser")) {
					System.out.println("add multi User to channel");
					String[] args = val.split(",");
					Integer chatId = Integer.parseInt(args[1].trim());
					long accessHash1 = Long.parseLong(args[2].trim());

					requestAddChannelUsers(kernelComm, chatId, accessHash1);
				} else {
					System.out.println("未知命令");
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

		} while (!val.equals("#")); // 如果输入的值不是#就继续输入
		System.out.println("你输入了\"#\"，程序已经退出！");
		input.close(); // 关闭资源
	}

	private void requestAddChannelUser(IKernelComm kernel, Integer channelId, long accessHash1, Integer userId,
			long accessHash) {
		//
		TelegramApi api = kernel.getApi();
		TLRequestChannelsInviteToChannel req = new TLRequestChannelsInviteToChannel();
		TLInputChannel ch = new TLInputChannel();
		ch.setChannelId(channelId);
		ch.setAccessHash(accessHash1);
		req.setChannel(ch);

		TLInputUser user = new TLInputUser();
		user.setUserId(userId);
		user.setAccessHash(accessHash);
		TLVector<TLAbsInputUser> users = new TLVector<TLAbsInputUser>();
		users.add(user);
		req.setUsers(users);

		api.doRpcCall(req, new RpcCallback() {

			@Override
			public void onResult(TLObject result) {
				System.out.println(result);

				TLVector<TLContactStatus> dl = (TLVector<TLContactStatus>) result;
				for (TLContactStatus s : dl) {
					System.out.println(s.getUserId());
				}
			}

			@Override
			public void onError(int errorCode, String message) {
				// TODO Auto-generated method stub

				System.out.println(errorCode + "," + message);
			}

		});
	}

	private void requestAddChannelUsers(IKernelComm kernel, Integer channelId, long accessHash1) {
		//
		TelegramApi api = kernel.getApi();
		TLRequestChannelsInviteToChannel req = new TLRequestChannelsInviteToChannel();
		TLInputChannel ch = new TLInputChannel();
		ch.setChannelId(channelId);
		ch.setAccessHash(accessHash1);
		req.setChannel(ch);

		int snum = 250;
		int len = snum + 50;
		IBotDataService dbm = (IBotDataService) databaseManager;

		TLVector<TLAbsInputUser> users = new TLVector<TLAbsInputUser>();

		List<User> list = dbm.findUsers();
		if (len > list.size())
			len = list.size();
		for (int i = snum; i < len; i++) {
			User u = list.get(i);
			TLInputUser user = new TLInputUser();
			user.setUserId(u.getUserId());
			user.setAccessHash(u.getUserHash());
			users.add(user);
		}
		req.setUsers(users);

		api.doRpcCall(req, new RpcCallback() {

			@Override
			public void onResult(TLObject result) {
				System.out.println(result);

				TLVector<TLContactStatus> dl = (TLVector<TLContactStatus>) result;
				for (TLContactStatus s : dl) {
					System.out.println(s.getUserId());
				}
			}

			@Override
			public void onError(int errorCode, String message) {
				// TODO Auto-generated method stub

				System.out.println(errorCode + "," + message);
			}

		});
	}

	private void requestAddChatUser(IKernelComm kernel, Integer chatId, Integer userId, long accessHash) {
		// 304385712
		TelegramApi api = kernel.getApi();
		TLRequestMessagesAddChatUser req = new TLRequestMessagesAddChatUser();
		req.setChatId(chatId);

		TLInputUser user = new TLInputUser();
		user.setUserId(userId);
		user.setAccessHash(accessHash);
		req.setUserId(user);

		api.doRpcCall(req, new RpcCallback() {

			@Override
			public void onResult(TLObject result) {
				System.out.println(result);

				TLVector<TLContactStatus> dl = (TLVector<TLContactStatus>) result;
				for (TLContactStatus s : dl) {
					System.out.println(s.getUserId());
				}
			}

			@Override
			public void onError(int errorCode, String message) {
				// TODO Auto-generated method stub

				System.out.println(errorCode + "," + message);
			}

		});
	}

	private void requestGetStatuses(IKernelComm kernel) {
		//
		TelegramApi api = kernel.getApi();
		TLRequestContactsGetStatuses req = new TLRequestContactsGetStatuses();

		api.doRpcCall(req, new RpcCallback() {

			@Override
			public void onResult(TLObject result) {
				System.out.println(result);

				TLVector<TLContactStatus> dl = (TLVector<TLContactStatus>) result;
				for (TLContactStatus s : dl) {
					System.out.println(s.getUserId());
				}
			}

			@Override
			public void onError(int errorCode, String message) {
				// TODO Auto-generated method stub

				System.out.println(errorCode + "," + message);
			}

		});
	}

	private void requestGetDialogs(IKernelComm kernel) {
		IChatsHandler chatsHandler1 = chatsHandler;
		IUsersHandler userHandler2 = usersHandler;
		IBotDataService dbm = (IBotDataService) databaseManager;
		//
		TelegramApi api = kernel.getApi();
		TLRequestMessagesGetDialogs req = new TLRequestMessagesGetDialogs();
		req.setLimit(30);
		req.setOffsetDate(0);
		// req.setOffsetId(offsetId);//最大id
		req.setOffsetPeer(new TLInputPeerSelf());
		api.doRpcCall(req, new RpcCallback() {

			@Override
			public void onResult(TLObject result) {
				System.out.println(result);

//				dbm.begin();

				TLAbsDialogs dl = (TLAbsDialogs) result;
				TLVector<TLAbsChat> chats = dl.getChats();
				for (TLAbsChat ch : chats) {

					if (ch instanceof TLChannel) {
						TLChannel c = (TLChannel) ch;
						System.out.println(ch + "---:" + ch.getId() + ":" + c.getAccessHash() + "--::" + c.getTitle());
						// requestGetChannel(kernel, (TLChannel) ch);
						// requestGetChannelPar(kernel, c);
						// break;
					}
				}
				// put db
				chatsHandler1.onChats(chats);

				TLVector<TLAbsUser> users = dl.getUsers();
				System.out.println("------33--------user---" + users.size());

				for (TLAbsUser u : users) {
					TLUser tu = (TLUser) u;
					System.out.println(u + "---:" + u.getId() + "--::" + tu.getAccessHash());
					// System.out.println(tu.getFirstName() + tu.getLastName());
					// System.out.println(tu.getUserName());
					// System.out.println(tu.getPhone());
					// System.out.println(tu.getStatus());
					// System.out.println(tu.getAccessHash());
				}

				userHandler2.onUsers(users);

//				dbm.commit();
			}

			@Override
			public void onError(int errorCode, String message) {
				// TODO Auto-generated method stub

				System.out.println(errorCode + "," + message);
			}

		});
	}

	private void requestGetChannelPar(IKernelComm kernel, TLChannel ch) {
		TelegramApi api = kernel.getApi();
		TLRequestChannelsGetParticipants req = new TLRequestChannelsGetParticipants();
		TLInputChannel c = new TLInputChannel();
		c.setChannelId(ch.getId());
		c.setAccessHash(ch.getAccessHash());
		req.setChannel(c);
		req.setOffset(0);
		req.setLimit(1000);
		req.setFilter(new TLChannelParticipantsFilterRecent());

		IBotDataService dbm = (IBotDataService) databaseManager;
		IUsersHandler userHandler2 = usersHandler;
		api.doRpcCall(req, new RpcCallback() {

			@Override
			public void onResult(TLObject result) {
				// TODO Auto-generated method stub
				System.out.println(result);
				TLChannelParticipants c = (TLChannelParticipants) result;
				System.out.println(c);
				System.out.println(c.getCount());
				System.out.println("--p.size-");
				System.out.println(c.getParticipants().size());
				System.out.println("---u.size");
				System.out.println(c.getUsers().size());

//				dbm.begin();
				userHandler2.onUsers(c.getUsers());
//				dbm.commit();
			}

			@Override
			public void onError(int errorCode, String message) {
				// TODO Auto-generated method stub

				System.out.println(errorCode + "," + message);
			}

		});
	}

	private void requestGetChannel(IKernelComm kernel, TLChannel ch) {
		TelegramApi api = kernel.getApi();
		TLRequestChannelsGetFullChannel req = new TLRequestChannelsGetFullChannel();
		TLInputChannel c = new TLInputChannel();
		c.setChannelId(ch.getId());
		c.setAccessHash(ch.getAccessHash());
		req.setChannel(c);

		api.doRpcCall(req, new RpcCallback() {

			@Override
			public void onResult(TLObject result) {
				// TODO Auto-generated method stub
				System.out.println(result);
				TLMessagesChatFull c = (TLMessagesChatFull) result;
				System.out.println(c);
				TLVector<TLAbsChat> chats = c.getChats();
				for (TLAbsChat cc : chats) {
					TLChannel ch = (TLChannel) cc;
					System.out.println(ch.getTitle());
					// System.out.println(ch.getParticipantsCount());
					System.out.println(ch.getVersion());
					System.out.println(ch.getClassId());

				}
				System.out.println("---");
				System.out.println(c.getUsers().size());
			}

			@Override
			public void onError(int errorCode, String message) {
				// TODO Auto-generated method stub

				System.out.println(errorCode + "," + message);
			}

		});
	}

	private void requestGetMe(IKernelComm kernel) {
		TelegramApi api = kernel.getApi();
		TLRequestUsersGetFullUser req = new TLRequestUsersGetFullUser();
		TLInputUser u = new TLInputUser();
		u.setUserId(kernel.getCurrentUserId());
		req.setId(u);
		api.doRpcCall(req, new RpcCallback() {

			@Override
			public void onResult(TLObject result) {
				// TODO Auto-generated method stub
				System.out.println(result);
				TLUserFull u = (TLUserFull) result;
				System.out.println(u.getAbout());
				System.out.println(u.getCommonChatsCount());
				System.out.println(u.getLink());
			}

			@Override
			public void onError(int errorCode, String message) {
				// TODO Auto-generated method stub

				System.out.println(errorCode + "," + message);
			}

		});
	}

	private void requestGetChats(IKernelComm kernel, Integer chatId) {
		TelegramApi api = kernel.getApi();
		TLRequestMessagesGetFullChat req = new TLRequestMessagesGetFullChat();
		// TLIntVector v = new TLIntVector();
		// v.add(chatId);
		// req.setExceptIds(v);
		req.setChatId(chatId);
		api.doRpcCall(req, new RpcCallback() {

			@Override
			public void onResult(TLObject result) {
				// TODO Auto-generated method stub
				System.out.println(result);
				TLMessagesChatFull c = (TLMessagesChatFull) result;
				System.out.println(c);
				TLVector<TLAbsChat> chats = c.getChats();
				for (TLAbsChat cc : chats) {
					TLChat ch = (TLChat) cc;
					System.out.println(ch.getTitle());
					System.out.println(ch.getParticipantsCount());
					System.out.println(ch.getVersion());
					System.out.println(ch.getClassId());

				}
				System.out.println("---");
				System.out.println(c.getUsers().size());
			}

			@Override
			public void onError(int errorCode, String message) {
				// TODO Auto-generated method stub

				System.out.println(errorCode + "," + message);
			}

		});
	}

	private void requestGetUser(IKernelComm kernel, Integer userId) {
		TelegramApi api = kernel.getApi();
		TLRequestUsersGetFullUser req = new TLRequestUsersGetFullUser();
		TLInputUser u = new TLInputUser();
		u.setUserId(userId);
		req.setId(u);
		api.doRpcCall(req, new RpcCallback() {

			@Override
			public void onResult(TLObject result) {
				// TODO Auto-generated method stub
				System.out.println(result);
				TLUserFull u = (TLUserFull) result;
				System.out.println(u.getAbout());
				System.out.println(u.getCommonChatsCount());
				System.out.println(u.getLink());
				System.out.println(u.getCommonChatsCount());
				TLUser tu = (TLUser) u.getUser();
				System.out.println(tu.getFirstName() + tu.getLastName());
				System.out.println(tu.getUserName());
				System.out.println(tu.getPhone());
				System.out.println(tu.getStatus());
				System.out.println(tu.getAccessHash());

			}

			@Override
			public void onError(int errorCode, String message) {
				// TODO Auto-generated method stub

				System.out.println(errorCode + "," + message);
			}

		});
	}

	@Override
	public void setDifferenceParametersService(IDifferenceParametersService differenceParametersService) {
		this.differenceParametersService = differenceParametersService;
	}

	@Override
	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}

	public ChatUpdatesBuilderImpl setUsersHandler(IUsersHandler usersHandler) {
		this.usersHandler = usersHandler;
		return this;
	}

	public ChatUpdatesBuilderImpl setChatsHandler(IChatsHandler chatsHandler) {
		this.chatsHandler = chatsHandler;
		return this;
	}

	public ChatUpdatesBuilderImpl setBotConfig(BotConfig botConfig) {
		this.botConfig = botConfig;
		return this;
	}

	public ChatUpdatesBuilderImpl setMessageHandler(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
		return this;
	}

	public ChatUpdatesBuilderImpl setDatabaseManager(IBotDataService databaseManager) {
		this.databaseManager = databaseManager;
		return this;
	}

	public ChatUpdatesBuilderImpl setTlMessageHandler(TLMessageHandler tlMessageHandler) {
		this.tlMessageHandler = tlMessageHandler;
		return this;
	}

	@Override
	public UpdatesHandlerBase build()
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		if (kernelComm == null) {
			throw new NullPointerException("Can't build the handler without a KernelComm");
		}
		if (differenceParametersService == null) {
			throw new NullPointerException("Can't build the handler without a differenceParamtersService");
		}

		messageHandler.setKernelComm(this.kernelComm);
		final Constructor<CustomUpdatesHandler> constructor = updatesHandlerBase.getConstructor(IKernelComm.class,
				IDifferenceParametersService.class, DatabaseManager.class);
		final CustomUpdatesHandler updatesHandler = constructor.newInstance(kernelComm, differenceParametersService,
				getDatabaseManager());
		updatesHandler.setConfig(botConfig);
		updatesHandler.setHandlers(messageHandler, usersHandler, chatsHandler, tlMessageHandler);
		return updatesHandler;
	}
}
