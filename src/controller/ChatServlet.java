package controller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.AuctionList;

import org.apache.derby.impl.sql.catalog.SYSPERMSRowFactory;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketFactory;
import org.eclipse.jetty.websocket.WebSocketServlet;

/**
 * WebSocket Servlet Class
 * @author XmasKong
 *
 */
@WebServlet(urlPatterns = "/chat", asyncSupported = true)
public class ChatServlet extends WebSocketServlet
{
	private WebSocketFactory _wsFactory;
	private final Set _members = new CopyOnWriteArraySet();
	private Channel channel = null;
	
	/**
	 * Initialize Channel and Start Threads
	 */
	@Override
	public void init() throws ServletException {
		channel = new Channel(5);
    	channel.startWorkers();
    	
    	new ClientThread("bid", channel, webSockets).start();
	}

	/**
	 * Connect Websocket
	 */
    @Override
    public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
        return new ChatWebSocket();
    }

    private Queue<ChatWebSocket> webSockets = new ConcurrentLinkedQueue<ChatWebSocket>();
    class ChatWebSocket implements WebSocket.OnTextMessage{
		Connection connection;
		
		@Override
		public void onOpen(Connection connection) {
		        this.connection = connection;
		        _members.add(this);
		        webSockets.add(this);
		}
		
		@Override
		public void onClose(int closeCode, String message) {
		        webSockets.remove(this);
		        _members.remove(this);
		}
		
		@Override
		public void onMessage(String msg) {
			//If request AuctionList update from browser
			if(msg.equals("update")) {
				AuctionList al = new AuctionList();
				al.refreshAuction();
			}
			for (ChatWebSocket member : webSockets) {
		        try {
		          member.connection.sendMessage(msg);
		          
		        } catch(IOException e) {
		        	e.printStackTrace();
		        }
		    }
		}
    }
}