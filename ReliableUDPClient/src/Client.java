
import java.net.*;


public class Client {
	DatagramSocket DGSock = null;
	InetAddress address = null;
	ProtogramPacket lastSent = new ProtogramPacket();
	ProtogramPacket lastReceived = new ProtogramPacket();
	DatagramPacket packetDock = null;
	private String request = null;
	private String response = "";
	byte[] bbuf;
	int port = 0;
	
	
	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public void send(){
		//usage checking
		try {
		    address = InetAddress.getByName("localhost");
		    port = Integer.parseInt("4444");
		    DGSock = new DatagramSocket();
		    DGSock.setSoTimeout(1000);
			
		} catch ( Exception e ) {
		    e.printStackTrace();
		    System.exit(-1);
		}
		
		System.out.println("client Started at port"+DGSock.getPort());
		
		bbuf = new byte[1000];
		packetDock = new DatagramPacket(bbuf, bbuf.length);
		lastSent = new ProtogramPacket(-(request.getBytes().length), 0, request.getBytes(), address, port);
		
		//guarantee delivery
	    while (true) {	       
		try {
		    DGSock.send(lastSent.asDatagram());
		    DGSock.receive(packetDock);
			lastReceived = ProtogramPacket.fromDatagram(packetDock);
		    break;
		} catch ( SocketTimeoutException e ) {
		} catch ( Exception e ) {
		    e.printStackTrace();
		    System.exit(-1);
		}
	    }
	   
	    while (lastReceived.end != -1) 
	    {
	    	if ( (lastReceived.end > 0) &&  (lastReceived.payload.length > 0) ) {
	    		response += new String(lastReceived.payload);
	    		System.out.println(response);
	    	}
	   
	    //create ACK packet
		lastSent = new ProtogramPacket(lastReceived.start,
					       lastReceived.end,
					       lastReceived.asDatagram().getAddress(),
					       lastReceived.asDatagram().getPort());
				
		//guarantee delivery
		while (true) {
		    try {
		    	DGSock.send(lastSent.asDatagram());
		    	DGSock.receive(packetDock);
		    	lastReceived = ProtogramPacket.fromDatagram(packetDock);
		    	break;
		    } catch ( SocketTimeoutException e ) {
		    } catch ( Exception e ) {
			e.printStackTrace();
		    }
		 }	    
	   }	
	}
	
	public static void main (String[] args) {
		Client client = new Client();
		client.setRequest("ProtogramPacket lastReceived = new ProtogramPacket()");
		client.send();
	}

}
