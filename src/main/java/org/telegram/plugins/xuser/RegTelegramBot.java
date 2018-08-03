package org.telegram.plugins.xuser;

import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.LoggerFactory;
import org.telegram.api.engine.LoggerInterface;
import org.telegram.api.engine.storage.AbsApiState;
import org.telegram.bot.ChatUpdatesBuilder;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.KernelAuth;
import org.telegram.bot.kernel.KernelComm;
import org.telegram.bot.kernel.MainHandler;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.kernel.differenceparameters.DifferenceParametersService;
import org.telegram.bot.kernel.engine.MemoryApiState;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.BotConfig;
import org.telegram.bot.structure.LoginStatus;
import org.telegram.mtproto.log.LogInterface;
import org.telegram.mtproto.log.Logger;

import com.thinkgem.jeesite.modules.tl.service.RegistePoolService;

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
	private final BotConfig config;
	private final ChatUpdatesBuilder chatUpdatesBuilder;
	private final int apiKey;
	private final String apiHash;
	private AbsApiState apiState;
	private RegKernelAuth kernelAuth;
	private MainHandler mainHandler;
	private IKernelComm kernelComm;

	public RegTelegramBot(BotConfig config,
			ChatUpdatesBuilder chatUpdatesBuilder, int apiKey, String apiHash) {
		super(config, chatUpdatesBuilder, apiKey, apiHash);
		if (config == null) {
			throw new NullPointerException("At least a BotConfig must be added");
		}
		if (chatUpdatesBuilder == null) {
			throw new NullPointerException(
					"At least a ChatUpdatesBuilder must be added");
		}
		BotLogger.info(LOGTAG, "--------------KERNEL CREATED--------------");
		setLogging();
		this.apiKey = apiKey;
		this.apiHash = apiHash;
		this.config = config;
		this.chatUpdatesBuilder = chatUpdatesBuilder;
		chatUpdatesBuilder
				.setDifferenceParametersService(new DifferenceParametersService(
						chatUpdatesBuilder.getDatabaseManager()));
	}

	static Timer timer = null;
	static int floodCount = 0;

	private static void setLogging() {
		Logger.registerInterface(new LogInterface() {
			@Override
			public void w(String tag, String message) {
				BotLogger.warn("MTPROTO", message);
				if (message != null && message.startsWith("FLOOD_WAIT_")) {
					int delay = Integer.parseInt(message
							.substring("FLOOD_WAIT_".length()));
					logger.error("接口被禁用~~~~{}", delay);
					if (delay > 10)
						floodCount++;
					// 少于3次，忽略
					if (floodCount <= 3)
						return;
					// 停止注册
					RegistePoolService.start = false;
					if (delay < 300) {
						// 如果被禁时间不是很长，设置定时器，过后再启动
						if (timer == null) {
							timer = new Timer();
							timer.schedule(new TimerTask() {

								@Override
								public void run() {
									logger.error("reg接口被禁用，恢复运行~~~~");
									RegistePoolService.start = true;
									timer.cancel();
									timer = null;
									floodCount = 0;
								}
							}, (delay + 60) * 1000); // 被禁时间+60秒
						}
					}
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
				BotLogger.warn("TELEGRAMAPI", message);
			}

			@Override
			public void d(String tag, String message) {
				BotLogger.debug("TELEGRAMAPI", message);
			}

			@Override
			public void e(String tag, Throwable t) {
				BotLogger.error("TELEGRAMAPI", t);
			}
		});
	}

	public LoginStatus init() throws NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		BotLogger.debug(LOGTAG, "Creating API");
		apiState = new MemoryApiState(config.getAuthfile());
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

	public void startBot() {
		initKernelHandler();
	}

	public void stopBot() {
		// this.mainHandler.stop();
	}

	private void initKernelHandler() {
		final long start = System.currentTimeMillis();
		this.mainHandler.start();
		BotLogger.info(LOGTAG, String.format("%s init in %d ms",
				this.kernelAuth.getClass().getName(),
				(start - System.currentTimeMillis()) * -1));
	}

	private void initKernelComm() {
		final long start = System.currentTimeMillis();
		this.kernelComm.init();
		BotLogger.info(LOGTAG, String.format("%s init in %d ms",
				this.kernelComm.getClass().getName(),
				(start - System.currentTimeMillis()) * -1));
	}

	private void createKernelAuth() {
		final long start = System.currentTimeMillis();
		// this.kernelAuth = new KernelAuth(this.apiState, config,
		// this.kernelComm, apiKey, apiHash);
		this.kernelAuth = new RegKernelAuth(this.apiState, config,
				this.kernelComm, apiKey, apiHash);
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
		chatUpdatesBuilder.setKernelComm(kernelComm);
		this.mainHandler = new MainHandler(kernelComm,
				chatUpdatesBuilder.build());
		BotLogger.info(LOGTAG, String.format("%s init in %d ms",
				this.mainHandler.getClass().getName(),
				(start - System.currentTimeMillis()) * -1));
	}

	private void createKernelComm() {
		final long start = System.currentTimeMillis();
		this.kernelComm = new KernelComm(apiKey, apiState);
		BotLogger.info(LOGTAG, String.format("%s init in %d ms",
				getKernelComm().getClass().getName(),
				(start - System.currentTimeMillis()) * -1));
	}

	public BotConfig getConfig() {
		return this.config;
	}

	public IKernelComm getKernelComm() {
		return this.kernelComm;
	}

	public KernelAuth getKernelAuth() {
		return this.kernelAuth;
	}

	public MainHandler getMainHandler() {
		return this.mainHandler;
	}

	public boolean isAuthenticated() {
		return this.apiState.isAuthenticated();
	}
}
