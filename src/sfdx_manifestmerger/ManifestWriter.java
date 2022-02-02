package sfdx_manifestmerger;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ManifestWriter {

    public Map<String, List<String>> AllTypesAndItems;
    public String SFDXVersion;
    public String FilePath;

    public void writeFile(){
        try {

            File output = new File(this.FilePath);
 
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
 
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
 
            Document document = documentBuilder.newDocument();
 
            // root element
            Element root = document.createElement("Package");
            document.appendChild(root);

            Attr attr = document.createAttribute("xmlns");
            attr.setValue("http://soap.sforce.com/2006/04/metadata");
            root.setAttributeNode(attr);
 

 
            for(String TypeName : this.AllTypesAndItems.keySet()){
                // types element
                Element types = document.createElement("types");
                root.appendChild(types);
                for(String MetadataName : this.AllTypesAndItems.get(TypeName)){
                    // members element
                    Element members = document.createElement("members");
                    members.appendChild(document.createTextNode(MetadataName));
                    types.appendChild(members);
                }
                // name element
                Element name = document.createElement("name");
                name.appendChild(document.createTextNode(TypeName));
                types.appendChild(name);
            }
 
            // version element
            Element version = document.createElement("version");
            version.appendChild(document.createTextNode(this.SFDXVersion));
            root.appendChild(version);            
 
            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(output);
 
            // If you use
            // StreamResult result = new StreamResult(System.out);
            // the output will be pushed to the standard output ...
            // You can use that for debugging 
 
            transformer.transform(domSource, streamResult);
 
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
}