package awynne.zyre.examples;

import static java.lang.System.*;
import static awynne.zyre.zyre4j.ZyreMsg.*;
import java.util.Scanner;

import awynne.zyre.zyre4j.ZyreMsg;
import awynne.zyre.zyre4j.ZyreNode;

/** 
 * This thread will listen to and publish anything received on the 
 * CHAT group
 */
public class Chat extends Thread {

	public static final String CHAT_GRP = "CHAT";

	private ZyreNode node;

	private boolean terminated = false;

	public Chat(String name) {
		node = new ZyreNode(name);
	}

	public Chat(String name, String intf) {
		this(name);
		node.setInterface(intf);
	}

	public void run() {
		if (node == null) {
			throw new RuntimeException("Could not create ZyreNode");
		}
		node.start();
		node.join(CHAT_GRP);

		while(!terminated) {
			try {
				ZyreMsg	msg = node.recv();

				String event = msg.getEvent();
				String name = msg.getPeerName();
				String group = msg.getGroup();

				if (event.equals(EV_JOIN) && group.equals(CHAT_GRP)) {
					out.printf ("%s has joined the chat\n", name);
				}
				else if (event.equals(EV_LEAVE)) {
					out.printf ("%s has left the chat\n", name);
				}
				else if(event.equals(EV_SHOUT)) {
					out.printf ("%s: %s\n", name, msg.getPayload());
				}
				else {
					// not handling events: WHISPER, ENTER, EXIT, EVASIVE, STOP
				}
			} 
			catch (InterruptedException e) {
				break;  // interrupted during zyre.recv()
			}
		}
		out.printf("Leaving %s and stopping zyre", CHAT_GRP);
	}

	public void shout(String text) {
		node.shout(CHAT_GRP, text);
	}

	public void terminate() {
		terminated = true;
		node.leave(CHAT_GRP);
		node.stop();
	}

	public static void main(String[] args) throws Exception {

		if (args.length < 1) {
			out.println("syntax: chat myname | chat myname interface");
			exit(0);
		}
		Chat chat;
		if (args.length == 1) {
			chat = new Chat(args[0]);
		}
		else {
			chat = new Chat(args[0], args[1]);
		}

		chat.start();
		Scanner scanner = new Scanner(in);

		while(true) {
			out.print("Enter message: ");
			String text = scanner.nextLine();
			if (text.toLowerCase().equals("exit")) 
				break;

			chat.shout(text);
		}
		scanner.close();
		chat.terminate();
		chat.join();
	}

}
