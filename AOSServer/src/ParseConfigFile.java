import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.Map.Entry;

class NeighbourNode {
	String HostName;
	int PortNumber;

	NeighbourNode(String _hostName, int _portNumber) {
		this.HostName = _hostName;
		this.PortNumber = _portNumber;
	}
}

class ParseConfigFile {
	// final static List<Node> nodelist = new ArrayList<>();
	final static HashMap<String, Node> nodeList = new HashMap<>();
	public static Node read(String Path, String hostName) throws Exception {
		System.out.println(hostName);
		BufferedReader b = new BufferedReader(new FileReader(Path));
		String readLine = "";
		b.readLine();
		String path = b.readLine();
		b.readLine();
		b.readLine();
		int files = Integer.parseInt(b.readLine());
		b.readLine();
		b.readLine();
		Node node = new Node();
		int myUID = -1;
		try {
			// while ((readLine = b.readLine()) != null) {
			while ((readLine = b.readLine()) != null) {
				if (!readLine.equals("")) {
					readLine = readLine.trim();
					System.out.println("I am here");
					String[] s = readLine.split("\\s+");
					for(int i=0;i<s.length;i++)
						System.out.println(i+":"+s[i]);
					int UID = Integer.parseInt(s[0]);
					String Hostname = s[1];
					int Port = Integer.parseInt(s[2]);
					if (hostName.equals(Hostname))
						myUID = UID;
					nodeList.put(Hostname,new Node(UID, Port, Hostname,null));
				}

			}
			node = nodeList.get(hostName);
			node.path = path;

			File server = new File(path+"/Server"+node.getNodeUID());
			node.path = server.getPath();
			System.out.println("ServerPath: "+node.path);
			if(server.exists() && server.isDirectory()) {
				for(File f : server.listFiles())
					if(!f.isDirectory())
						f.delete();
			}
			server.mkdir();
			for( int i=0; i < files; i++) {
				File tmp = new File(server.getPath()+"/file"+i+".txt");
				tmp.createNewFile();
			}
		} finally {

			b.close();
		}

		return node;
	}
}