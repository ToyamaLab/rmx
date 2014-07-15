package data;

import java.util.ArrayList;

public class Message {
	private String sender;
	private String recipient;
	private String subject;
	private ArrayList<String> header;
	private ArrayList<String> body;
	
	//コンストラクタ
	public Message() {
		this.sender = null;
		this.recipient = null;
		this.subject  = null;
		this.header = new ArrayList<String>();
		this.body = new ArrayList<String>();
	}
	
	public void setSender(String sender) {
		this.sender = extractAddress(sender);
	}
	
	public void setRecipient(String recipient) {
		this.recipient = extractAddress(recipient);
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public void addHeader(String header) {
		this.header.add(header);
	}
	
	public void addBody(String body) {
		this.body.add(body);
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getRecipient() {
		return recipient;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public ArrayList<String> getHeader(){
		return header;
	}
	
	public ArrayList<String> getBody(){
		return body;
	}
	
	public void initiate() {
		this.sender = null;
		this.recipient = null;
		this.subject  = null;
		this.header.clear();
		this.body.clear();
	}
	
	public void clearSender() {
		this.sender  = null;
	}
	
	public void clearRecipient() {
		this.recipient = null;
	}
	
	public void clearSubject() {
		this.subject = null;
	}
	
	public void clearHeader() {
		this.header.clear();
	}
	
	public void clearBody() {
		this.body.clear();
	}
	
	private String extractAddress(String s) {

		int i, j;
		String address;

		// The e-mail address is enclosed within a pair of angle brackets.
		if ((s.indexOf('<') > -1) && (s.indexOf('>') > -1)) {
			i = s.indexOf('<') + 1;
			j = s.indexOf('>');
			address = s.substring(i, j);
			return address;

		// The e-mail address is as is.
		} else {
			i = s.indexOf(':');
			address = s.substring(i + 1);
			return address.trim();
		}
	}
}
