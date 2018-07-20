package org.telegram.plugins.xuser.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.api.message.TLMessage;
import org.telegram.api.peer.TLAbsPeer;
import org.telegram.api.peer.TLPeerUser;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.IUser;
import org.telegram.plugins.xuser.XUtils;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 16 of October of 2016
 */
public class TLMessageHandler {
	protected Logger logger = LoggerFactory.getLogger(getClass());
    private static final String LOGTAG = "TLMESSAGEHANDLER";
    private final MessageHandler messageHandler;
    private final DatabaseManager databaseManager;

    public TLMessageHandler(MessageHandler messageHandler, DatabaseManager databaseManager) {
        this.messageHandler = messageHandler;
        this.databaseManager = databaseManager;
    }

    public void onTLMessage(TLMessage message) {
        final TLAbsPeer absPeer = message.getToId();
        if (absPeer instanceof TLPeerUser) {
        	logger.info(messageHandler.getBotConfig().getPhoneNumber()+",用户消息:"+ XUtils.transChartset(message.getMessage())+",from="+message.getFromId());
            onTLMessageForUser(message);
        } else {
           // BotLogger.severe(LOGTAG, "Unsupported Peer: " + absPeer.toString());
          
        	logger.info(messageHandler.getBotConfig().getPhoneNumber()+",群组消息:"+message.getChatId()+","+ XUtils.transChartset(message.getMessage())+",from="+message.getFromId()+",date="+message.getDate());
        }
    }

    private void onTLMessageForUser(TLMessage message) {
        if (!message.isSent()) {
            final IUser user = databaseManager.getUserById(message.getFromId());
            if (user != null) {
                this.messageHandler.handleMessage(user, message);
            }else{
            	logger.info(messageHandler.getBotConfig().getPhoneNumber()+" messaget from="+message.getFromId()+" user not exist !");
            	
            }
        }
    }
}
