package controller;
import java.util.Queue;

/**
 * Thread takes request from client
 * and then deliver the request to channel
 * @author XmasKong
 *
 */
public class ClientThread extends Thread {
	private String name = null;
	private Channel channel = null;		//The Channel delivers the request
	private Queue<ChatServlet.ChatWebSocket> webSockets;
	
	public ClientThread(String name, Channel channel, Queue<ChatServlet.ChatWebSocket> webSockets) {
		this.name = name;
		this.channel = channel;			//Set Channel
		this.webSockets = webSockets;
	}
	
	public void run() {
		try {
			for (int i = 0; true; i++) {
				//Assign a work to the Request
				Request request = new Request(name, i, webSockets);
				//Request to Channel
				channel.putRequest(request);
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
