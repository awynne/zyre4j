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
	
	/** Stop node and close its resources */
	public void stop();

	/** Join the named group */
	public void join(String group);

	/** Leave the named group */
	public void leave(String group);
	
	/** Receive a ZyreMsg containing a String payload 
	 * @throws InterruptedException */
	public ZyreMsg recv() throws InterruptedException;
	
	/** Receive a ZyreMsg containing a byte[] payload 
	 * @throws InterruptedException */
	public ZyreMsg recvb() throws InterruptedException;

	/** 
	 * Multicast send to the named group 
	 * @param group
	 * @param payload Payload as string
	 */
	public void shout(String group, String payload);

	/**
	 * Multicast send to the named group
	 * @param group
	 * @param payload Payload in bytes
	 */
	public void shout(String group, byte[] payload);
	
	/**
	 * Unicast send to the peer with the specified ID
	 * @param peer
	 * @param payload Payload as string
	 */
	public void whisper(String peer, String payload);
	
	/**
	 * Unicast send to the peer with the specified ID
	 * @param peer
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
}

