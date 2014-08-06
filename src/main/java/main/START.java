package main;

import controller.SystemController;
import controller.impl.SystemControllerImpl;

public class START {
	public static void main(String args[]) {
//		SmtpListener sl = new SmtpListener();
//		sl.startPkg();
		SystemController sc = new SystemControllerImpl();
		sc.startSystem();
	}
	
}
