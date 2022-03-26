import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.io.IOException;
import java.net.*;

public class Node {
	int UID, port;
	String path;
	String HostName;
	HashMap<Integer, NeighbourNode> uIDofNeighbors;
	ServerSocket serverSocket;
	List<TCPClient> connectedClients = Collections.synchronizedList(new ArrayList<TCPClient>());
	BlockingQueue<Message> msgQueue;

	public Node(int UID, int port, String hostName, HashMap<Integer, NeighbourNode> uIDofNeighbors) {
		this.UID = UID;
		this.port = port;
		this.HostName = hostName;
		this.uIDofNeighbors = uIDofNeighbors;
		this.msgQueue = new PriorityBlockingQueue<Message>();
	}

	public Node() {
	}

	public Message getHeadMessageFromQueue() {
		if (this.msgQueue.peek() != null) {
			Message msg = this.msgQueue.peek();
			this.msgQueue.remove();
			return msg;
		}
		return null;
	}

	public Message getMessageFromQueue() {
		Message msg = null;
		try {
			msg = this.msgQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return msg;
	}

	synchronized public void addMessageToQueue(Message msg) {
		//			setMyTimeStamp(Math.max(msg.timeStamp,getMyTimeStamp()));
		msgQueue.add(msg);
	}

	public void sendReply(int getsenderUID, String file, String str) {
		synchronized (connectedClients) {
			for(Iterator<TCPClient> iterator = connectedClients.iterator(); iterator.hasNext();) {
				TCPClient client = iterator.next();
				if(getsenderUID == client.getClientUID()) {
					try {
						System.out.println("Sending ServerReply to:"+getsenderUID+" "+ file);
						client.getOutputWriter().writeObject(new Message(this.UID,MessageType.ServerReply,file,str));
						System.out.println("Connection Closed for UID:"+getsenderUID);
						client.setFlag(false);
						iterator.remove();
						break;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void attachServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public int getNodeUID() {
		return this.UID;
	}

	public int getNodePort() {
		return this.port;
	}

	public String getNodeHostName() {
		return this.HostName;
	}

	public HashMap<Integer, NeighbourNode> getNeighbors() {
		return this.uIDofNeighbors;
	}

	public void addClient(TCPClient client) {
		synchronized (connectedClients) {
			connectedClients.add(client);
		}
	}


	public List<TCPClient> getAllConnectedClients() {
		return this.connectedClients;
	}

}
