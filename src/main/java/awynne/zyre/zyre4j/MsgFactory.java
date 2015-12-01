package awynne.zyre.zyre4j;

import static awynne.zyre.zyre4j.ZyreMsg.*;

/**
 * Used to create ZyreMsg objects for each type of Zyre event.
 * Use this instead of calling the ZyreMsg constructor
 */
public class MsgFactory {
	
	public ZyreMsg createEnter(String peer, String peerName) {
		ZyreMsg msg = new ZyreMsg();
		msg.event = EV_ENTER;
		msg.peer = peer;
		msg.peerName = peerName;
		return msg;
	}

	public ZyreMsg createEvasive(String peer, String peerName) {
		ZyreMsg msg = new ZyreMsg();
		msg.event = EV_EVASIVE;
		msg.peer = peer;
		msg.peerName = peerName;
		return msg;
	}

	public ZyreMsg createExit(String peer, String peerName) {
		ZyreMsg msg = new ZyreMsg();
		msg.event = EV_EXIT;
		msg.peer = peer;
		msg.peerName = peerName;
		return msg;
	}
	
	public ZyreMsg createStop(String peer, String peerName) {
		ZyreMsg msg = new ZyreMsg();
		msg.event = EV_STOP;
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
	
	public ZyreMsg createShout(String peer, String peerName, String group, String payloadStr) {
		return createShoutObj(peer, peerName, group, payloadStr);
	}

	public ZyreMsg createShout(String peer, String peerName, String group, byte[] payloadBytes) {
		return createShoutObj(peer, peerName, group, payloadBytes);
	}
	
	public ZyreMsg createWhisper(String peer, String peerName, String payloadStr) {
		return createWhisperObj(peer, peerName, payloadStr);
	}
	
	public ZyreMsg createWhisper(String peer, String peerName, byte[] payloadBytes) {
		return createWhisperObj(peer, peerName, payloadBytes);
	}

	private ZyreMsg createShoutObj(String peer, String peerName, String group, Object payload) {
		ZyreMsg msg = new ZyreMsg();
		msg.event = EV_SHOUT;
		msg.peer = peer;
		msg.peerName = peerName;
		msg.group = group;
		msg.payload = payload;
		return msg;
	}

	private ZyreMsg createWhisperObj(String peer, String peerName, Object payload) {
		ZyreMsg msg = new ZyreMsg();
		msg.event = EV_WHISPER;
		msg.peer = peer;
		msg.peerName = peerName;
		msg.payload = payload;
		return msg;
	}

}
