import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class InvokeMain {
	public static void main(String[] args) {
		try {

			// build a node for each terminal
			String clientHostName = "";
			try {
					clientHostName = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
			}

			Node dsNode = BuildNode(clientHostName);
			System.out.println("Initializing Server with UID: " + dsNode.UID);

			// Start server thread
			Runnable serverRunnable = new Runnable() {
				public void run() {
					TCPServer server = new TCPServer(dsNode);
					// start listening for client requests
					server.listenSocket();
				}
			};
			Thread serverthread = new Thread(serverRunnable);
			serverthread.start();

			System.out.println("Server started and listening to client requests.........");

			// Sleep for sufficient time for all the node Servers to start
			Thread.sleep(6000);

			// Iterate through the node neighbors to send the Client Requests
					dsNode.uIDofNeighbors.entrySet().forEach((neighbour) -> {
							Runnable clientRunnable = new Runnable() {
								public void run() {
									TCPClient client = new TCPClient(dsNode.UID,
											neighbour.getValue().PortNumber, neighbour.getValue().HostName, dsNode.getNodeHostName(), neighbour.getKey(),
											dsNode);
									System.out.println("Client Connection sent from "+dsNode.UID+" to UID: "+neighbour.getKey()+" at Port: "+neighbour.getValue().PortNumber);
									// The following function calls starts the Socket Connections, and adds the client to a list to access
									// it later. Listen Messages is an infinite loop to preserve the socket connection
									client.listenSocket();
									client.sendHandShakeMessage();
									dsNode.addClient(client);
									client.listenToMessages();
								}
							};
							Thread clientthread = new Thread(clientRunnable);
							clientthread.start();
					});

			// Sleep so that all the Client connections are established		
			Thread.sleep(5000);
			RicartAgrawala algo = new RicartAgrawala(dsNode);
			// Start Enquiring from the File Server to obtain the metadata (Number of files and fileNames
			algo.enquire();
			// Sleep for sufficient time so all the clients start executing the algorithm almost at the same time
			Thread.sleep(10000);
			algo.InitiateAlgorithm();

		}catch(Exception e){
			e.printStackTrace();
		}
	
	}

	// Finds the current node's UID, HostName, Port and its neighbors, by reading from a config File. 
	public static Node BuildNode(String clientHostName) {
		Node dsNode = new Node();
		try {
			dsNode = ParseConfigFile.read(
					"/home/010/c/cx/cxs172130/AOS/src/readFile.txt",
							InetAddress.getLocalHost().getHostName());
		} catch (Exception e) {
			throw new RuntimeException("Unable to get nodeList", e);
		}
		return dsNode;
	}
}
