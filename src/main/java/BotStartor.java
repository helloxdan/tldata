import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
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
	static ApplicationContext ac;
	static BotService botService;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// initContext();
		// 取配置文件信息，得到jobid，拉人总数和采集群信息
		String cfgFileName = System.getProperty("filename");
		System.out.println(cfgFileName);
		File cfgFile=new File(cfgFileName);
		List<String> lines=new ArrayList<String>();
		try {
			lines = FileUtils.readLines(cfgFile);
			for (String l : lines) {
				System.out.println(l);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 卡商编码
		String cardCode = System.getProperty("cardCode");
		System.out.println(cardCode);
	}

	private static void initContext() {
		ac = new ClassPathXmlApplicationContext("applicationcontext.xml");
		botService = (BotService) ac.getBean("botService");
	}

}
