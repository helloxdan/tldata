import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.thinkgem.jeesite.modules.tl.service.BotService;

/**
 * 
 */

/**
 * @author ThinkPad
 *
 */
public class BotStartor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationContext ac = new ClassPathXmlApplicationContext(
				"applicationcontext.xml");
		BotService botService = (BotService) ac.getBean("botService");

	}

}
