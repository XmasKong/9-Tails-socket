package controller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import model.AuctionBean;
import model.AuctionList;

import org.json.simple.JSONObject;

/**
 * This is a class which represent works
 * @author XmasKong
 */
public class Request extends TimerTask {
	private String name;	//Thread name
	private int cnt;		//Thread Call Count
	private Queue<ChatServlet.ChatWebSocket> webSockets;
	
	public Request(String name, int cnt, Queue<ChatServlet.ChatWebSocket> webSockets) {
		this.name = name;
		this.cnt = cnt;
		this.webSockets = webSockets;
	}

	/**
	 * Execute Work
	 */
	public void execute() {
		//Declation Timer
		Timer timer = new Timer();
		
		//run() is called every second
		timer.schedule(new Request(name, cnt, webSockets), 1000);
	}

	/**
	 * Send AuctionList Info to Client by JSON type
	 */
	@Override
	public void run() {
		new AuctionList();
		
		Map<Integer, AuctionBean> auctionList = AuctionList.getAuctionList();
		
		JSONObject jo = new JSONObject();	
		
    	Iterator<Integer> iterator = auctionList.keySet().iterator();
    	
    	ArrayList keys = new ArrayList();
    	
    	while(iterator.hasNext()) {
			int key = iterator.next();
			AuctionBean ab = auctionList.get(key);
			
			Map<String, Object> jm = new HashMap<String, Object>();
			
			jm.put("member_id", ab.getAuction_member_id());
			jm.put("now_price", ab.getAuction_now_price());
			jm.put("duration", ab.getAuction_duration());
			
			jo.put("auc"+key, jm);
			keys.add(key);
		}
    	jo.put("auctionsize", auctionList.size());
    	jo.put("auctionkeys", keys);
    	
    	System.out.println(jo.toString());
    	
		for (ChatServlet.ChatWebSocket member : webSockets) {
		    try
		    {
		      member.connection.sendMessage(jo.toString());
		    } catch(IOException e) {
		    	e.printStackTrace();
		    }
		}
	}
}
