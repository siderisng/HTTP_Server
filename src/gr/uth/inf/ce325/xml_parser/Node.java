//CE325 - Prwto project -  Fotis Tsokos 1679 Giorgos Sideris 1622

//Represents every node in the XML doc
package gr.uth.inf.ce325.xml_parser;
import java.util.List;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Node {
	
	private String nodeName; //name of node
	private String text=""; //text
	private List <Node> nodeList; //list of children nodes
	private List <Attribute> attrList;//list of attributes
	private Namespace nmRef; //Namespace linked to node
	private Document docRef; //Document -//-
	private Node parent;    //parent of node
	private int readIdx;    //position of last child accesed on nodeList
	private int attrIdx;//position of last attribute accessed on attrList
	private String spIndent;//used for indentation 
	private String indent;  //indentation between parent and children
	private int nofKidz=0;  //number of Kidz
	private int nofAttrs=0;  //number of attributes    
	private int parentFl=0;  //checks if node has parent  (if root) 
	private int nmRefF=0;   //checks if node has namespace 
	
	////////////////////Constructors///////////////////////////////
	public Node (){
		//init Lists
		this.attrList = new LinkedList<Attribute>();
		this.nodeList = new LinkedList<Node>(); 
		
	}
	
	public Node (Document Doc, String nodeStr){
		//init Lists
		this.attrList = new LinkedList<>();
		this.nodeList = new LinkedList<>(); 
		//link node to doc
		docRef = Doc;
		parseNode (nodeStr);//start parsing of nodename
		
	}
	
	public Node (Document Doc, String nodeStr, Node parent){
		//init Lists  
		this.nodeList = new LinkedList<>(); 
		this.attrList = new LinkedList<>();
		
		//init node with assigned values
		parseNode(nodeStr);
		parentFl = 1; 
		docRef = Doc; 
		this.parent = parent;
		
	}
	
	public Node (Document Doc, String nodeStr, Node parent, Namespace nm){
		//init Lists  
		this.nodeList = new LinkedList<>(); 
		this.attrList = new LinkedList<>();
		
		//init node with assigned values
		nmRef = nm;
		nmRefF = 1;
		parseNode(nodeStr);
		parentFl = 1; 
		docRef = Doc; 
		this.parent = parent;
		
	}
	
	public Node (Document Doc, String name, String text){
		//init Lists  
		this.nodeList = new LinkedList<>(); 
		this.attrList = new LinkedList<>();
		
		//init node with assigned values
		nodeName = name;
		this.text = text;
		docRef = Doc; 
		
		
	}
	
	
	public Node (Document Doc, String name, String text, List<Attribute> attrs,Namespace nm){
		//init Lists  
		this.nodeList = new LinkedList<>(); 
		this.attrList = new LinkedList<>();
		
		//init node with assigned values
		nodeName = name;
		this.text = text;
		docRef = Doc; 
		attrList = attrs;
		nmRef = nm;
		nmRefF = 1;
	}
	
	
	public Node (Document Doc, String name, String text, Node parent){
		//init Lists  
		this.nodeList = new LinkedList<>(); 
		this.attrList = new LinkedList<>();
		
		//init node with assigned values
		this.parent = parent;
		nodeName = name;
		this.text = text;
		docRef = Doc; 
		parentFl = 1;
		
	}
	
	public Node (Document Doc, String name, String text,Node parent, List<Attribute> attrs,Namespace nm){
		//init Lists  
		this.nodeList = new LinkedList<>(); 
		this.attrList = new LinkedList<>();
		
		//init node with assigned values
		nodeName = name;
		this.text = text;
		this.parent = parent;
		parentFl = 1;
		docRef = Doc; 
		attrList = attrs;
		nmRef = nm;
		nmRefF = 1;
	}
	
	
	public Node (String name){
		//init Lists  
		this.nodeList = new LinkedList<>(); 
		this.attrList = new LinkedList<>();
		
		//init node with assigned values
		nodeName = name;
		
	}
	
	public Node (String name, String text){
		//init Lists  
		this.nodeList = new LinkedList<>(); 
		this.attrList = new LinkedList<>();
		
		//init node with assigned values
		nodeName = name;
		this.text = text;
	}
	
	public Node (String name, String text, List<Attribute> attrs,Namespace nm){
		//init Lists  
		this.nodeList = new LinkedList<>(); 
		this.attrList = new LinkedList<>();
		
		//init node with assigned values
		nodeName = name;
		this.text = text; 
		attrList = attrs;
		nmRef = nm;
		nmRefF = 1;
	}
	
	public Node (String name, String text, Node parent){
		//init Lists  
		this.nodeList = new LinkedList<>(); 
		this.attrList = new LinkedList<>();
		
		//init node with assigned values
		this.parent = parent;
		nodeName = name;
		this.text = text;
		parentFl = 1;
	}    
	
	public Node (String name, String text, Node parent,  List<Attribute> attrs){
		//init Lists  
		this.nodeList = new LinkedList<>(); 
		this.attrList = new LinkedList<>();
		
		//init node with assigned values
		attrList = attrs;
		this.parent = parent;
		nodeName = name;
		this.text = text;
		parentFl = 1;
	}    
	
	
	public Node (String name, String text, Node parent,  List<Attribute> attrs, Namespace nm){
		//init Lists  
		this.nodeList = new LinkedList<>(); 
		this.attrList = new LinkedList<>();
		
		//init node with assigned values
		attrList = attrs;
		this.parent = parent;
		nodeName = name;
		this.text = text;
		parentFl = 1;
		nmRef = nm;
		nmRefF = 1;
	}    
	////////////////////Methods//////////////////////////
	
	
	//////set/add methods
	
	
	//setName:Set node name
	public void setName(String name){
		
		nodeName = name;
		
	}
	
	//setParent:Sets Parent of node
	public void setParent(Node parent){
		
		parentFl = 1;
		this.parent = parent;
		
	}
	
	//setText:Set node text
	public void setText(String text){
		
		this.text = text;
		
	}
	
	
	//addChild:adds a child Node
	public void addChild(Node child){
		
		nodeList.add(nofKidz++,child);
		
	}
	
	
	//addChild:adds a child Node
	public void addChild(int index, Node child){
		
		nofKidz++;
		readIdx = index;
		nodeList.add(index, child);
		
	}
	
	//addAttribute: adds an attribute
	public void addAttribute(Attribute attr){
		
		attrIdx = 0;
		attrList.add(nofAttrs++, attr);
		
	}
	
	//addAttribute: adds an attribute with index
	public void addAttribute(int index, Attribute attr){
		
		nofAttrs++;
		attrIdx = index;
		attrList.add(index, attr);
		
	}
	
	
	//setNamespace:Sets Namespace
	public void setNamespace(Namespace n){
		nmRefF=1;  //flag
		nmRef = n;
		
	}
	
	
	/////Get/print methods
	
	//getNamespace:Returns Namespace
	public Namespace getNamespace(){
		
		
		return (nmRef);
		
	}
	
	
	//getName:Returns Name
	public String getName(){
		
		return (nodeName);
		
	}
	
	//getText: return node Text
	public String getText(){
		
		return (text);
		
	}
	
	
	//getParent:Gets parent of node
	public Node getParent(){
		
		return (parent);
		
	}
	
	
	//getFirstChild:Returns first Child
	public Node getFirstChild(){
		
		readIdx = 0;
		return (nodeList.get(readIdx));
		
	}
	
	//getNextChild:Returns next Child
	public Node getNextChild(){
		
		readIdx++;
		if (readIdx <= nofKidz){ //if there is another kido
		return (nodeList.get(readIdx));
		}else{  //if not return null and initialize readIdx
		readIdx=0;
		return (null);
		}
	}
	
	//getChild:Returns the child node at index
	public Node getChild(int index){
		
		readIdx = index;
		
		return (nodeList.get(index));
		
	}
	
	//getChildren: returns list of Children
	public List<Node> getChildren(){
		
		return (nodeList);
		
	}
	
	
	
	//getFirstAttribute:Returns First attribute
	public Attribute getFirstAttribute(){
		
		attrIdx = 0 ;
		return (attrList.get(attrIdx));
		
	}
	
	
	//getNextAttribute:Returns Next attribute
	public Attribute getNextAttribute(){
		
		attrIdx++;
		if (attrIdx<= nofAttrs){    //if there is another attribute
		return (attrList.get(attrIdx));
		}else{      //if not return null and initialize nofAttrs
		nofAttrs = 0;
		return (null);
		}
	}
	
	//getAttribute: Returns Attribute at index
	public Attribute getAttribute(int index){
		
		attrIdx = index;
		return (attrList.get(index));
		
	}
	
	//getAttributes: Returns all Attributes 
	public List <Attribute> getAttributes(){
		
		return (attrList);
		
	}
	
	
	
	
	//toString: Returns a string representation blah blah
	
	@Override       
	public String toString(){
		indent = " ";
		spIndent = " ";
		
		String toPrint;
		
		
		toPrint = formStr();
		
		return (toPrint);
		
	}
	
	//toString: Returns a string representation blah blah starting from
	//a certain depth
	public String toString(int depth){
		indent = " ";
		spIndent = " ";
		int i;
		String toPrint;
		
		for (i=0; i<depth; i++){
			spIndent=spIndent+"|_____|"; //special indent to help distinguish nodes
			indent = indent +"       " ;
		}
		toPrint = formStr();
		
		return (toPrint);
	}
	
	////ETC
	
	//check if arg indeed matches attribute and then creates a new attribute object and
	//puts it in the appropriate list
	protected void parseNodeAttributes(String str ) {
		/*Pattern: ([a-zA-z_]+) name of attribute
		 *([+\\p{Alnum}\\p{Punct}]+) attribute value*/
		Pattern attrP = Pattern.compile(" ([a-zA-z_]+)=\"([+\\p{Alnum}\\p{Punct}]+)\"");
		Matcher attrM = attrP.matcher(str);
		
		while(attrM.find() ) {
			//if indeed attribute create new atribute object
			Attribute newAttr = new Attribute (docRef,attrM.group()); 
			newAttr.setNamespace(nmRef);
			attrList.add (nofAttrs++,newAttr);
		}
		
	}
	//parseNode:parse node str String and populate the node object
	protected void parseNode(String nodeStr){
		
		//patter explained @ DocumentBuilder class 
		Pattern nodeP = Pattern.compile("<([a-zA-Z_]+)([a-zA-Z_0-9:]*)([^>]*)");
		Matcher attrM = nodeP.matcher(nodeStr);
		
		while (attrM.find()){
			//add nodename if found
			nodeName = attrM.group(1) + attrM.group(2);
			
			
			//if something looks like attrbutes send it to parse node attributes to check
			parseNodeAttributes(attrM.group(3));
		}    
	}
	
	//creates the string to be printed
	private String formStr(){
		String finalStr;    //string to return
		int i= 0;           //used for list searching
		
		
		//add node name to string
		finalStr = spIndent+"Node Name: " +nodeName+"\n";
		
		//if there's text add it to string
		if (!text.isEmpty()){
			finalStr = finalStr + indent+"Text: " +text + "\n";
		}
		
		//if node is linked to a namespace print it
		if (nmRefF!=0){	
			finalStr =finalStr + indent+"Namespace: "+ nmRef.toString()+"\n";
		}
		
		//if there's a parent print him/her :P
		if (parentFl!=0){
			finalStr = finalStr +indent+"Parent Node: "
			+ parent.nodeName +"\n";
		}
		
		//print the list of attributes
		if (nofAttrs > 0){
			String attrStr = (indent+"Attributes :" );
			//attrString
			while ( attrList.size()!= i){
				attrStr = attrStr +"\n" + indent+"Name: " 
				+(attrList.get(i)).getName()+ " Value: " 
				+(attrList.get(i)).getValue() ; 
				i++;
			}	
			
			finalStr = finalStr+ attrStr;
			i=0;
		}
		//print the list of children nodes
		if (nofKidz>0){
			
			String nodeStr = (indent+"Children : ");
			while ( nodeList.size()!= i){
				
				nodeStr = nodeStr +"\n"+ indent +  "Name: " 
				+ (nodeList.get(i)).nodeName;
				i++;
			}
			
			finalStr = finalStr + nodeStr;
		}
		return (finalStr);
	}
	
	
}
