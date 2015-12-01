package awynne.zyre.zyre4j;

/**
 *  <p>A ZyreMsg is a high level representation of the message returned
 *  from a call to zyre.recv().  This simplifies the standard zyre API 
 *  in these ways: 
 *  <li>The developer does not need to know what order to pop frames off 
 *      of the ZMsg list structure. Instead, simple getter methods are 
 *      used to retrieve items in a ZyreMsg.
 *  <li>The payload of a ZyreMsg is constrained to be either a byte[] or 
 *      a String (ie, lists of ZFrame are not supported as a payload). 
 *      The choice of payload type is up to each application. The 
 *      receiver chooses which type of payload to receive using the 
 *      recv() and recvb() methods. 
 *      
 *  <p>Notes:
 *  <li>The constructor of ZyreMsg is protected to encourage the use of 
 *      MsgFactory to create each type of event
 *  <li>The user of zyre4j typically will not need to create ZyreMsg 
 *      objects, as this is handled by ZyreNode's recv methods
 *
 */
public class ZyreMsg {
	public static final String EV_ENTER = "ENTER";
	public static final String EV_JOIN = "JOIN";
	public static final String EV_LEAVE = "LEAVE";
	public static final String EV_EXIT = "EXIT";
	public static final String EV_EVASIVE = "EVASIVE";
	public static final String EV_SHOUT = "SHOUT";
	public static final String EV_WHISPER = "WHISPER";
	public static final String EV_STOP = "STOP";

	protected String event = null;
	protected String peer = "";
	protected String peerName = "";
	protected String group = "";
	
	protected Object payload = null;

	//protected String payload = "";
	//protected byte[] payloadb = new byte[0];
	
	protected ZyreMsg() {
	}

	public String getEvent() {
		return event;
	}
	public String getPeer() {
		return peer;
	}
	public String getPeerName() {
		return peerName;
	}
	public String getGroup() {
		return group;
	}
	public String getPayload() {
		return (String) payload;
	}
	public byte[] getPayloadAsBytes() {
		return (byte[]) payload;
	}
}
