import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**two exercises for you:
 * 1) print out only accession numbers encountered in attributes within SequenceLocation elements
 * 2) print out the value of only CitationTextElements

 * @author gares
 *
 */

//this class extends a class that implements the ContentHandler interface
public class MySAXHandler extends DefaultHandler{
	private int elementCount = 0;
	private int elementEndings = 0;
	
	//buffer used to store text that's encounterd between element tags
	private StringBuffer elementText = new StringBuffer();
	
	/*
	 * let's override the "hook" methods that get called by the parser when
	 * different document features are reached
	 * these methods are declared in ContentHandler
	 * empty, default implementations are provided in DefaultHandler (concrete base class)
	 */
	
	/**
	 * this method will be called by the XML reader when the start of the document is reached
	 */
	@Override
	public void startDocument() throws SAXException{
		System.out.println("The start of the document was reached");
	}
	
	/**
	 * this method will be called by the XML reader when the end of the document is reached
	 */
	@Override
	public void endDocument() throws SAXException{
		System.out.println("The end of the document was reached");
	}
	
	/**
	 * this method is called by the reader whenever a new element is encountered
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes){
		elementCount ++;
		System.out.println("\n\n\nThe start of element " + localName + " was reached, this is the "+ elementCount+"th element");
		//clear the buffer
		elementText.setLength(0);
		System.out.println("This element has the following attributes: ");
		//we are traversing and printing out the attributes
		for(int i = 0; i < attributes.getLength(); i++){
			System.out.println("Attribute: " + i + " is " + attributes.getLocalName(i)+ "=" + attributes.getValue(i));
		}
		
	}
	
	/**
	 * this method is called whenever the reader encounters the end of an element
	 */
	@Override
	public void endElement(String uri, String localName, String qName){
		elementEndings++;
		
		//do some additional processing if the element contained any text
		//in this case we are going to print it, but you can do whatever you want with it
		if(elementText.length() > 0){
			//print it out, and empty the buffer
			System.out.println("element text: "+ elementText.toString());
		}
		System.out.println("The end of element " + elementEndings+ " was reached");
	
	}
	
	/**
	 * this method will be called whenever ordinary text characters are encountered between element tags
	 */
	public void characters(char[] text, int begin, int length){
		//handle any characters we find by shoving them into the buffer
		elementText.append(text, begin, length);
	}

	public static void main(String[] args) {
		//show a dialog to let the user choose an XML file to parse
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(null);
		//pass the selected file to the loadSequenceFromFile method
		String filename = fc.getSelectedFile().getAbsolutePath();
		
		System.out.println("Starting to parse: " + filename);
		//in the java SAX api, you're supposed to get an instance of SAXParser from the SAXParserFactory
		//that means we need an instance of the factory first!
		//NOTE: SAXParserFactory.newInstance() is a static method that returns a SAXParserFactory instance
		SAXParserFactory spf = SAXParserFactory.newInstance();
		//set the namespace for the parser factory
		spf.setNamespaceAware(true);
		//use the factory instance to create a new SAXParser
		//note this might throw some exceptions that must be caught (or declare main as throwing etc.)
		SAXParser sp =null;
		try {
			sp = spf.newSAXParser();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		//create an XML reader for use with the XML parser, and our Handler (which we haven't declared yet)
		XMLReader xmlReader = null;
		try {
			xmlReader = sp.getXMLReader();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		//set a content handler that will interpret or "do something" with the elements in the XML document
		//first instantiate the handler class (this class itself)
		MySAXHandler handler = new MySAXHandler();
		//associate the handler with the xml reader
		xmlReader.setContentHandler(handler);
		//tell the xmlReader to parse the XML document
		try {
			xmlReader.parse(formatFileName(filename));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * clean up the filename in case there are operating system induced differences
	 */
	private static String formatFileName(String filename){
		//first get the path
		String path = new File(filename).getAbsolutePath();
		if(File.separatorChar != '/'){
			path = path.replace(File.separatorChar, '/');
		}
		//handle absolute paths
		if(!path.startsWith("/")){
			path = "/" + path;
		}
		return "file:"+path;
	}

}
