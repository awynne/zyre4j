package awynne.zyre.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import awynne.zyre.zyre4j.ZyreMsg;
import awynne.zyre.zyre4j.ZyreNode;

import static org.junit.Assert.*;

public class SendByteTest {
	
	private static final Logger log = LoggerFactory.getLogger(SendByteTest.class);
	
	public static final String TEXT = "hey hey my my";
	public static final String GROUP = "GLOBAL";
	
	private static boolean passed = false;
	private static String node2UUID;

	@Test
	public void shoutByte() throws Exception {
		send(true);
	}
	
	@Test
	public void whisperByte() throws Exception {
		send(false);
	}
	
	private void send(boolean shout) throws InterruptedException {
		
		Node2Thread n2Thread = new Node2Thread();
		n2Thread.start();
		
		ZyreNode node1 = new ZyreNode("node1");
		node1.start();
		Thread.sleep(100);

		byte[] payload = Compressor.compress(TEXT);
		log.info("sending: " + TEXT);
		
		if (shout) {
			node1.shout(GROUP, payload);
		}
		else {
			node1.whisper(node2UUID, payload);
		}
		
		n2Thread.join(2000);
		
		node1.stop();
		assertTrue(passed);
	}
	
	public class Node2Thread extends Thread {

		private ZyreNode n2 = new ZyreNode("node2");

		public void run() {

			n2.join(GROUP);
			n2.start();
			node2UUID = n2.uuid();

			while(true) {
				try {
					ZyreMsg msg = n2.recvb();
					String ev = msg.getEvent();
					
					if (ev.equals(ZyreMsg.EV_SHOUT) || ev.equals(ZyreMsg.EV_WHISPER)) {
						byte[] payload = msg.getPayloadBytes();
						
						String str = Compressor.decompress(payload);
						log.info("received: "  + str);
						passed = str.equals(TEXT);
						break;
					}
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			n2.stop();
		}
	}
}
