import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;


public class Driver {

	static BufferedOutputStream outputFile;
	
	public static void main(String[] args) {

		//Output file
		outputFile = null;
		try {
			outputFile = new BufferedOutputStream(new FileOutputStream("output.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//Input file as argument
		String inputFile = args[0];
		
		//Map to maintain hash tags
		HashMap<String, FibonacciHeapNode> tagMap = new HashMap<String, FibonacciHeapNode>();
		
		//Fibonacci Heap
		FibonacciHeap heap = new FibonacciHeap();
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			String str;
			while ((str = br.readLine()) != null) {
				// Use case statement here
				if(str.charAt(0) == '#') {
					
					String[] ip = str.split(" ");
					String tag = ip[0].substring(1);
					int count = Integer.parseInt(ip[1]);

					//increase key if hashtag already present
					if(tagMap.containsKey(tag)) {
						heap.increaseFreqOfNode(tagMap.get(tag), count);
					} 
					//if hashtag not present add it to tree
					else {
						FibonacciHeapNode node = new FibonacciHeapNode(count, tag);
						tagMap.put(tag, node);
						heap.addNode(node);
					}
				} 
				
				else if (str.equalsIgnoreCase("stop")) {
					//terminate run
					try {
						outputFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				} 
				// extract given number of max count nodes
				else {
					
					int lines = Integer.parseInt(str);					
					Queue<FibonacciHeapNode> q = new LinkedList<FibonacciHeapNode>();

					int n = lines;					
					
					while(n >= 1) {
						if(n != lines) {
							try {
								outputFile.write(",".getBytes());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						
						FibonacciHeapNode node = heap.removeMaxFreqNode();
						
						if(node != null) {
							try {
								outputFile.write(node.hashTag.getBytes());
							} catch (IOException e) {
								e.printStackTrace();
							}
						} 
						
						else {
							try {
								outputFile.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							return;
						}
						
						resetNode(node);
						q.add(node);
						n--;
					}
					try {
						outputFile.write('\n');
					} catch (IOException e) {
						e.printStackTrace();
					}
					for(FibonacciHeapNode fibn: q) {
						heap.mergeTopLevelNodes(fibn);
					}
				}
			}
		} catch(Exception e) {
			System.out.println("Exception occured" + e);
		}	
	}
	
	// Resets the value of max node that was extracted earlier 
	// so that it can be inserted as a fresh node
	private static void resetNode(FibonacciHeapNode n) {
		n.numChildren = 0;
		n.childCutValue = false;
        n.parent = null;
        n.prevSibling = null;
        n.nextSibling = null;
        n.childNode = null;

	}
	
}
