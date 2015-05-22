//CE325 - Prwto project -  Fotis Tsokos 1679 Giorgos Sideris 1622


//List of attributes for a given xml node
package gr.uth.inf.ce325.xml_parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Attribute {
	
	private String nameAttr;//type of attribute
	private String valueAttr;//name of attribute
	private Namespace nmRef;//references namespace
	private Document docRef;//refernces document
	
	/////////////constructors//////////
	public Attribute(String attrStr){
		//finds attributes in given string
		Pattern attrP = Pattern.compile(" ([a-zA-z_]+)=\"([\\p{Alnum}\\p{Punct}]+)\"");
		Matcher attrM = attrP.matcher(attrStr);
		
		if (attrM.find()){
			//sets name and value of attr
			nameAttr = attrM.group(1);
			
			valueAttr=attrM.group(2) ;
		}
		
	}
	
	public Attribute(Document doc,String attrStr){
		//same here... links doc to attr
		Pattern attrP = Pattern.compile(" ([a-zA-z_]+)=\"([\\p{Alnum}\\p{Punct}]+)\"");
		Matcher attrM = attrP.matcher(attrStr);
		
		if (attrM.find()){
			
			nameAttr = attrM.group(1);
			valueAttr=attrM.group(2) ;
		}
		docRef = doc;
		
	}
	
	
	public Attribute(String name, String value){
		//same here... links doc to attr
		
		nameAttr = name;
		valueAttr = value ;
		
		
	}
	
	public Attribute(String name, String value, Document doc){
		//same here... links doc to attr
		
		nameAttr = name;
		valueAttr = value ;
		
		docRef = doc;
		
	}
	
	public Attribute(String name, String value, Document doc, Namespace nm){
		//same here... links doc to attr
		
		nameAttr = name;
		valueAttr = value ;
		
		docRef = doc;
		nmRef = nm;
	}
	
	public Attribute(String name, String value, Namespace nm){
		//same here... links doc to attr
		
		nameAttr = name;
		valueAttr = value ;
		
		nmRef = nm;
		
	}
	///////////Methods///////////////////
	
	
	////set/add
	
	//SetName:Sets attribute name
	public void setName (String name){
		nameAttr = name;
		
	}
	
	//setValue:Sets the attribute value
	public void setValue(String value){
		valueAttr = value;
		
	}
	
	
	//setNamespace:Sets the Namespace the attribute belongs to
	public void setNamespace(Namespace nm){
		
		nmRef = nm; 
		
	}
	
	///get/print
	//getName:Returns the attribute Name
	public String getName(){
		
		return (nameAttr);
		
	}
	
	//getValue:Returns the attribute value
	public String getValue(){
		
		
		return (valueAttr);
	}
	
	//getNamespace:Returns reference to the namespace attribute belongs to
	public Namespace getNamespace(){
		
		return (nmRef);
		
	}
	
	//toString:Returns a string representation of the attribute object
	@Override
	public String toString(){
		
		return ("Attribute name: "+nameAttr+" value: "+valueAttr+" referenced "
		+ "Namespace: " + nmRef + " referenced Document: "+docRef );
	}
	
	
	
	
}
