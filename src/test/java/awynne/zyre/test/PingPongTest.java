package awynne.zyre.test;


import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import awynne.zyre.msg.MsgFactory;
import awynne.zyre.msg.ZyreMsg;
import awynne.zyre.node.ZyreNode;

/**
 * Tests the request-response pattern where a single requester sends "ping"
 * and the responder replies with "pong"
 *
 */
public class PingPongTest {

	private static final Logger log = LoggerFactory.getLogger(PingPongTest.class);
	
	public static final String PING = "ping";
	public static final String PONG = "pong";
	public static final String GROUP = "global";
	
	private Requester reqThread;
	private Responder respThread;
	
	private boolean passed = true;
	
	private MsgFactory fact = new MsgFactory();

	@Test
	public void test() throws Exception {
		
		reqThread = new Requester("requester");
		respThread = new Responder("responder");
		
		// start requester, which waits for the responder to JOIN
		reqThread.init();
		reqThread.start(); 

		// start responder
		respThread.init();
		respThread.start();

		// wait for responder to finish
		respThread.join();

		// wait for requester to finish
		reqThread.join();
		reqThread.destroy();
		respThread.destroy();

		// leave some time for resources to be freed
		try { Thread.sleep(100); } 
		catch (InterruptedException e) { e.printStackTrace(); }
		
		assertTrue(passed);
	}
	
	/**
	 *
	 */
	private class Requester extends ZyreThread {
		
		public Requester(String name) {
			super(name);
		}
		
		public void run() {
			String peer;
			// wait for responder to join
			while (true) {
				ZyreMsg zyreMsg = zyre.recv();
				String event= zyreMsg.getEvent();
				peer = zyreMsg.getPeer();

				if (event.equals("JOIN")) {
					if (peer == null) {
						log.error("Peer is null");
						passed = false;
						return;
					}
					log.info("responder joined (" + peer + ")");
					break;
				}
			}
			log.info("sending ping");
			zyre.whisper(peer, PING); 

			while(true) {
				ZyreMsg zyreMsg = zyre.recv();
				String event = zyreMsg.getEvent();
				
				if (event.equals("WHISPER")) {
					String msg = zyreMsg.getPayload();
					log.info("requester received response: " + msg);
					if (!msg.equals(PONG)) {
						log.error("Did not receive PONG.  Message was: " + msg);
						passed = false;
					}
					break;
				}
			}
		}
	}
	
	private class Responder extends ZyreThread {
		
		public Responder(String name) {
			super(name);
		}
		
		public void run() {
			log.info("responder running");
			while(!Thread.currentThread().isInterrupted()) {
				ZyreMsg zyreMsg = zyre.recv();

				String event = zyreMsg.getEvent();
				String peer = zyreMsg.getPeer();
				
				if (event.equals("WHISPER")){
					String msg = zyreMsg.getPayload();
					log.info("responder received: " + msg);
					if (!msg.equals(PING)) {
						log.error("Did not receive PING. Message was: " + msg);
						passed = false;
						break;
					}
					
					log.info("sending pong");
					zyre.whisper(peer, PONG);
					break;
				}
			}
		}
	}
	
	private class ZyreThread extends Thread {
		
		protected ZyreNode zyre;
		protected String name;
		
		public ZyreThread(String name) {
			this.name = name;
		}
		
		public void init() {
			zyre = new ZyreNode(name);
			zyre.start();
			zyre.join(GROUP);
		}
		
		public void destroy() {
			zyre.stop();
		}
	}

}
