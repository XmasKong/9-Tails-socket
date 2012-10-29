package controller;
/**
 * This is a class which divides channel deal with request
 * @author XmasKong
 *
 */
public class Channel {
	private static final int MAX_REQUEST = 100;	//Request Queue Max size
	private Request[] requestQueue;				//Request Queue List
	private int tail;							//Queue End(If nothing queue, tail is 0)
	private int head;							//Queue Start
	private int count;							//Request Count
	private WorkerThread[] threadPool;			//Thread Pool
	
	/**
	 * Initialize
	 */
	public Channel(int threads) {
		this.requestQueue = new Request[MAX_REQUEST];
		this.head = 0;
		this.tail = 0;
		this.count = 0;
		//Create Worker Thread
		this.threadPool = new WorkerThread[threads];
		for(int i = 0; i < threadPool.length; i++) {
			threadPool[i] = new WorkerThread("Worker - " + i , this);
		}
	}
	
	/**
	 * Start Worker Thread
	 */
	public void startWorkers() {
		for(int i = 0; i < threadPool.length; i++) {
			threadPool[i].start();
		}
	}
	
	/**
	 * Put Request Method(ClientThread calls every second)
	 * @param request
	 */
	public synchronized void putRequest(Request request) {
		//If request count is more than Queue List, waiting
		//Queue is fulled with request
		while(count >= requestQueue.length) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//If queue has space,
		//add request Queue List
		requestQueue[tail] = request;
        tail = (tail+1) % requestQueue.length;
        count++ ;
        
        //Notify All Threads
        notifyAll( );
	}

	/**
	 * Deal with Request message(WorkThread calls regulary)
	 * @return
	 */
	public synchronized Request takeRequest() {
		//If nothing request, waiting
		while(count <= 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Request request = requestQueue[head];
		//head move
		//if head is 99(end), head initializes 0
		//head = (99+1) % 100 = 0
		head = (head+1) %  requestQueue.length; 
		
		//execute process delete
		count--;
		
		//Notify All Threads
		notifyAll();
		
		return request;
	}
}