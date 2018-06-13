package org.telegram;

import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.telegram.api.engine.RpcCallback;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.messages.TLRequestMessagesGetAllChats;
import org.telegram.bot.handlers.interfaces.IChatsHandler;
import org.telegram.bot.handlers.interfaces.IUsersHandler;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.BotConfig;
import org.telegram.bot.structure.LoginStatus;
import org.telegram.plugins.echo.BotConfigImpl;
import org.telegram.plugins.echo.ChatUpdatesBuilderImpl;
import org.telegram.plugins.echo.CustomUpdatesHandler;
import org.telegram.plugins.echo.database.DatabaseManagerImpl;
import org.telegram.plugins.echo.handlers.ChatsHandler;
import org.telegram.plugins.echo.handlers.MessageHandler;
import org.telegram.plugins.echo.handlers.TLMessageHandler;
import org.telegram.plugins.echo.handlers.UsersHandler;
import org.telegram.tl.TLObject;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 16 of October of 2016
 */
public class Deepthought {
    private static final int APIKEY = 202491; // your api key
    private static final String APIHASH = "9f32d44fca581599dbbe02cec25ffe58"; // your api hash
//    private static final String PHONENUMBER = "8618566104318"; // Your phone number
	
//  private static final int APIKEY = 208853; // your api key
//  private static final String APIHASH = "2b0149b45cfc13f5b272ad832608066b"; // your api hash
  private static final String PHONENUMBER = "8613751867473"; // Your phone number
    public static void main(String[] args) {
        Logger.getGlobal().addHandler(new ConsoleHandler());
        Logger.getGlobal().setLevel(Level.ALL);


        final DatabaseManagerImpl databaseManager = new DatabaseManagerImpl();
        final BotConfig botConfig = new BotConfigImpl(PHONENUMBER);

        final IUsersHandler usersHandler = new UsersHandler(databaseManager);
        final IChatsHandler chatsHandler = new ChatsHandler(databaseManager);
        final MessageHandler messageHandler = new MessageHandler();
        final TLMessageHandler tlMessageHandler = new TLMessageHandler(messageHandler, databaseManager);

        final ChatUpdatesBuilderImpl builder = new ChatUpdatesBuilderImpl(CustomUpdatesHandler.class);
        builder.setBotConfig(botConfig)
                .setDatabaseManager(databaseManager)
                .setUsersHandler(usersHandler)
                .setChatsHandler(chatsHandler)
                .setMessageHandler(messageHandler)
                .setTlMessageHandler(tlMessageHandler);
        
        try {
            final TelegramBot kernel = new TelegramBot(botConfig, builder, APIKEY, APIHASH);
            LoginStatus status = kernel.init();
            if (status == LoginStatus.CODESENT) {
                Scanner in = new Scanner(System.in);
                boolean success = kernel.getKernelAuth().setAuthCode(in.nextLine().trim());
                if (success) {
                    status = LoginStatus.ALREADYLOGGED;
                }
            }
            if (status == LoginStatus.ALREADYLOGGED) {
                kernel.startBot();
            } else {
                throw new Exception("Failed to log in: " + status);
            }
            
            //业务操作
           // doMyTask(kernel);
            
        } catch (Exception e) {
            BotLogger.severe("MAIN", e);
        }
    }

	private static void doMyTask(TelegramBot kernel) {
		// TODO Auto-generated method stub
		TelegramApi api = kernel.getKernelComm().getApi();
		api.doRpcCall(new TLRequestMessagesGetAllChats() ,new RpcCallback(){

			@Override
			public void onResult(TLObject result) {
				// TODO Auto-generated method stub
				System.out.println(result);
			}

			@Override
			public void onError(int errorCode, String message) {
				// TODO Auto-generated method stub
				
				System.out.println(errorCode+","+message);
			}
			
		} );
	}
}
