import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileRequestHandler {
	
	Node dsNode;
	
	public FileRequestHandler(Node node) {
		this.dsNode = node;
	}
	
	public void listen() {
		// Listen for Requests
		while(true) {
			if(dsNode.msgQueue.size() != 0) {
				String str = "";
				Message msg = dsNode.getHeadMessageFromQueue();
				if(msg.getMsgType() == MessageType.Read) {
					str = readFromFile(msg.getFileName());
				}
				else if(msg.getMsgType() == MessageType.Enquire) {
					str = enquire();
				}
				else if(msg.getMsgType() == MessageType.Write) {
					writeToFile(msg.getsenderUID(),msg.getTimeStamp(),msg.getFileName());
				}
				dsNode.sendReply(msg.getsenderUID(), msg.getFileName(),str);
			}
		}
	}

	private String enquire() {
		// Send the Filenames and the no.of files
		File dir = new File(dsNode.path);
		if(!dir.exists())
			dir.mkdir();
		StringBuilder builder = new StringBuilder();
		int count = 0;
		for(File tmp : dir.listFiles()) {
			builder.append(tmp.getName()+"\n");
			count++;
		}
		builder.append(count);
		return builder.toString();
	}

	private void writeToFile(int UID, int timestamp, String fileName) {
		try
		{
			// Append (UID and Timestamp) to the file
			FileWriter fw = new FileWriter(dsNode.path+"/"+fileName,true); //the true will append the new data
		    fw.write("UID: "+UID+" TimeStamp: "+timestamp+"\n");//appends the string to the file
		    System.out.println("Write. "+"UID: "+UID+" TimeStamp: "+timestamp + " "+fileName);
		    fw.close();
		}
		catch(IOException ioe)
		{
		    System.err.println("IOException: " + ioe.getMessage());
		}
	}

	private String readFromFile(String fileName) {
		// Read Last line from the file
		String last="", line;
	    BufferedReader input;
		try {
			input = new BufferedReader(new FileReader(dsNode.path+"/"+fileName));

		    while ((line = input.readLine()) != null) { 
		        last = line;
		    }
		    System.out.println("Read from "+fileName+" "+ last);
		    input.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return last;
	}
}
