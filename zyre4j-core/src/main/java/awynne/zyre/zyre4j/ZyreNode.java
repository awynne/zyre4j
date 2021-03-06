package awynne.zyre.zyre4j;

import static awynne.zyre.zyre4j.ZyreMsg.*;

import java.util.ArrayList;
import java.util.List;

import org.zeromq.czmq.Zframe;
import org.zeromq.czmq.Zlist;
import org.zeromq.czmq.Zmsg;
import org.zeromq.czmq.Zstr;
import org.zeromq.zyre.Zyre;

/**
 * <p>Concrete implementation of a high level zyre node. 
 * Implementation Notes:
 * <li> the stop() method calls zyre.stop() and then zyre.close()
 */
public class ZyreNode implements IZyre {
	
	protected Zyre zyre;
	protected MsgFactory fact;
	
	protected long ptr = Long.MAX_VALUE;
	
	public ZyreNode(String name) {
		zyre = new Zyre(name);
		fact = new MsgFactory();
	}

	@Override
	public boolean start() {
		int ret = zyre.start();
		return (ret == 0);
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
	public ZyreMsg recv() throws InterruptedException {
		return recv(true);
	}
	
	@Override
	public ZyreMsg recvb() throws InterruptedException {
		return recv(false);
	}
	
	/**
	 * Receive a ZyreMsg, block if none 
	 * @param expectString True if the caller expects the payload
	 * to be a String, false if byte[] payload is expected
	 * @return The received ZyreMsg or null if interrupted
	 */
	protected ZyreMsg recv(boolean expectString) throws InterruptedException {
		Zmsg zmsg = zyre.recv();
		if (zmsg == null) {
			throw new InterruptedException("Interrupted during zyre.recv()");
		}

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
		case EV_STOP:
			zyreMsg = fact.createStop(peer, peerName);
			break;
		case EV_EVASIVE:
			zyreMsg = fact.createEnter(peer, peerName);
			break;
		default:
			throw new RuntimeException("Unknown event type: " + event);
		}
		zmsg.close();
		
		return zyreMsg;
	}
	
	private ZyreMsg createShout(boolean expectString, String peer, String peerName, Zmsg zmsg) {
		ZyreMsg zyreMsg;
		String group = zmsg.popstr();

		if (expectString) {
			String payload = zmsg.popstr();
			zyreMsg = fact.createShout(peer, peerName, group, payload);
		}
		else {
			Zframe zframe = zmsg.pop();
			byte[] payload = zframe.data();
			zyreMsg = fact.createShout(peer, peerName, group, payload);
		}

		return zyreMsg;

	}
	
	private ZyreMsg createWhisper(boolean expectString, String peer, String peerName, Zmsg zmsg) {
		ZyreMsg zyreMsg;
		
		if (expectString) {
			String payload = zmsg.popstr();
			zyreMsg = fact.createWhisper(peer, peerName, payload);
		}
		else {
			Zframe zframe = zmsg.pop();
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
		Zframe zframe = new Zframe(payload, payload.length);
		Zmsg zmsg = new Zmsg();
		zmsg.append(zframe);
		zyre.shout(group, zmsg);
	}

	@Override
	public void whisper(String peer, String payload) {
		zyre.whispers(peer, payload);
	}

	@Override
	public void whisper(String peer, byte[] payload) {
		Zframe zframe = new Zframe(payload, payload.length);
		Zmsg zmsg = new Zmsg();
		zmsg.append(zframe);
		zyre.whisper(peer, zmsg);
	}

	@Override
	public String name() {
		return zyre.name();
	}

	@Override
	public List<String> peerGroups() {
		return toStringList( zyre.peerGroups() );
	}

	@Override
	public List<String> ownGroups() {
		return toStringList( zyre.ownGroups() );
	}

	@Override
	public List<String> peers() {
		return toStringList( zyre.peers()) ;
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

	@Override
	public String peerAddress(String peer) {
		return zyre.peerAddress(peer);
	}

	@Override
	public String peerHeaderValue(String peer, String key) {
		return zyre.peerHeaderValue(peer, key);
	}

	@Override
	public void setHeader(String key, String value) {
		zyre.setHeader(key, value);
	}

	@Override
	public String uuid() {
		return zyre.uuid();
	}
	
	private List<String> toStringList(Zlist zlist) {
		ArrayList<String> jlist = new ArrayList<>();
		
		long ptr = zlist.first();
		while (ptr > 0) {
			String str = new Zstr().str(ptr);
			jlist.add(str);
			ptr = zlist.next();
		}
		
		return jlist;
	}
}
