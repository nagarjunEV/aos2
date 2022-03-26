import java.io.IOException;
import java.util.Random;

public class RicartAgrawala {
	Node dsNode;
	int csEntryCount = 0;
	TCPClient[] servers = new TCPClient[3];

	public RicartAgrawala(Node _dsNode) {
		this.dsNode = _dsNode;
		servers[0] = new TCPClient(0, 3223, "dc02.utdallas.edu", dsNode.getNodeHostName(), 0, dsNode);
		servers[1] = new TCPClient(0, 3224, "dc03.utdallas.edu", dsNode.getNodeHostName(), 1, dsNode);
		servers[2] = new TCPClient(0, 3225, "dc05.utdallas.edu", dsNode.getNodeHostName(), 2, dsNode);
	}

	public void InitiateAlgorithm() {

		String[] operation = new String[2];
		operation[0] = "Read";
		operation[1] = "Write";
		
		while (this.csEntryCount < 20) {
			int op = new Random().nextInt(2);
			int randomFileNumber = getRandomNumber();
			dsNode.setFileNumber(randomFileNumber);
			Request_Resource();
			
			do {
				CriticalSection(op);
				op = new Random().nextInt(2);
			}while(!dsNode.ifRequestReceived() && this.csEntryCount < 6 && (getRandomNumber() == dsNode.getFileNumber()));
			Release_Resource();

			try {
				Thread.sleep(new Random().nextInt(300)+ 500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done");
	}

	private int getRandomNumber() {
		return new Random().nextInt(dsNode.getServerFileCount());
	}

	public void Request_Resource() {
		int FileNumber = dsNode.getFileNumber();
		dsNode.IncrementMyTimeStamp();
		dsNode.setWaiting(true);
		dsNode.temp = dsNode.getMyTimeStamp();
		Message msg = new Message(dsNode.getMyTimeStamp(), dsNode.UID, MessageType.Request, FileNumber);
		synchronized (dsNode.connectedClients) {
			System.out.println("dsNode.connectedClients" +dsNode.connectedClients.size());
			dsNode.connectedClients.parallelStream().forEach((clientHandler) -> {
				if(!dsNode.authorize[FileNumber][clientHandler.getClientUID()]) {
					System.out.println("Request for file"+FileNumber+" sent from " + dsNode.UID + " to " + clientHandler.getClientUID()
					+"at "+dsNode.getMyTimeStamp());
					try {
						clientHandler.getOutputWriter().writeObject(msg);
						clientHandler.getOutputWriter().flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}

		dsNode.waitforReplies();
		dsNode.setWaiting(false);
		dsNode.setUsing(true);

		// TODO: Wait for replies to enter the Critical Section
	}

	synchronized private void CriticalSection(int op) {
		this.csEntryCount++;
		dsNode.IncrementMyTimeStamp();
		System.out.println("IN Critical Section folks at:"+dsNode.getMyTimeStamp() + " file"+dsNode.getFileNumber()+".txt");
		if(op == 0) {
			Read();
		}
		else {
			Write();
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Exiting the Critical Section"+ " file"+dsNode.fileNumber+".txt");

	}

	public void Release_Resource() {
		int fileNumber = dsNode.getFileNumber();
		System.out.println("Resource Released");
		dsNode.setUsing(false);
		for( int i = 0; i < dsNode.reply_deferred[0].length; i++) {
			if(dsNode.reply_deferred[fileNumber][i] != true || i == dsNode.UID)
				continue;
			dsNode.authorize[fileNumber][i] = dsNode.reply_deferred[fileNumber][i] = false;
			System.out.println("Sending Deferred Replies");
			dsNode.sendReply(fileNumber,i);
		}
	}

	public void Read() {
		int index = new Random().nextInt(3);
		System.out.println("Read from Server:"+index);
		servers[index].listenSocket();
		try {
			Runnable clientRunnable = new Runnable() {
				public void run() {
					Message msg = servers[index].listenToServerReplies();
					System.out.println("Read from Server:"+index+" Message: "+msg.getStr());
				}
			};
			Thread clientthread = new Thread(clientRunnable);
			clientthread.start();
			servers[index].getOutputWriter().writeObject(new Message(dsNode.getMyTimeStamp(),dsNode.UID,MessageType.Read,dsNode.getFileNumber()));
			while(servers[index].getFlag() != false) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		//		servers[0].closeConnection();
		//		System.out.println("Read from Server: "+index);
	}

	public void Write() {

		for(TCPClient x: servers) {
			x.listenSocket();
			try {
				x.getOutputWriter().writeObject(new Message(dsNode.temp,dsNode.UID,MessageType.Write,dsNode.getFileNumber()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			Runnable clientRunnable = new Runnable() {
				public void run() {
					Message msg = x.listenToServerReplies();
					System.out.println("Write to Server:"+x.getServerUID() +" Message: "+msg.getStr());
				}
			};
			Thread clientthread = new Thread(clientRunnable);
			clientthread.start();
			while(x.getFlag() != false) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//			x.closeConnection();
		}
	}

	public void enquire() {
		servers[0].listenSocket();

		try {
			servers[0].getOutputWriter().writeObject(new Message(dsNode.getMyTimeStamp(),dsNode.UID,MessageType.Enquire));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Message msg = servers[0].listenToServerReplies();
		//		servers[0].closeConnection();
		String str = msg.getStr();
		String[] tmp = str.split("\\r?\\n");
		dsNode.setAuthorizeReplySize(Integer.parseInt(tmp[tmp.length-1]));
		System.out.println("ServerFiles count: "+tmp[tmp.length-1]);
		System.out.println("Enquiry: " + msg.getStr());
	}

}
