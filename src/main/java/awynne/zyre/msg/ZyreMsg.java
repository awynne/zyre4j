package awynne.zyre.msg;

public class ZyreMsg {
	public static final String EV_ENTER = "ENTER";
	public static final String EV_JOIN= "JOIN";
	public static final String EV_LEAVE = "LEAVE";
	public static final String EV_EXIT = "EXIT";

	public static final String EV_SHOUT = "SHOUT";
	public static final String EV_WHISPER = "WHISPER";

	protected String event = null;
	protected String peer = "";
	protected String peerName = "";
	protected String group = "";

	protected String payload = "";
	protected byte[] payloadb = new byte[0];
	
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
		return payload;
	}
	public byte[] getPayloadb() {
		return payloadb;
	}
}
