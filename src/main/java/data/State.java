package data;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.slf4j.*;

 public class State{
	//
	private String state;
	//
	private Socket connSocket;
	//
	private String serverName;
	//
	private String serverAddress;
	//
	private String clientName;
	//
	private String clientAddress;
	//
	private DateFormat date;
	
	private static final Logger log = LoggerFactory.getLogger(State.class);
	//コンストラクタ
	public State(Socket _connSocket) {
		try {
			connSocket = _connSocket;
		
			serverName = InetAddress.getLocalHost().getHostName();
			serverAddress = InetAddress.getLocalHost().getHostAddress();
			
			clientName = connSocket.getInetAddress().getHostName();
			clientAddress = connSocket.getInetAddress().getHostAddress();
			
			date = new SimpleDateFormat("d MMM yyyy HH:mm:ss Z", Locale.US);
		} catch(UnknownHostException e) {
			log.error("# Error: {}", e.toString());
			System.exit(-1);
		} catch(Exception e) {
			log.error("# Error: {}", e.toString());
			System.exit(-1);
		}
	}

	public void setState(String _state) {
		state = _state;
	}

	public String getState() {
		return state;
	}

	public String getServerName() {
		return serverName;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public String getClientName() {
		return clientName;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public DateFormat getDate() {
		return date;
	}
}
