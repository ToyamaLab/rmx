package main;

import controller.SystemController;
import controller.impl.SystemControllerImpl;

public class START {
	public static void main(String args[]) {
		SystemController sc = new SystemControllerImpl();
		sc.startSystem();
	}
	
}
