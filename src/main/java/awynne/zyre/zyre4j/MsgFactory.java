package awynne.zyre.zyre4j;

import static awynne.zyre.zyre4j.ZyreMsg.*;

/**
 * Used to create ZyreMsg objects for each type of 
 * Zyre event
 */
public class MsgFactory {
	
	public ZyreMsg createEnter(String peer, String peerName) {
		ZyreMsg msg = new ZyreMsg();
		msg.event = EV_ENTER;
		msg.peer = peer;
		msg.peerName = peerName;
		return msg;
	}
	
	public ZyreMsg createJoin(String peer, String peerName, String group) {
		ZyreMsg msg = new ZyreMsg();
		msg.event = EV_JOIN;
		msg.peer = peer;
		msg.peerName = peerName;
		msg.group = group;
		return msg;
	}
	
	public ZyreMsg createLeave(String peer, String peerName, String group)  {
		ZyreMsg msg = new ZyreMsg();
		msg.event = EV_LEAVE;
		msg.peer = peer;
		msg.peerName = peerName;
		msg.group = group;
		return msg;
	}
	
	public ZyreMsg createExit(String peer, String peerName) {
		ZyreMsg msg = new ZyreMsg();
		msg.event = EV_EXIT;
		msg.peer = peer;
		msg.peerName = peerName;
		return msg;
	}
	
	public ZyreMsg createShout(String peer, String peerName, String group, String payload) {
		ZyreMsg msg = new ZyreMsg();
		msg.event = EV_SHOUT;
		msg.peer = peer;
		msg.peerName = peerName;
		msg.group = group;
		msg.payload = payload;
		return msg;
	}

	public ZyreMsg createShout(String peer, String peerName, String group, byte[] payload) {
		ZyreMsg msg = new ZyreMsg();
		msg.event = EV_SHOUT;
		msg.peer = peer;
		msg.peerName = peerName;
		msg.group = group;
		msg.payloadb = payload;
		return msg;
	}
	
	public ZyreMsg createWhisper(String peer, String peerName, String payload) {
		ZyreMsg msg = new ZyreMsg();
		msg.event = EV_WHISPER;
		msg.peer = peer;
		msg.peerName = peerName;
		msg.payload = payload;
		return msg;
	}
	
	public ZyreMsg createWhisper(String peer, String peerName, byte[] payload) {
		ZyreMsg msg = new ZyreMsg();
		msg.event = EV_WHISPER;
		msg.peer = peer;
		msg.peerName = peerName;
		msg.payloadb = payload;
		return msg;
	}
	
}
