package controller.impl;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import logic.Incoming;
import logic.MakeMessage;
import logic.MakeMessageSelector;
import logic.Parse;
import logic.impl.IncomingImpl;
import logic.impl.MakeMessageSelectorImpl;
import logic.impl.OpenPropFileImpl;
import logic.impl.ParseImpl;
import logic.impl.SendMailImpl;
import logic.parse.Parsable;
import controller.LogicController;
import data.Message;

/**
 * LogicControllerの実装
 */
public class LogicControllerImpl implements LogicController {

	private Socket cSocket;
	private Message oMsg;
	private ExecutorService sending;

	public LogicControllerImpl(Socket _cSocket) {
		cSocket = _cSocket;
		sending = Executors.newFixedThreadPool(Integer.parseInt(OpenPropFileImpl.getInstance().getEnvBundle().getString("max_send_socket").trim()));
	}

	@Override
	public void run() {
		this.startlogic();
	}

	private void startlogic() {
		try {
			Incoming ic = new IncomingImpl(cSocket);
			ic.getMail();
			oMsg = ic.getMessage();
			Parse parse = new ParseImpl(oMsg);
			Parsable parser = parse.getParser();

			MakeMessageSelector mms = new MakeMessageSelectorImpl();
			MakeMessage mm = mms.select(parser, parse.getDomBundle());
			List<Message> sendMessages = mm.make(oMsg, parse);
			Future<?> future = null;
			for (int i = 0; i < sendMessages.size(); i++) {
				Runnable sm = new SendMailImpl(sendMessages.get(i));
				future = sending.submit(sm);
			}
			future.get();
		} catch (IOException | ExecutionException | InterruptedException e) {
			e.printStackTrace();
		} finally {
				sending.shutdown();
		}
	}

	@Override
	public List<Message> startLogic(Message originalMsg) {
		return null;
	}

	@Override
	public Message bodyLogic(Message originalMsg) {
		return null;
	}

}
