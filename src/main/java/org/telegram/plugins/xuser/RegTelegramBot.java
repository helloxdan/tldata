package org.telegram.plugins.xuser;

import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.telegram.api.engine.LoggerInterface;
import org.telegram.bot.ChatUpdatesBuilder;
import org.telegram.bot.kernel.MainHandler;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.kernel.engine.MemoryApiState;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.BotConfig;
import org.telegram.bot.structure.LoginStatus;
import org.telegram.mtproto.log.LogInterface;
import org.telegram.mtproto.log.Logger;
import org.telegram.plugins.xuser.support.BotConfigImpl;
import org.telegram.plugins.xuser.work.BotPool;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.modules.tl.service.RegisteService;

/**
 * @author Hendrik Hofstadt
 * @author Ruben Bermudez
 * @version 2.0
 * @brief Bot kernel
 * @date 13.03.14
 */
public class RegTelegramBot extends TelegramBot {
	protected static org.slf4j.Logger logger = LoggerFactory
			.getLogger(RegTelegramBot.class);
	private static final String LOGTAG = "KERNELMAIN";
	// private final BotConfig config;
	// private final ChatUpdatesBuilder chatUpdatesBuilder;
	// private final int apiKey;
	// private final String apiHash;
	// private AbsApiState apiState;
	private RegKernelAuth kernelAuth;
	// private MainHandler mainHandler;
	// private IKernelComm kernelComm;
	private static int TRY_NUM = -1;

	private static String phone = null;

	public RegTelegramBot(BotConfig config,
			ChatUpdatesBuilder chatUpdatesBuilder, int apiKey, String apiHash) {
		super(config, chatUpdatesBuilder, apiKey, apiHash);
		if (config instanceof BotConfigImpl) {
			this.phone = ((BotConfigImpl) config).getPhoneNumber();
		}
		// if (config == null) {
		// throw new NullPointerException("At least a BotConfig must be added");
		// }
		// if (chatUpdatesBuilder == null) {
		// throw new NullPointerException(
		// "At least a ChatUpdatesBuilder must be added");
		// }
		// BotLogger.info(LOGTAG, "--------------KERNEL CREATED--------------");
		setLogging();
		// this.apiKey = apiKey;
		// this.apiHash = apiHash;
		// this.config = config;
		// this.chatUpdatesBuilder = chatUpdatesBuilder;
		// chatUpdatesBuilder
		// .setDifferenceParametersService(new DifferenceParametersService(
		// chatUpdatesBuilder.getDatabaseManager()));
	}

	public static int getTRY_NUM() {
		if (TRY_NUM == -1) {
			String trynum = Global.getConfig("tl.floodwait.trynum");
			if (StringUtils.isNotBlank(trynum)) {
				TRY_NUM = Integer.parseInt(trynum);
			} else {
				TRY_NUM = 3;
			}
		}
		return TRY_NUM;
	}

	public static void setTRY_NUM(int tRY_NUM) {
		TRY_NUM = tRY_NUM;
	}

	static Timer timer = null;
	static int floodCount = 0;

	private static void setLogging() {
		Logger.registerInterface(new LogInterface() {
			@Override
			public void w(String tag, String message) {
				BotLogger.warn("REG TLAPI MTPROTO", phone + "," + message);
				// [5_8_2018_14:18:53] {WARNING} MTPROTO - too more
				if (message != null && message.startsWith("FLOOD_WAIT_")) {
					int delay = Integer.parseInt(message
							.substring("FLOOD_WAIT_".length()));
					// if (delay > 10)
					if (delay > 300)
						floodCount++;
					// 少于3次，忽略
					if (floodCount <= getTRY_NUM())
						return;

					logger.error("reg接口被警告禁用~~~~{}", delay);
					// 停止注册
					RegisteService.start = false;
					if (delay < 300) {
						// 如果被禁时间不是很长，设置定时器，过后再启动
						if (timer == null) {
							timer = new Timer();
							timer.schedule(new TimerTask() {

								@Override
								public void run() {
									logger.error("reg接口被禁用，恢复运行~~~~");
									RegisteService.start = true;
									timer.cancel();
									timer = null;
									floodCount = 0;
								}
							}, (60 + 60) * 1000); // 被禁时间+60秒（改为固定时间120秒）
						}
					} else {
						// 大于300秒的，就直接停止了，不设置定时器重新启动了
						// 如果被禁时间不是很长，设置定时器，过后再启动
						if (timer == null) {
							timer = new Timer();
							timer.schedule(new TimerTask() {

								@Override
								public void run() {
									logger.error("reg接口被禁用，恢复运行~~~~");
									RegisteService.start = true;
									timer.cancel();
									timer = null;
									floodCount = 0;
								}
							}, (60 + 60) * 1000); // 被禁时间+60秒（改为固定时间120秒）
						}
					}
				} else if (message != null && message.startsWith("too more")) {
					// 停止
					logger.error("reg接口警告 too more，停止运行10分钟~~~~~~~~~~~~~");
					RegisteService.start = false;
					BotPool.run = false;

					// 如果被禁时间不是很长，设置定时器，过后再启动
					if (timer == null) {
						timer = new Timer();
						timer.schedule(new TimerTask() {

							@Override
							public void run() {
								logger.error("reg接口被警告too more禁用，10分钟后恢复运行~~~~");
								RegisteService.start = true;
								BotPool.run = true;
								timer.cancel();
								timer = null;
								floodCount = 0;
							}
						}, (600) * 1000); // 10分钟
					}
				} else {

				}
			}

			@Override
			public void d(String tag, String message) {
				BotLogger.debug("MTPROTO", message);
			}

			@Override
			public void e(String tag, String message) {
				BotLogger.error("MTPROTO", message);
			}

			@Override
			public void e(String tag, Throwable t) {
				BotLogger.error("MTPROTO", t);
			}
		});
		org.telegram.api.engine.Logger.registerInterface(new LoggerInterface() {
			@Override
			public void w(String tag, String message) {
				BotLogger.warn("x-TELEGRAMAPI", message);
			}

			@Override
			public void d(String tag, String message) {
				BotLogger.debug("x-TELEGRAMAPI", message);
			}

			@Override
			public void e(String tag, Throwable t) {
				BotLogger.error("x-TELEGRAMAPI", t);
			}
		});
	}

