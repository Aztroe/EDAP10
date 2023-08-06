package actor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ActorThread<M> extends Thread {
	
	private BlockingQueue<M> queue;
	
	public ActorThread(){
		this.queue = new LinkedBlockingQueue<M>();
	}
	
	
    /** Called by another thread, to send a message to this thread. 
     * @throws InterruptedException */
    public void send(M message) throws InterruptedException {
        queue.add(message);
    }
    
    /** Returns the first message in the queue, or blocks if none available. */
    protected M receive() throws InterruptedException {
    	M firstmessage = queue.take();
        return firstmessage;
    }
    
    /** Returns the first message in the queue, or blocks up to 'timeout'
        milliseconds if none available. Returns null if no message is obtained
        within 'timeout' milliseconds. */
    protected M receiveWithTimeout(long timeout) throws InterruptedException {
    	M firstmessage = queue.poll(timeout, TimeUnit.MILLISECONDS);
    	
        return firstmessage;
    }
}