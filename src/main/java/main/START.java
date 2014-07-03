package main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import dao.DBDao;
import logic.RmxController;
import logic.parse.Distributor;
import logic.parse.User;
import logic.parse.User1;
import logic.propfile.PropfileOperator;
import logic.service.ParseService;

public class START {
	public static void main(String args[]) {
		//RmxController.startPkg();
		
		String recipient = "kita@name.testk.";
		ResourceBundle envBundle =ResourceBundle.getBundle("env"); 
		PropfileOperator p = new PropfileOperator();
		
//		User1 user1 = new User1();
//		user1.User1Start(recipient, dom, domain);
//		System.out.println(user1.getQuery());
		
		//User user = new User();
		//user.UserStart(recipient, dom);
		
		Distributor d = new Distributor(null, envBundle, p.getDomainsMaps());
		d.run();
		
	}
	
}
