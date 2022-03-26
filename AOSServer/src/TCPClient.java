import java.io.*;
import java.net.*;

public class TCPClient extends Thread{

	String serverHostName, clientHostName;
	private int serverPortNumber, UID, serverUID;
	private String clientUID;
	private Socket clientsocket;
	ObjectInputStream in;
	ObjectOutputStream out;
    Node dsNode;
    boolean flag;
    
	public TCPClient(int UID, int serverPort, String serverHostName, String clientHostName, int serverUID , Node _dsNode) {
		this.serverHostName = serverHostName;
		this.serverPortNumber = serverPort;
		this.UID = UID;
		this.clientHostName = clientHostName;
		this.clientUID = Integer.toString(serverUID);
		this.serverUID = serverUID;
		this.dsNode = _dsNode;
		this.flag = true;
	}
	
	TCPClient(Socket client, Node dsNode) {
		this.clientsocket = client;
		this.dsNode = dsNode;
	}

	public Socket getClientSocket() {
		return this.clientsocket;
	}

	public int getClientUID() {
		return Integer.parseInt(this.clientUID);
	}

	public ObjectInputStream getInputReader() {
		return this.in;
	}

	public ObjectOutputStream getOutputWriter() {
		return this.out;
	}
	
	synchronized public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	public void run() {
		try {
			in = new ObjectInputStream(clientsocket.getInputStream());
			out = new ObjectOutputStream(clientsocket.getOutputStream());
			out.flush();
		} catch (IOException e) {
			System.out.println("in or out failed");
			System.exit(-1);
		}


			try {
				// Read data from client

				// InitialHandShake read
				Object msg = in.readObject();

				if (msg instanceof Message) {
					Message broadcastMessage = (Message) msg;
					// add received messages to Blocking queue
					this.clientUID = Integer.toString(broadcastMessage.getsenderUID());
					System.out.println("Msg rx UID: " + broadcastMessage.getsenderUID()+" "+broadcastMessage.getMsgType()+" tmp"+broadcastMessage.getTimeStamp());
					this.dsNode.addMessageToQueue(broadcastMessage);
				}
				while (this.flag) {
					Thread.sleep(1000);
				}

			} catch (IOException | ClassNotFoundException | InterruptedException e) {
				System.out.println("Read failed");
				System.exit(-1);
			}
		
	}
	
}
