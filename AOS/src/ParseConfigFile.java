import java.io.BufferedReader;
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
	// Returns the current node details and its neighbors by reading from a config file
	public static Node read(String Path, String hostName) throws Exception {
		System.out.println(hostName);
		BufferedReader b = new BufferedReader(new FileReader(Path));
		HashMap<Integer, NeighbourNode> map;
		String readLine = "";
		b.readLine();
		int numberOfNodes = Integer.parseInt(b.readLine());
		map = new HashMap<Integer, NeighbourNode>(numberOfNodes);
		b.readLine();
		b.readLine();
		Node node = new Node();
		int myUID = -1;
		HashMap<Integer, NeighbourNode> UIDofNeighbors = new HashMap<Integer, NeighbourNode>();
		try {
			// while ((readLine = b.readLine()) != null) {
			while ((readLine = b.readLine().trim()) != null) {
				if (readLine.equals("")) {
//					b.readLine();
					try {
						while ((readLine = b.readLine()) != null) {
							System.out.println(readLine);
							String[] s = readLine.split("\\s+");
							if (myUID == Integer.parseInt(s[0])) {
								for (int i = 1; i < s.length; i++) {
									UIDofNeighbors.put(Integer.parseInt(s[i]), map.get(Integer.parseInt(s[i])));
									System.out.println(s[0] + s[i]);
								}
								break;
							}
							
						}
						break;

					} catch (Exception e) {
						break;

					}
				} else {
					System.out.println("I am here");
					String[] s = readLine.split("\\s+");
					for(int i=0;i<s.length;i++)
					System.out.println(i+":"+s[i]);
					int UID = Integer.parseInt(s[0]);
					String Hostname = s[1];
					int Port = Integer.parseInt(s[2]);
					map.put(UID, new NeighbourNode(Hostname, Port));
					if (hostName.equals(Hostname))
						myUID = UID;
					nodeList.put(Hostname,new Node(UID, Port, Hostname, UIDofNeighbors));
				}

			}
			node = nodeList.get(hostName);
			node.uIDofNeighbors = UIDofNeighbors;

		} finally {
			b.close();
		}

		return node;
	}
}