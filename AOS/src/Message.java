import java.io.Serializable;

// Message Class with all the required Attributes for message exchanges. 
public class Message  implements Serializable, Comparable<Message> {
	
	private static final long serialVersionUID = 1L;
	private int timeStamp;
	private int senderUID;
	private String str;
	private String fileName;
	private int fileNumber;
	private MessageType msgtype;
	

	public Message(int TimeStamp, int senderUID, MessageType Msgtype) {
		this.timeStamp = TimeStamp;
		this.senderUID = senderUID;
		this.msgtype = Msgtype;
		
	}
		
	public Message(int TimeStamp, int senderUID, MessageType Msgtype, int FileNumber) {
		this.timeStamp = TimeStamp;
		this.senderUID = senderUID;
		this.msgtype = Msgtype;
		this.fileName = "file"+FileNumber+".txt";
		this.fileNumber = FileNumber;
	}
	
	public Message(MessageType msgtype) {
		this.msgtype = msgtype;
	}
	
	public Message(Message message) {
		this(message.timeStamp, message.senderUID, message.msgtype);
	}

	public int getTimeStamp() {
		return this.timeStamp;
	}
	
	public int getsenderUID() {
		return this.senderUID;
	}

	public MessageType getMsgType() {
		return this.msgtype;
	}
	
	public String getFileName() {
		return this.fileName;
	}
	
	public int getFileNumber() {
		return this.fileNumber;
	}
	public String getStr() {
		return this.str;
	}
	@Override
	public int compareTo(Message msg) {
		if(msg.getMsgType() == MessageType.Reply)
			return -1;
		else
			return 1;
	}
}
