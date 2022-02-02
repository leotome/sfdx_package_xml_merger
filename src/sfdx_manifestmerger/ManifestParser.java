package sfdx_manifestmerger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManifestParser {

    private File File;
    private Boolean FinishedProcessing;
    private Map<String, List<String>> TypesAndItems;
    
    public ManifestParser(){
        this.FinishedProcessing = false;
        this.TypesAndItems = new HashMap<String, List<String>>();
    }

    public void setFile(File File){
        this.File = File;
    }

    public File getFile(){
        return this.File;
    }

    public Boolean IsProcessed(){
        return this.FinishedProcessing;
    }

    public List<String> getKeys(){
        List<String> keys = new ArrayList<String>();
        for(String key : this.TypesAndItems.keySet()){
            keys.add(key);
        }
        return keys;
    }

    public List<String> getValues(String key){
        Boolean ContainsKey = this.getKeys().contains(key);
        List<String> values = (ContainsKey == true) ? this.TypesAndItems.get(key) : null;
        return values;
    }

    public void processFile(){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            // optional, but recommended
            // process XML securely, avoid attacks like XML External Entities (XXE)
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
  
            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();
  
            Document doc = db.parse(this.File);
  
            // optional, but recommended
            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();
  
            // get <types> from xml
            NodeList list = doc.getElementsByTagName("types");
  
            for(int ParentIterator = 0; ParentIterator < list.getLength(); ParentIterator++) {
  
                Node ParentNode = list.item(ParentIterator);
  
                if(ParentNode.getNodeType() == Node.ELEMENT_NODE) {
  
                    Element ParentElement = (Element) ParentNode;

                    String MetadataType = ParentElement.getElementsByTagName("name").item(0).getTextContent();

                    NodeList MetadataAPINameNodeList = ParentElement.getElementsByTagName("members");

                    List<String> MetadataAPINameStringArray = new ArrayList<String>();

                    for(int ChildIterator = 0; ChildIterator < MetadataAPINameNodeList.getLength(); ChildIterator++) {
                        Node ChildNode = MetadataAPINameNodeList.item(ChildIterator);
                        if(ChildNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element ChildElement = (Element) ChildNode;
                            String MetadataAPINameValue = ChildElement.getTextContent();
                            MetadataAPINameStringArray.add(MetadataAPINameValue);
                        }
                    }

                    this.TypesAndItems.put(MetadataType, MetadataAPINameStringArray);
                }
            }
            this.FinishedProcessing = true;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }    
}