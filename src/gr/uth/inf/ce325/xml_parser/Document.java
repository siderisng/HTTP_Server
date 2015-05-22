//CE325 - Prwto project -  Fotis Tsokos 1679 Giorgos Sideris 1622


//Contains useful information for the xml file
//Contains root node and list of namespaces that the document has
package gr.uth.inf.ce325.xml_parser;
import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;

public class Document {
	
	private static Node Root;   //root node of document (name of file)
	private List <Namespace> nmList; //list of document namespaces
	private String toPrintS;    //used for printing document's info
	private int depth;      //tree depth
	
	
	/////////Constructors/////////////////
	public Document (){
		//init list
		this.nmList = new LinkedList<>();
		
	}
	
	public Document (Node rootNode){
		//init list and set root node
		this.nmList = new LinkedList<>();
		Root = rootNode;
		
	}
	
	//////////////////Methods/////////////////
	
	///////////////////add/set methods
	
	
	//setRootNode:Sets Root
	protected void setRootNode(Node rootNode){
		
		Root = rootNode;
		
	}
	
	//addNamespace:adds a Namespace obj to the list
	protected void addNamespace (Namespace namespace){
		
		nmList.add (0,namespace);
		
	}
	
	
	
	////////////get/print methods
	
	//getRootNode:Returns Root
	public Node getRootNode(){
		
		return (Root);	
		
	}
	
	
	//getNamespace:Returns the Namespace obj that belongs to the list
	public Namespace getNamespace(String prefix){
		
		if (namespacePrefixExists(prefix)){
			for (Namespace temp : nmList) {
				if (temp.getPrefix().equals(prefix)){ //if it exists in the list
				return (temp);
				}
			}
			
		}
		
		
		return null;
		
		
		
	}
	
	
	//toString:Returns a string representation of Doc
	@Override
	public String toString(){
		toPrintS = "";   
		depth= 0;
		int ans=0;
		Node trueRoot = null;
                trueRoot = Root.getFirstChild();
            try (Scanner in = new Scanner (System.in)) {
                while(ans!=1 && ans != 2){
                    
                    System.out.println("Press 1 for Document or 2 for XML representation of file"); //awaiting user input to proceed
                    ans = in.nextInt ();
                    
                    
                }
            }
            catch (Exception ie){
                System.out.println(ie.getCause());
            }
		if (ans == 1){
			toPrintS = "DOCUMENT REPRESENTATION OF FILE" +"\n-------------------";
			
			
			recTree (trueRoot);
		}
		if (ans ==2){
			toPrintS = toPrintS+ "\n\n\nXML REPRESENTATION OF FILE\n"+"-------------------";
			
			
			toPrintS = toPrintS + "\n\n\nNAMESPACES IN DOCUMENT" +"\n-------------------\n";
			for (Namespace temp : nmList){
				
				toPrintS = toPrintS+"xmlns: "+ temp.getPrefix()+"=\""+temp.getURI()+"\""+"\n\n";
			}
			
			toPrintS = toPrintS+ "XML\n"+"-------------------\n\n";
			recXml (trueRoot);
		}
		return (toPrintS);
		
	}
	
	///////////Varia
	
	//namespacePrefixExists:Check if prefix exists or not
	public boolean namespacePrefixExists (String prefix){
		
		for (Namespace temp : nmList){
			if (temp.getPrefix().equals(prefix)){
				return (true);
			} 
			
		}
		return (false);
	}
	
	//using recursion finds all the tree nodes and gives string representation
	private void recTree (Node curr){
		
		List <Node>Children;
		
		
		//prints string representation of current node
		toPrintS =toPrintS +"\n\n" +  curr.toString(depth);
		//get list of children for current node
		Children = curr.getChildren();
		
		//for every children do the same
		for (Node temp : Children){
			depth++;// every time you go deeper in the tree raise depth
			recTree (temp);
		}
		depth--;//when function ends tree returns to previous depth
	}
	
	//xml representation
	private void recXml (Node curr){
		
		//recreating xml file below from the objects we created earlier..
		toPrintS =toPrintS + "<";
		if (curr.getNamespace()!= null){
			
			toPrintS =toPrintS + curr.getNamespace().getPrefix()+":"+curr.getName() ;
			for (Attribute a : curr.getAttributes()){
				
				toPrintS =toPrintS + " " + a.getName()+"=\""+a.getValue()+"\"";
			}
		}
		else{
			
			toPrintS =toPrintS + curr.getName() ;
			
			for (Attribute a : curr.getAttributes()){
				
				toPrintS =toPrintS +" "+ a.getName()+"=\""+a.getValue()+"\" ";
				
			}
		}
		
		toPrintS =toPrintS + ">" +" "+ curr.getText()+" \n";
		
		
		for (Node temp : curr.getChildren()){ //recursive call
		recXml (temp);
		}
		toPrintS =toPrintS + "<";
		if (curr.getNamespace()!= null){
			
			toPrintS =toPrintS + "/" + curr.getNamespace().getPrefix()+":"+curr.getName() + "  " ;
			
		}
		else{
			
			toPrintS =toPrintS + "/"+ curr.getName() + "  " ;
			
		} 
		
		toPrintS =toPrintS + ">"+"\n";
	}
}
