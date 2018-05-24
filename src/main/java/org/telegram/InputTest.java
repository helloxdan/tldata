package org.telegram;

import java.util.Scanner;

public class InputTest {
	public static void main(String[] args) {
		gotoCmd();
	}
	private static  void gotoCmd() {
		Scanner input = new Scanner(System.in);
		String val = null; // 记录输入的字符串
		do {
			System.out.print(">>");
			val = input.nextLine(); // 等待输入值
			System.out.println("您输入的是：" + val);
			if ("me".equals(val)) {
				System.out.println("show me info");
			} else if ("getChats".equals(val)) {
				System.out.println("get my all chats list");

			}else if (val.startsWith("addUser")) {
				System.out.println("add User to chat");
				
			}  else {
				System.out.println("未知命令");
			}

		} while (!val.equals("#")); // 如果输入的值不是#就继续输入
		System.out.println("你输入了\"#\"，程序已经退出！");
		input.close(); // 关闭资源
	}
}
