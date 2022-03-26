import java.io.Serializable;

public class Message  implements Serializable, Comparable<Message>{
	
	private static final long serialVersionUID = 1L;
	private int timeStamp;
	private int senderUID;
	private String fileName;
	private String str;
	private MessageType msgtype;
	
	
	public Message(int senderUID, MessageType Msgtype, String FileName, String str) {
		this.senderUID = senderUID;
		this.msgtype = Msgtype;
		this.fileName = FileName;
		this.str = str;
	}
	
	public Message(int timeStamp, int senderUID,MessageType messageType ) {
		this.timeStamp = timeStamp;
		this.senderUID = senderUID;
		this.msgtype = messageType;
	}
	
	public Message(MessageType msgtype) {
		this.msgtype = msgtype;
	}
	
//	public Message(Message message) {
//		this(message.timeStamp, message.senderUID, message.msgtype);
//	}

	public int getTimeStamp() {
		return this.timeStamp;
	}
	
	public String getFileName() {
		return this.fileName;
	}
	
	public int getsenderUID() {
		return this.senderUID;
	}

	public MessageType getMsgType() {
		return this.msgtype;
	}

	@Override
	public int compareTo(Message msg) {
		// TODO Auto-generated method stub
		if(msg.getMsgType() == MessageType.Read)
			return -1;
		else
			return 1;
	}

}
