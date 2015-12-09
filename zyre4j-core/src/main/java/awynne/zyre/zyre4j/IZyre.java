package awynne.zyre.zyre4j;

import java.util.List;

/**
 * High level Java interface for a Zyre node.  The main difference 
 * from pure zyre is that in zyre4j payloads are assumed to only 
 * have a single part that is either a String or byte[].  Notes:
 * <li> This interface provides a higher level abstraction to 
 *      <a href="https://github.com/zeromq/zyre/blob/master/bindings/jni/src/main/java/org/zeromq/zyre/Zyre.java">Zyre.java</a>
 * <li> Documentation for zyre can be found in 
 *      <a href="https://github.com/zeromq/zyre/blob/master/src/zyre.c>zyre.c</a> 
 * <li> ZRE Protocol is documented <a href="http://rfc.zeromq.org/spec:20">here</>
 * <li> java.util.List objects are used for simplicity to 
 *      return collections of groups and peers
 */
public interface IZyre {
	
	/** Start a node to begin discovery and connection process */
	public boolean start();
	
	/** Send stop message, terminate, and close resources */
	public void stop();

	/** Join the named group */
	public void join(String group);

	/** Leave the named group */
	public void leave(String group);
	
	/**
	 *  Receive a ZyreMsg containing a String payload . Each app decides
	 *  whether to exchange byte[] or String payloads
	 * @throws InterruptedException if zyre-jni is interrupted during zyre.recv
	 */
	public ZyreMsg recv() throws InterruptedException;
	
	/** 
	 * Receive a ZyreMsg containing a byte[] payload. Each application decides
	 * whether it will exchange byte[] or string payloads.
	 * @throws InterruptedException if zyre-jni is interrupted during zyre.recv
	 */
	public ZyreMsg recvb() throws InterruptedException;

	/** 
	 * Multicast send to the named group 
	 * @param group Group name
	 * @param payload Payload as string
	 */
	public void shout(String group, String payload);

	/**
	 * Multicast send to the named group
	 * @param group Group name
	 * @param payload Payload in bytes
	 */
	public void shout(String group, byte[] payload);
	
	/**
	 * Unicast send to the peer with the specified ID
	 * @param peer Peer ID
	 * @param payload Payload as string
	 */
	public void whisper(String peer, String payload);
	
	/**
	 * Unicast send to the peer with the specified ID
	 * @param peer Peer ID
	 * @param payload Payload in bytes
	 */
	public void whisper(String peer, byte[] payload);
	
	/** Return the name of this zyre node */
	public String name();

	/** Return list of groups known through connected peers */
	public List<String> peerGroups();
	
	/** Return list of currently joined groups */
	public List<String> ownGroups();
	
	/** Return list of current peers */
	public List<String> peers();
	
	/** 
	 * Set interval between sends of discovery beacon. 
	 * @param intervalMs Interval, in ms. Default is 1000 ms.
	 */
	public void setInterval(long intervalMs);
	
	/**
	 * Set port on which to receive discovery beacon.
	 * @param port UDP port to listen on
	 */
	public void setPort(int port);
	
	/**
	 * Set network interface for UDP beacons. If not set, the interface 
	 * is automatically chosen, which will cause problems on a machine with 
	 * multiple interfaces
	 * @param intf Name of the interface
	 */
	public void setInterface(String intf);
	
	/**
	 * Return the IP  address of the specified peer
	 * @param peer Peer ID
	 * @return the IP address
	 */
	public String peerAddress(String peer);
	
	/**
	 * Return the header value of the specified peer
	 * @param peer Peer ID
	 * @param key Key for the particular header
	 * @return the Value for the specified key
	 */
	public String peerHeaderValue(String peer, String key);
	
	/**
	 * Set the header value for this node
	 * @param key key for the header to set
	 * @param value value of this header item
	 */
	public void setHeader(String key, String value);
	
	/**
	 * Return our UUID, aka Peer ID
	 * @return
	 */
	public String uuid();
}

