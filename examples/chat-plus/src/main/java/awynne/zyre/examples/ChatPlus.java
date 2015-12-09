package awynne.zyre.examples;

import static java.lang.System.*;

import java.util.ArrayList;

import static awynne.zyre.zyre4j.ZyreMsg.*;
import java.util.Scanner;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.TreeBidiMap;

import awynne.zyre.zyre4j.ZyreMsg;
import awynne.zyre.zyre4j.ZyreNode;

/** 
 * This thread will listen to and publish anything received on the 
 * CHAT group
 */
public class ChatPlus extends Thread {

	public static final String DEFAULT_GRP = "WAITING";

	public static final String SVC_KEY = "service";
	public static final String SVC_VAL = "chat";

	private ZyreNode node;

	private boolean terminated = false;
	
	// Bidirectional map: peerName --> peerId
	private BidiMap peers = new TreeBidiMap();

	public ChatPlus(String name) {
		node = new ZyreNode(name);
		// use header to advertise capabilities
		node.setHeader(SVC_KEY, SVC_VAL);
	}

	public ChatPlus(String name, String intf) {
		this(name);
		node.setInterface(intf);
	}

	public void run() {
		if (node == null) {
			throw new RuntimeException("Could not create ZyreNode");
		}
		node.start();
		node.join(DEFAULT_GRP);
		
		out.println("my name: " + node.name());
		out.println("my uuid: " + node.uuid());
		out.println("my groups: " + node.ownGroups());
		
		while(!terminated) {
			try {
				ZyreMsg	msg = node.recv();

				String event = msg.getEvent();
				String name = msg.getPeerName();
				String peer = msg.getPeer();
				String group = msg.getGroup();

				if (event.equals(EV_ENTER)) {
					peers.put(name, peer);
				}
				else if (event.equals(EV_JOIN)) { 
					// notify user that a peer has joined group if this node is in the same group
					if (node.ownGroups().contains(group)) { 
						String service = node.peerHeaderValue(peer, SVC_KEY);
						if (service != null && service.equals(SVC_VAL)) {
							out.printf ("%s has joined room: %s\n", name, group);
						}
						else{
							err.printf("peer hosts unknown service: " + service);
						}
					}
				}
				else if (event.equals(EV_LEAVE)) {
					out.printf ("%s has left room: %s\n", name, group);
				}
				else if(event.equals(EV_SHOUT)) {
					out.printf ("%s shouted to %s: %s\n", name, group, msg.getPayload());
				}
				else if(event.equals(EV_WHISPER)) {
					out.printf ("%s whispered: %s\n", name, msg.getPayload());
				}
				else if(event.equals(EV_EXIT)) {
					peers.remove(name);
				}
				else {
					// not handling events: WHISPER, ENTER, EXIT, EVASIVE, STOP
				}
			} 
			catch (InterruptedException e) {
				break;  // interrupted during zyre.recv()
			}
		}
		out.printf("Exiting chat");
	}

	public void shout(String group, String text) {
		node.shout(group, text);
	}
	
	public void whisper(String peer, String text) {
		node.whisper(peer, text);
	}

	public void terminate() {
		terminated = true;
		node.stop();
	}

	public static void main(String[] args) throws Exception {

		if (args.length < 1) {
			out.println("syntax: chat-plus myname | chat myname interface");
			exit(0);
		}
		ChatPlus chat;
		if (args.length == 1) {
			chat = new ChatPlus(args[0]);
		}
		else {
			chat = new ChatPlus(args[0], args[1]);
		}

		chat.start();
		chat.processInput();
		chat.join();
	}
	
	private void processInput() {
		boolean processing = true;
		Scanner scanner = new Scanner(in);

		while(processing) {
			out.print("Enter message: ");
			String line = scanner.nextLine();
			String[] parts = line.split("\\s+", 3);

			if (parts == null || parts.length < 1) {
				continue; // empty line
			}

			processing = handleCommand(parts);
		}
		scanner.close();
		terminate();
	}
	
	private boolean handleCommand(String[] parts) {
		String cmd = parts[0];
		switch(cmd) {
			case "exit":
				return false;
			case "shout":
				if (parts.length < 3) {
					err.println("USAGE: shout group text");
				}
				else {
					shout(parts[1], parts[2]);
				}
				break;
			case "whisper":
				if (parts.length < 3) {
					err.println("USAGE: whisper peer text");
				}
				else {
					String peerId = (String) peers.get(parts[1]);
					whisper(peerId, parts[2]);
				}
				break;
			case "rooms":
				out.println(node.name() + " is a member of these rooms: " + node.ownGroups());
				break;
			case "peers":
				ArrayList<String > list = new ArrayList<>();
				for (String id : node.peers()) {
					String name = (String) peers.getKey(id);
					list.add(name);
				}
				out.println(node.name() + " has peers: " + list);
			case "join":
				if (parts.length < 2)  {
					err.println("USAGE: join GROUP");
				}
				else {
					node.join(parts[1]);
				}
				break;
			default:
				err.println("unkown command: " + cmd);
				break;
		}
		return true;
	}
}
