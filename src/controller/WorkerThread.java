package controller;
/**
 * This is a class which deals with work from channel
 * @author XmasKong
 */
public class WorkerThread extends Thread {
	private Channel channel;
	
	public WorkerThread(String name, Channel channel) {
		super(name);				
		this.channel = channel;
	}
	
	public void run( ) {
        while(true) {
        	//Get Work from Channel
            Request request = channel.takeRequest( );
            //Execute Work
            request.execute( );
        }
    }
}