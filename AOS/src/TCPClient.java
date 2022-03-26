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

	public int getServerUID() {
		return this.serverUID;
	}
	
	public ObjectInputStream getInputReader() {
		return this.in;
	}

	public ObjectOutputStream getOutputWriter() {
		return this.out;
	}
	
	public void run() {
		// If using TCPClient as a ClientRequestHandler 
		try {
			in = new ObjectInputStream(clientsocket.getInputStream());
			out = new ObjectOutputStream(clientsocket.getOutputStream());
			out.flush();
		} catch (IOException e) {
			System.out.println("in or out failed");
			System.exit(-1);
		}

		while (true) {
			try {
				// Read data from client

				// InitialHandShake read
				Object msg = in.readObject();
				if (msg instanceof String) {
					String message = msg.toString();
					String[] msgArr = message.split("!");
					// Client UID is the UID from which we received the request
					this.clientUID = msgArr[1];
					System.out.println("Text received from client: " + this.clientUID);
				}

				else if (msg instanceof Message) {
					Message broadcastMessage = (Message) msg;
					// Increment Lamport Clock &  add received messages to Blocking queue
					this.dsNode.setMyTimeStamp(Math.max(broadcastMessage.getTimeStamp(), dsNode.getMyTimeStamp()+1));
					System.out.println("Msg rx UID: " + broadcastMessage.getsenderUID()+" "+broadcastMessage.getMsgType()+" tmp:"+broadcastMessage.getTimeStamp()+" at:"+dsNode.getMyTimeStamp()+ "for "+broadcastMessage.getFileName());
					this.dsNode.messageHandler(broadcastMessage);
				}

			} catch (IOException | ClassNotFoundException e) {
				System.out.println("Read failed");
				System.exit(-1);
			}
		}
	}
	
	// If using TCPClient as a Client Request Sender
	public void listenSocket() {
		// Create socket connection
		try {
			clientsocket = new Socket(serverHostName, serverPortNumber, InetAddress.getByName(clientHostName), 0);
			out = new ObjectOutputStream(clientsocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(clientsocket.getInputStream());
			System.out.println("After inputStream, listenSocket");
		} catch (UnknownHostException e) {
			System.out.println("Unknown host:" + serverHostName);
			System.exit(1);
		} catch (IOException e) {
			System.out.println("No I/O" + e);
			System.exit(1);
		}
	}

	public void sendHandShakeMessage() {

		try {
			// Send text to server
			System.out.println("Sending HandShake message to server " + this.serverUID + ".....");
			String msg = "Hi!" + this.UID;
			out.writeObject(msg);
			out.flush();
		} catch (IOException e) {
			System.out.println("failed transmission" + e);
			System.exit(1);
		}
	}

	public void listenToMessages() {
		try {
			while (true) {
				// listen for messages
				Message message = (Message) in.readObject();
				this.dsNode.setMyTimeStamp(Math.max(message.getTimeStamp(), dsNode.getMyTimeStamp()+1));
				// add received messages to Blocking queue
				this.dsNode.messageHandler(message);
				
				System.out.println("Msg rx UID: " + message.getsenderUID()+" "+message.getMsgType()+" tmp"+message.getTimeStamp()+" at:"+dsNode.getMyTimeStamp()+ "for file:"+message.getFileName());
			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("failed transmission");
			System.exit(1);
		}

	}
	
	public Message listenToServerReplies() {
		// File Server Replies
		flag = true;
		Message message = null;
		System.out.println("Started Listening to Server Reply");
		try {
			message = (Message) in.readObject();
			System.out.println("ServerReply Received");
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		flag = false;
		return message;
	}
	
	public boolean getFlag() {
		return this.flag;
	}
}