	public LoginStatus init() throws NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		BotLogger.debug(LOGTAG, "Creating API");
		MemoryApiState apiState1 = new MemoryApiState(getConfig().getAuthfile());
		// FIXME  默认的dc
		 apiState1.setPrimaryDc(XUserBot.defaultDc);
		setApiState(apiState1);
		BotLogger.debug(LOGTAG, "API created");
		createKernelComm(); // Only set up threads and assign api state
		createKernelAuth(); // Only assign api state to kernel auth
		initKernelComm(); // Create TelegramApi and run the updates handler
							// threads
		// final LoginStatus loginResult = startKernelAuth(); // Perform login
		// if necessary
		final LoginStatus loginResult = null;// regAccount();
		createKernelHandler(); // Create rest of handlers
		BotLogger.info(LOGTAG, "----------------BOT READY-----------------");
		return loginResult;
	}

	/**
	 * @return
	 */
	public LoginStatus regAccount() {
		final long start = System.currentTimeMillis();
		final LoginStatus status = this.kernelAuth.registe();
		BotLogger.info(LOGTAG, String.format("%s started in %d ms",
				this.kernelAuth.getClass().getName(),
				(start - System.currentTimeMillis()) * -1));
		return status;
	}

	// public void startBot() {
	// initKernelHandler();
	// }
	//
	// public void stopBot() {
	// this.mainHandler.stop();
	// }

	private void initKernelHandler() {
		final long start = System.currentTimeMillis();
		getMainHandler().start();
		BotLogger.info(LOGTAG, String.format("%s init in %d ms",
				this.kernelAuth.getClass().getName(),
				(start - System.currentTimeMillis()) * -1));
	}

	private void initKernelComm() {
		final long start = System.currentTimeMillis();
		getKernelComm().init();
		BotLogger.info(LOGTAG, String.format("%s init in %d ms",
				getKernelComm().getClass().getName(),
				(start - System.currentTimeMillis()) * -1));
	}

	private void createKernelAuth() {
		final long start = System.currentTimeMillis();
		// this.kernelAuth = new KernelAuth(this.apiState, config,
		// this.kernelComm, apiKey, apiHash);
		this.kernelAuth = new RegKernelAuth(getApiState(), getConfig(),
				getKernelComm(), getApiKey(), getApiHash());
		setKernelAuth(this.kernelAuth);
		BotLogger.info(LOGTAG, String.format("%s init in %d ms",
				this.kernelAuth.getClass().getName(),
				(start - System.currentTimeMillis()) * -1));
	}

	private LoginStatus startKernelAuth() {
		final long start = System.currentTimeMillis();
		final LoginStatus status = this.kernelAuth.start();
		BotLogger.info(LOGTAG, String.format("%s started in %d ms",
				this.kernelAuth.getClass().getName(),
				(start - System.currentTimeMillis()) * -1));
		return status;
	}

	private void createKernelHandler() throws InvocationTargetException,
			NoSuchMethodException, InstantiationException,
			IllegalAccessException {
		final long start = System.currentTimeMillis();
		getChatUpdatesBuilder().setKernelComm(getKernelComm());
		MainHandler mainHandler2 = new MainHandler(getKernelComm(),
				getChatUpdatesBuilder().build());
		setMainHandler(mainHandler2);
		BotLogger.info(LOGTAG, String.format("%s init in %d ms",
				getMainHandler().getClass().getName(),
				(start - System.currentTimeMillis()) * -1));
	}

	private void createKernelComm() {
		final long start = System.currentTimeMillis();
		// this.kernelComm = new KernelComm(getApiKey(), getApiState());
		XKernelComm kernelComm2 = new XKernelComm(getApiKey(), getApiState());
		// kernelComm2.setBot(this);
		setKernelComm(kernelComm2);
		BotLogger.info(LOGTAG, String.format("%s init in %d ms",
				getKernelComm().getClass().getName(),
				(start - System.currentTimeMillis()) * -1));
	}

}
