import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Vaibhav
 * Fibonacci Heap class to capture hash tags frequencies given by the inputs. Following are the key functions
 * included in the process: 
 * removeMaxFreqNode: Function to keep extracting max value nodes to prepare the descending order frequency list
 * increaseFreqOfNode: Increment the incoming hashtag frequency if the node already exists.
 * mergeTopLevelNodes: Meld the top level nodes based on the hashtable to track the degrees of each subtree
 *
 */
public class FibonacciHeap {
	//max node declaration with no frequency	
	private FibonacciHeapNode maxFreqNode;
    public FibonacciHeap() {
        maxFreqNode = null;
    }
    
    //New Hash tag node 
    FibonacciHeapNode addNode(FibonacciHeapNode node) {
        this.mergeTopLevelNodes(node);         
        return node;
    }
    
    /**
     * Removes the max frequency node from the heap and lines it in a queue
     * to be read for the output file. Once read, the queue nodes are set to reset
     * and again reinserted in the heap.
     */
    public FibonacciHeapNode removeMaxFreqNode() {
    	//No node exception
        if(maxFreqNode == null) {
            System.out.println("No max node");
            return null;
        }
        
        //Hold removed nodes in a temporary queue for later re insertion 
        Queue<FibonacciHeapNode> q = new LinkedList<FibonacciHeapNode>(); 
        FibonacciHeapNode max_node = maxFreqNode;
        
        FibonacciHeapNode child = max_node.childNode;

        //Reset the data fields of each node to be reinserted as a fresh node
        for( int i=0 ; i < max_node.numChildren ; i++) {
        	if(child == null) {
            	System.out.println("Null child with degree greater than 0");
                System.exit(1);
            }
            
            FibonacciHeapNode temp = child.nextSibling;
            resetChild(child);
            q.add(child);
            child = temp;
        }
        
        FibonacciHeapNode tmpNode = maxFreqNode.nextSibling;
        
        while((tmpNode != null) && (tmpNode != maxFreqNode)) {
            q.add(tmpNode);
            tmpNode = tmpNode.nextSibling;
        }
        
        maxFreqNode = null;

        //Combine nodes of same degree after max remove 
    	HashMap<Integer, FibonacciHeapNode> mapTransition = new HashMap<Integer, FibonacciHeapNode>();
        int maxChildren = 0; 
        while(!q.isEmpty()) {
        	FibonacciHeapNode tmpNode1 = q.poll(); 
            if(tmpNode1 == null){
                System.out.println("Pairwise combine error");
            } 
            else {
                
            	while(mapTransition.containsKey(tmpNode1.numChildren)) { 
                	FibonacciHeapNode tmpNode2 = mapTransition.get(tmpNode1.numChildren);
                    mapTransition.remove(tmpNode1.numChildren);
                    
                    if(tmpNode1.freq >= tmpNode2.freq) {
                        tmpNode1.addChildNode(tmpNode2);
                    }
                    else {
                        tmpNode2.addChildNode(tmpNode1);
                        tmpNode1 = tmpNode2;
                    }
                }
                //store node after pairwise combine in map
                mapTransition.put(tmpNode1.numChildren, tmpNode1);
                if(tmpNode1.numChildren > maxChildren)
                    maxChildren = tmpNode1.numChildren;
            }
        }
        
        for(int i=0 ; i<=maxChildren ; i++) {
            if(mapTransition.containsKey(i)) {
            	mergeTopLevelNodes(mapTransition.get(i)); 
                mapTransition.remove(i);
            }
        }
        return max_node;
    }

    private void resetChild(FibonacciHeapNode childNode) {
    	childNode.childCutValue = false;
    	childNode.parent = null;
    	childNode.prevSibling = null;
    	childNode.nextSibling = null;
    }
    
    /**
     * Function to increment the frequency value of hash tags as received from input file.
     */
    public void increaseFreqOfNode(FibonacciHeapNode fibHeapNode, int incrFreq) {
        //No node exception 
    	if(fibHeapNode == null) {
            System.out.println("No node exception");
            return;
        }
        
        FibonacciHeapNode parent = fibHeapNode.parent;
        
        //Non root node frequency update
        if(fibHeapNode.parent != null) {
        	//check if the increment is less than the parent node
        	if((incrFreq + fibHeapNode.freq <= fibHeapNode.parent.freq) && (incrFreq != -1)) { 
                fibHeapNode.freq = incrFreq + fibHeapNode.freq;
            } 
        	
        	else {
                        		
        		if(fibHeapNode.parent.numChildren > 1) {
                    fibHeapNode.prevSibling.nextSibling = fibHeapNode.nextSibling; 
                    fibHeapNode.nextSibling.prevSibling = fibHeapNode.prevSibling;                    
                    if(fibHeapNode == fibHeapNode.parent.childNode)
                        fibHeapNode.parent.childNode = fibHeapNode.nextSibling;                    	
                } 
        		
        		else if(fibHeapNode.parent.numChildren == 1) {
                    fibHeapNode.parent.childNode = null;
                } 

        		else {
                    System.out.println("Increase frequency error");
                    return;
                }
                
        		updateFibonacciHeapNode(fibHeapNode);
        		
                // Decrement by 1 to indicate modified child cut
        		
                if(incrFreq != -1)
                    fibHeapNode.freq = fibHeapNode.freq + incrFreq; 

                //merge top level nodes
                mergeTopLevelNodes(fibHeapNode); 
                
                               
                if(parent.childCutValue == false && parent.parent != null) {
                    parent.childCutValue = true;                	
                } 
                
                else if(parent.childCutValue == true && parent.parent != null) {
                    increaseFreqOfNode(parent, -1); 
                }
            }
        } 
      //Root node value update without changing the rest heap
        else {  
            fibHeapNode.freq = fibHeapNode.freq + incrFreq;
            if(fibHeapNode != maxFreqNode && fibHeapNode.freq > maxFreqNode.freq)
                maxFreqNode = fibHeapNode;
        } 
    }

    //update node values to re insert in the heap.
    private void updateFibonacciHeapNode(FibonacciHeapNode n) {
        
        n.parent.numChildren = n.parent.numChildren - 1; 
        n.prevSibling = null;
        n.nextSibling = null;
        n.parent = null;
        n.childCutValue = false;
    }
            
    /**
     * Given two Fibonacci heaps of same degree, merge returns a new joined heap with
     * max of both heaps as root node and the other heap as the child
     *
     */
    
    void mergeTopLevelNodes(FibonacciHeapNode node) {
    	if((node.numChildren > 0) && (node.childNode == null)){
        	System.out.println("Wrong node merge");
        	System.exit(2);
        }
    	
    	if(maxFreqNode != null) { 
        	node.prevSibling = maxFreqNode.prevSibling;
        	node.nextSibling = maxFreqNode;
        	maxFreqNode.prevSibling.nextSibling = node;
        	maxFreqNode.prevSibling = node;
        	if(maxFreqNode.freq < node.freq) {
        		    maxFreqNode = node;         		
        	}
    	} 
    	
    	else {
    		maxFreqNode = node;
    		maxFreqNode.nextSibling = maxFreqNode;
    		maxFreqNode.prevSibling = maxFreqNode;
    		if((maxFreqNode.numChildren > 0) && (maxFreqNode.childNode == null)){
    			System.out.println("Wrong node merge");
    			System.exit(2);
    		}

        }
    }    
}

