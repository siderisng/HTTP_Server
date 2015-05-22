//CE325 - Prwto project -  Fotis Tsokos 1679 Giorgos Sideris 1622


//Builds xml Doc from a regular fs file or url
package gr.uth.inf.ce325.xml_parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentBuilder {
	private Node current; //for tree creation and population of nodes
	private Document doc; //the primary document to create
	
	
	//Constructors
	public DocumentBuilder(){};
	
	//Methods 
	
	//getDocumentAsString:Reads a text file and returns context as String
	public String getDocumentAsString(String location){
		
		String file;    //will contain the file 
		if (location.toLowerCase().contains("http://")){//if it's a URL do the appropriate read
		location = getLocationByUrl(location);
		}
		file = readFile(location);//read file from location
		
		doc = new Document();//init doc
		Node root = new Node (doc,location);//create root node of tree
		doc.setRootNode (root); //set this as the document's root node
		root.setName("XML DOCUMENT: " + location); 
		doc = parseDocument(file,doc); //start populating the tree and parsing the doc
		
		//return string describing doc
		return (doc.toString());
	}
	
	//getDocument:Reads a file or url and returns Document object
	public Document getDocument(String location){

		if (location.toLowerCase().contains("http://")){//if it's a URL do the appropriate read
		location=getLocationByUrl(location);
		}
		//same as getDocumentAsString... it returns only the document
		doc = new Document();
		Node root = new Node (doc,location);
		doc.setRootNode (root);
		root.setName("XML DOCUMENT: " + location);
		String file;
		
		
		file = readFile(location);
		doc = parseDocument(file,doc);  
		
		return (doc);
	}
	
	//parseDocument:parses xml String and returns Document object, does not 
	//verify validity of xml
	public Document parseDocument(String documentStr, Document doc){
		
		//Start parsing process
		documentStr = namespaceMatch (documentStr,doc);
		//first find all the namespaces and build them
		nodeMatch(documentStr,doc);
		//build the nodes
		return (doc);
		
	}
	
	
	//////////varia////////////
	//reads file
	protected String readFile(String path) {
		//we borrowed this from the code that was provided to us
		try {
			
			File file = new File (path);
			FileReader fReader = new FileReader(file);
			BufferedReader in = new BufferedReader(fReader);
			String inputLine;
			StringBuffer strDocument = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				strDocument.append(inputLine);
			}
			fReader.close();
			return strDocument.toString();
		}
		catch(FileNotFoundException ex) {  //exception handler
		System.out.println("The specified file was not found at "+ path);
		return "";
		}
		
		catch(IOException ex) {         //exception handler no.2
		System.out.println("IOException occured while reading from file "+path);
		}       
		return "Nothing to return..";
	}
	
	
	
	//find all namespaces in string 
	public String namespaceMatch(String str,Document doc) {
		Pattern nmP = Pattern.compile("xmlns:([a-zA-z_]+)([a-zA-Z_0-9]*)(=\")([\\p{Alnum}\\p{Punct}]+)(\")([\\s]*)");
		Matcher nmM = nmP.matcher(str);
		
		//everytime you find one add it in list and remove from string...
		while( nmM.find() ) {
			Namespace newNm = new Namespace (nmM.group(1)+nmM.group(2),nmM.group(4));
			doc.addNamespace(newNm);
			str = str.replace(nmM.group(),"");
			
			//substruct group from string
		}
		return (str);
	}
	
	
	
	private void nodeMatch(String str, Document doc) {
		boolean nmFlag = false;  //check if an namespace exists
		char check;                //check if string ends with '/>'
		String attribz;            //attributes
		Namespace nmToAdd;         //namespace
		
		current = doc.getRootNode();        //start from root populating the tree
		Pattern nodeP = Pattern.compile("<(/?)([a-zA-Z_]+)([a-zA-Z_0-9]*)(:([a-zA-Z_]+)([a-zA-Z_0-9]*))?([^(>)]*)>([^<]*)");
		Matcher nodeM = nodeP.matcher(str);
		//group 1 : if it ends inline
		// group 2+3 : nodename if nmFlag=false, else it is prefix
		//group 4: not null if its namespace
		//group 5,6 : if nmFlag=true then it is nodename
		//group 7: attributes (might contain '/')
		//group 8: node text
		
		while(nodeM.find() ) {
			//check if namespace 
			if (nodeM.group(4)!=null){
				
				nmFlag = true;}
				else {
					nmFlag = false;}
					
					//check if node has attributes and keeps the last char
					if (!nodeM.group(7).isEmpty()){
						check = (nodeM.group(7)).charAt(nodeM.group(7).length()-1);
					}else{
						check = 'n';
					}
					
					//if last char is '\' then node is  in <node/> form
					if(check =='/'){
						//set node attributes
						attribz = nodeM.group(7);
						//remove the last char ('\')
						attribz = attribz.substring(0,attribz.length()-1);
						
						//create nodes depending on nmFlag
						//if nmFlag=true,
						if (nmFlag){
							//create node
							
							// add namespace if it exists
							if ((nmToAdd= doc.getNamespace(nodeM.group(2)+nodeM.group(3)))!=null){
								Node newNd = new Node(doc,"<"+nodeM.group(5)+nodeM.group(6)+attribz+">",current,nmToAdd);
								current.addChild(newNd);
							}else {
								Node newNd = new Node(doc,"<"+nodeM.group(5)+nodeM.group(6)+attribz+">",current);
								current.addChild(newNd);
							}
							//it's a <node/> node... continue from current 
						}
						//if not namespace just create node
						else{ 
							Node newNd = new Node(doc,"<"+nodeM.group(2)+nodeM.group(3)+attribz+">",current);
							current.addChild(newNd);
						}
						
					}
					//if it's a normal node do all the operations required (add it to tree set child,parent etc)
					else if (!("/".equals(nodeM.group(1)))){
						//same as before
						if (nmFlag){
							
							if ((nmToAdd= doc.getNamespace(nodeM.group(2)+nodeM.group(3)))!=null){
								Node newNd = new Node(doc,"<"+nodeM.group(5)+nodeM.group(6)+nodeM.group(7)+">",current,nmToAdd);
								current.addChild(newNd);
								current= newNd;
							}else {
								Node newNd = new Node(doc,"<"+nodeM.group(5)+nodeM.group(6)+nodeM.group(7)+">",current);
								current.addChild(newNd);
								current= newNd;
							}
							//continue from new node, it's in normal form
							
						}
						else{
							Node newNd = new Node(doc,"<"+nodeM.group(2)+nodeM.group(3)+nodeM.group(7)+">",current);
							current.addChild(newNd);
							current= newNd;
						}
						//set text to node <node>text</node>
						current.setText(nodeM.group(8));
						
					}
					//else continue as parent
					else {
						
						current = current.getParent();
					}
					
					
		}
		
		
	}
	
	
	
	private String getLocationByUrl(String location){
		try {
			// get URL content
			URL url = new URL(location);
			URLConnection conn = url.openConnection();
			
			// open the stream and put it into BufferedReader
			BufferedReader br = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
				
				String inputLine;
				
				//save to this filename
				String fileName = "file.xml";
				File file = new File(fileName);
				
				if (!file.exists()) {
					file.createNewFile();
				}
				
				//use FileWriter to write file
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				
				while ((inputLine = br.readLine()) != null) {
					bw.write(inputLine);
				}
				
				bw.close();
				br.close();
				location=fileName;
				
		} catch (MalformedURLException e) {
			System.out.println("MalformedURLException");
		} catch (IOException e) {
			System.out.println("IOException");

		}
		return (location);
	}
	
	
}


