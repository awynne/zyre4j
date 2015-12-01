package awynne.zyre.test;


import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import awynne.zyre.zyre4j.ZyreMsg;
import awynne.zyre.zyre4j.ZyreNode;

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

	private boolean receivedPing = false;
	private boolean receivedPong = false;


	@Test
	public void test() throws Exception {
		
		System.out.println("java.library.path: " + System.getProperty("java.library.path"));

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

		assertTrue(receivedPing && receivedPong);
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
			// wait for responder to join and send PING
			while (true) {
				try {
					ZyreMsg zyreMsg = zyre.recv();
					String event= zyreMsg.getEvent();
					peer = zyreMsg.getPeer();

					if (event.equals("JOIN")) {
						log.info("responder joined (" + peer + "). sending ping");
						zyre.whisper(peer, PING); 
						break;
					}
				} 
				catch (InterruptedException e) {
					break;
				}
			}

			// PING sent. Wait for PONG
			while(true) {
				try {
					ZyreMsg zyreMsg = zyre.recv();
					String event = zyreMsg.getEvent();

					if (event.equals("WHISPER")) {
						if (zyreMsg.getPayload().equals(PONG)) {
							receivedPong = true;
						}
						break;
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
			log.info("responder running");
			while(!Thread.currentThread().isInterrupted()) {
				try {
					ZyreMsg zyreMsg = zyre.recv();
					String event = zyreMsg.getEvent();
					String peer = zyreMsg.getPeer();

					if (event.equals("WHISPER")){
						String msg = zyreMsg.getPayload();
						log.info("responder received: " + msg);

						if (msg.equals(PING)) {
							receivedPing = true;
							log.info("sending pong");
							zyre.whisper(peer, PONG);
						}
						break;
					}
				} 
				catch (InterruptedException e) {
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
