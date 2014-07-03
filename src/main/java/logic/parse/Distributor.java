package logic.parse;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.javac.code.Attribute.Array;

import presentation.mail.IncomingMailService;
import dao.PropfileDao;
import data.Message;
import logic.RmxController;
import logic.flow.AnswerFlow;
import logic.flow.FunctionFlow;
import logic.flow.TransferFlow;
import logic.parse.SOP.parserVisitor;
import logic.propfile.PropfileOperator;
import logic.service.ParseService;

public class Distributor implements Runnable{
	/** A outgoing Address */
	private String recipient;
	
	/***/
	private String domain;
	
	/***/
	private String subdomain;
	
	/***/
	private String propfile;

	/** name of error */
	private String typoBounce;

	/** delivery rules ex.) name, grade */
	public ArrayList<String> keys;

	/** delivery parameters */
	public ArrayList<String> values;

	/** subdomain files ex.)rmx.properties */
	private ResourceBundle domconfBundle;
	
	/**subdomain,domain,prop_file's map*/
	private ArrayList<HashMap<String, String>> domains_maps;
	
	private Socket socket;
	private ResourceBundle envBundle;
	private static final Logger log = LoggerFactory.getLogger(RmxController.class);

	/**
	 * tree parser created by obunai
	 */
	private User user;
	private User1 user1;
	private parserVisitor user_info;//User,User1の親クラス
	
	boolean transfer_flg;
	boolean answer_flg;
	boolean mixture_flg;
	boolean function_flg;
	
	//コンストラクタ
	public Distributor(Socket socket, ResourceBundle envBundle,ArrayList<HashMap<String, String>> domains_maps) {
		try {
			this.socket = socket;
			this.envBundle = envBundle;
			this.domains_maps = domains_maps;
			typoBounce = new String();
			keys = new ArrayList<String>();
			values = new ArrayList<String>();
			domain = new String();
			subdomain = new String();
			propfile = new String();
			user = new User();
			user1 = new User1();
			transfer_flg = false;
			answer_flg = false;
			mixture_flg = false;
			function_flg = false;
		} catch (NullPointerException E) {
			System.out.println("# Error: " + E.toString());
			System.exit(-1);
		} catch (MissingResourceException E) {
			System.out.println("# Error: " + E.toString());
			System.exit(-1);
		}
	}
	
	@Override
	public void run() {
		parse();
	}
	
	//宛先に応じて処理を振り分ける
	public void parse() {
		try {
			//送られてきたメールをオブジェクトとして得る
			Message oMsg = new Message();
			IncomingMailService icm = new IncomingMailService(socket);
			oMsg = icm.getMessage();
			
//			//宛先を得る
			recipient = oMsg.getRecipient();
			
			//自然形式or関数形式のどちらでパースするか決定する
			String recipient_form = ParseService.whichForm(recipient);
			//関数形式について
			if(recipient_form.equalsIgnoreCase("functional_form")) {
				domain = ParseService.getDomainOfFunctional(recipient);
				subdomain = ParseService.getSubdomainOfFunctional(recipient);
				propfile = ParseService.checkDomainAndSubdomain(domain, subdomain, domains_maps);
				domconfBundle = PropfileDao.readPropFile(propfile);
				user.UserStart(recipient, domconfBundle);
				user_info = user;
			}
			//自然形式について
			else {
				//宛先からドメインとサブドメインを切り取る
				//ex)kita@name.testk.keio.com=>subdomain=testk,domain=keio.com
				domain = ParseService.getDomainOfNatural(recipient);
				subdomain = ParseService.getSubdomainOfNatural(recipient);
				//得られたサブドメインとドメインがenv.propetiesに記述されているかチェック
				propfile = ParseService.checkDomainAndSubdomain(domain, subdomain, domains_maps);
				if(propfile!=null) 
					domconfBundle = PropfileDao.readPropFile(propfile);
				user1.User1Start(recipient, domconfBundle, domain);
				user_info = user1;
			}
			
			//user_infoにはuserもしくはuser1が格納されている
			//関数もしくは自然形式の宛先のとき
			if(user_info.getNormalFlg()) {
				transfer_flg = ParseService.checkForTransfer(user_info.getKeys(), domconfBundle);
				answer_flg = ParseService.checkForAnswer(user_info.getKeys(), domconfBundle);
				mixture_flg = ParseService.checkForMixture(user_info.getKeys(), domconfBundle);
			}
			//#形式のとき
			else if(user_info.getFunctionFlg()) {
				function_flg = true;
			}
			
			if(transfer_flg) {
				TransferFlow t_flow = new TransferFlow(oMsg, domconfBundle, envBundle, user_info);
				t_flow.startTransfer();
			}else if(answer_flg) {
				AnswerFlow a_flow = new AnswerFlow(oMsg, domconfBundle, envBundle, user_info);
				a_flow.startAnswer();
			}else if(mixture_flg) {
				
			}else if(function_flg) {
				FunctionFlow f_flow = new FunctionFlow(oMsg, envBundle, domconfBundle, user_info, propfile);
				f_flow.startFunction();
			}else {
				
			}
		}catch(Exception e) {
			
		}
	}
	
}
