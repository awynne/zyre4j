package awynne.zyre.test;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import awynne.zyre.zyre4j.ZyreMsg;
import awynne.zyre.zyre4j.ZyreNode;

/**
 * Tests the SHOUT-WHISPER "pattern" in which a single requester sends a 
 * number of SHOUT messages to a group and each responder in the group
 * replies with a WHISPER to the requester.
 */
public class ShoutWhisperTest {
	
	private static final Logger log = LoggerFactory.getLogger(ShoutWhisperTest.class);
	
	public static final String PING = "ping";
	public static final String PONG = "pong";
	public static final String GROUP = "global";
	
	public static final int NUM_RESP = 3;
	public static final int NUM_MSGS = 2;
	
	private Requester reqThread;
	private List<Responder> respThreads = new ArrayList<Responder>();
	
	private int expected = NUM_MSGS * NUM_RESP;
	private int totResponses = 0;

	@Test
	public void test() throws Exception {
		
		reqThread = new Requester("requester");
		
		// start requester, which waits for the responder to JOIN
		reqThread.init();
		reqThread.start(); 

		// start responders
		for (int i=0; i < NUM_RESP; i++) {
			Responder resp = new Responder("responder"+i);
			resp.init();
			resp.start();
			respThreads.add(resp);
		}

		// wait for responders to finish
		for (Responder resp : respThreads) {
			resp.join();
		}

		// wait for requester to finish
		reqThread.join();
		reqThread.destroy();

		// destroy all responders once requester is done
		for (Responder resp : respThreads) {
			resp.destroy();
		}

		// leave some time for resources to be freed
		Thread.sleep(200);
		
		log.info("received: " + totResponses + " expected: " + expected);
		assertTrue(totResponses == expected);
	}
	
	/**
	 *
	 */
	private class Requester extends ZyreThread {
		
		public Requester(String name) {
			super(name);
		}
		
		public void run() {
			int joinCt = 0;

			// wait for responders to join
			while (true) {
				try {
					ZyreMsg zyreMsg = zyre.recv();
					
					String event = zyreMsg.getEvent();
					String peer = zyreMsg.getPeer();
					String name = zyreMsg.getPeerName();

					if (event.equals("JOIN")) {
						joinCt++;
						log.info("responder joined: " + name + " (" + peer + ")");
								
						if (joinCt == NUM_RESP) {
							log.info("All responders joined: " + NUM_RESP);
							break;
						}
					}
				} 
				catch (InterruptedException e) {
					break;
				}
			}

			try { Thread.sleep(200); } 
			catch (InterruptedException e) { e.printStackTrace(); }

			// Send PINGs
			log.info("sending ping(s) via SHOUT: " + NUM_MSGS);
			for (int i=0; i < NUM_MSGS; i++) {
				zyre.shout(GROUP, PING);
			}

			// Collect response PONGs
			while(true) {
				try {
					ZyreMsg zyreMsg = zyre.recv();
					String event = zyreMsg.getEvent();
					String name = zyreMsg.getPeerName();
					
					if (event.equals("WHISPER")) {
						String msg = zyreMsg.getPayload();
						log.info("requester received response: <" + msg + "> from: " + name);

						if (msg.equals(PONG)) {
							totResponses++;
						}
						if (totResponses == expected) {
							break;
						}
					}
				} 
				catch (InterruptedException e) {
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
			int pingCt = 0;

			while(true) {
				try {
					ZyreMsg zyreMsg = zyre.recv();
					
					String event = zyreMsg.getEvent();
					String peer = zyreMsg.getPeer();
					
					if (event.equals("SHOUT")){
						String msg = zyreMsg.getPayload();
						if (!msg.equals(PING)) {
							log.error("Did not receive PING. Message was: " + msg);
							break;
						}
						
						log.info("sending pong");
						zyre.whisper(peer, PONG);
						if (++pingCt == NUM_MSGS) {
							break;
						}
					}
				} 
				catch (InterruptedException e) {
					break;
				}

			}
		}
	}
	
	private class ZyreThread extends Thread {
		
		protected String name;
		
		public ZyreThread(String name) {
			this.name = name;
		}
		
		protected ZyreNode zyre;
		
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
