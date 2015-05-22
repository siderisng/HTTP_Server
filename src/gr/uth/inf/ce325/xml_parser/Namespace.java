//CE325 - Prwto project -  Fotis Tsokos 1679 Giorgos Sideris 1622


//This class represents the xml Namespace.
package gr.uth.inf.ce325.xml_parser;


public class Namespace {
	
	private  String prefix; //Namespace prefix
	private  String URI;    //Namespace URI
	
	////////////////Constructor
	
	public Namespace(String prefix, String uri){
		//creates Namespace object set prefix, URI
		this.prefix = prefix;
		URI = uri;
		
	}
	
	////////////Methods
	
	//getPrefix: Returns Prefix
	public String getPrefix(){
		
		return (prefix);
		
	}
	
	//getURI:Returns the URI
	public String getURI(){
		
		return (URI);
		
	}
	
	//toString:Returns a String representation blah blah
	@Override
	public String toString(){
		
		return ("Prefix :" +prefix+ " URI: "+ URI );
		
	}
	
	
	
}
