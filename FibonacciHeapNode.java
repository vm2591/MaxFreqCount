
/*Node class with fields - degree, child etc.*/
	public class FibonacciHeapNode {
		
	    //parent
	    FibonacciHeapNode parent;

		//left
	    FibonacciHeapNode prevSibling;
	    //right
	    FibonacciHeapNode nextSibling;

		//degree count
	    int numChildren;
	    
	    //child
	    FibonacciHeapNode childNode;

	    //child cut, set false
	    boolean childCutValue;

	    //data
	    int freq;
	    //tag
	    String hashTag;
	    	    

	    public FibonacciHeapNode(int n, String s) {

	    	//defaults
	        parent = null;

	        prevSibling = null;
	        nextSibling = null;

	        numChildren = 0;
	        childNode = null;

	        childCutValue = false;
	        
	        //parameters
	        freq = n;
	        hashTag = s;        
	    }
	    
	    //New node insertion for each new hash tag from the input file   
	    public void addChildNode(FibonacciHeapNode newNode) { 
	        
	    	if(this.numChildren == 0) {
	            this.childNode = newNode;
	            newNode.prevSibling = newNode;
	            newNode.nextSibling = newNode;
	        } else {
	        	FibonacciHeapNode tempChild = this.childNode;
	            newNode.prevSibling = tempChild.prevSibling;
	            newNode.nextSibling = tempChild;
	            tempChild.prevSibling.nextSibling = newNode;
	            tempChild.prevSibling = newNode;            
	        }
	    	
	        this.numChildren++;
	        newNode.parent = this;
	        newNode.childCutValue = false; 
	        
	    }
	} 

