import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import org.apache.commons.lang3.RandomUtils;

import nl.dannyvanheumen.nio.ProxiedSocketChannel;

public class SocketChannelTest {
	public static void main(String[] args) {
//		
//		String botVersion = String.format("%d.%d.%d",
//				RandomUtils.nextInt(1, 5), RandomUtils.nextInt(1, 10),
//				RandomUtils.nextInt(1, 10));
//		System.out.println(botVersion);  
		try {
			// System.getProperties().setProperty("socksProxySet", "true");
			// System.getProperties().setProperty("socksProxyHost",
			// "127.0.0.1");
			// System.getProperties().setProperty("socksProxyPort", "1080");
			 
			InetSocketAddress proxyAddr = new InetSocketAddress("127.0.0.1", 1080);
			Proxy proxy = new Proxy(Proxy.Type.SOCKS, proxyAddr);
//			 SocketChannel s = SocketChannel.open();
			SocketChannel s = new ProxiedSocketChannel(proxy);
			InetSocketAddress addr = new InetSocketAddress("173.240.5.1", 443);
			if (s.connect(addr)) {
				System.out.println("Socks Proxy Complete!");
			System.out.println(String.format("%s,%s,%s,%s", s.isBlocking(),s.isConnected(),s.isOpen(),s.provider()));
			} else {
				System.out.println("Socks Proxy Failed!");
			}
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
