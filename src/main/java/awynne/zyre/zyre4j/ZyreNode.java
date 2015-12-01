package awynne.zyre.zyre4j;

import static awynne.zyre.zyre4j.ZyreMsg.*;

import java.util.List;

import org.zeromq.czmq.ZFrame;
import org.zeromq.czmq.ZMsg;
import org.zeromq.zyre.Zyre;

/**
 * <p>Concrete implementation of a high level zyre node. 
 * Implementation Notes:
 * <li> the stop() method calls zyre.stop() and then zyre.close()
 */
public class ZyreNode implements IZyre {
	
	protected Zyre zyre;
	protected MsgFactory fact;
	
	public ZyreNode(String name) {
		zyre = new Zyre(name);
		fact = new MsgFactory();
	}

	@Override
	public boolean start() {
		return zyre.start();
	}

	@Override
	public void stop() {
		zyre.stop();
		try { 
			Thread.sleep(100);
			zyre.close();
		} 
		catch (InterruptedException e) { 
			// sleep interrupted, do nothing
		}
	}

	@Override
	public void join(String group) {
		zyre.join(group);
	}

	@Override
	public void leave(String group) {
		zyre.leave(group);
	}

	@Override
	public ZyreMsg recv() {
		return recv(true);
	}
	
	@Override
	public ZyreMsg recvb() {
		return recv(false);
	}
	
	protected ZyreMsg recv(boolean expectString) {
		ZMsg zmsg = zyre.recv();
		String event = zmsg.popstr();
		String peer = zmsg.popstr();
		String peerName = zmsg.popstr();
		String group = null;
		
		ZyreMsg zyreMsg;
		
		switch(event) {
		case EV_ENTER:
			zyreMsg = fact.createEnter(peer, peerName);
			break;
		case EV_EXIT:
			zyreMsg = fact.createExit(peer, peerName);
			break;
		case EV_JOIN:
			group = zmsg.popstr();
			zyreMsg = fact.createJoin(peer, peerName, group);
			break;
		case EV_LEAVE:
			group = zmsg.popstr();
			zyreMsg = fact.createLeave(peer, peerName, group);
			break;
		case EV_SHOUT:
			zyreMsg = this.createShout(expectString, peer, peerName, zmsg);
			break;
		case EV_WHISPER:
			zyreMsg = this.createWhisper(expectString, peer, peerName, zmsg);
			break;
		default:
			throw new RuntimeException("Unknown event type: " + event);
		}
		zmsg.close();
		
		return zyreMsg;
	}
	
	private ZyreMsg createShout(boolean expectString, String peer, String peerName, ZMsg zmsg) {
		ZyreMsg zyreMsg;
		String group = zmsg.popstr();

		if (expectString) {
			String payload = zmsg.popstr();
			zyreMsg = fact.createShout(peer, peerName, group, payload);
		}
		else {
			ZFrame zframe = zmsg.pop();
			byte[] payload = zframe.data();
			zyreMsg = fact.createShout(peer, peerName, group, payload);
		}

		return zyreMsg;

	}
	
	private ZyreMsg createWhisper(boolean expectString, String peer, String peerName, ZMsg zmsg) {
		ZyreMsg zyreMsg;
		
		if (expectString) {
			String payload = zmsg.popstr();
			zyreMsg = fact.createWhisper(peer, peerName, payload);
		}
		else {
			ZFrame zframe = zmsg.pop();
			byte[] payload = zframe.data();
			zyreMsg = fact.createWhisper(peer, peerName, payload);
		}
		
		return zyreMsg;
	}

	@Override
	public void shout(String group, String payload) {
		zyre.shouts(group, payload);
	}

	@Override
	public void shout(String group, byte[] payload) {
		ZFrame zframe = new ZFrame(payload);
		ZMsg zmsg = new ZMsg();
		zmsg.append(zframe);
		zyre.shout(group, zmsg);
	}

	@Override
	public void whisper(String peer, String payload) {
		zyre.whispers(peer, payload);
	}

	@Override
	public void whisper(String peer, byte[] payload) {
		ZFrame zframe = new ZFrame(payload);
		ZMsg zmsg = new ZMsg();
		zmsg.append(zframe);
		zyre.whisper(peer, zmsg);
	}

	@Override
	public String name() {
		return zyre.name();
	}

	@Override
	public List<String> peerGroups() {
		throw new UnsupportedOperationException();
//		ArrayList<String> groups = new ArrayList<>();
//		ZList peerZlist = zyre.peerGroups();
		// TODO: populate List object with groups from Zlist
//		return groups;
	}

	@Override
	public List<String> ownGroups() {
		throw new UnsupportedOperationException();
		/*
		ArrayList<String> groups = new ArrayList<>();
		ZList peerZlist = zyre.ownGroups();
		// TODO: populate List object with groups from Zlist
		return groups;
		*/
	}

	@Override
	public List<String> peers() {
		throw new UnsupportedOperationException();
		/*
		ArrayList<String> peers = new ArrayList<>();
		ZList peerZlist = zyre.peers();
		// TODO: populate List object with groups from Zlist
		return peers;
		*/
	}

	@Override
	public void setInterval(long intervalMs) {
		zyre.setInterval(intervalMs);

	}

	@Override
	public void setPort(int port) {
		zyre.setPort(port);
	}

	@Override
	public void setInterface(String intf) {
		zyre.setInterface(intf);
	}
}
