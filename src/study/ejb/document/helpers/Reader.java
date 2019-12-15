/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package study.ejb.document.helpers;

import study.ejb.document.entity.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

/**
 *
 * @author dzmitry
 */
public class Reader {

    private org.w3c.dom.Document doc;
    
    protected List<Document> list = new ArrayList<>();
    
    private String xmlFile = "";
    
    private String xsdFile = "";

    public Reader(String xmlFile, String xsdFile) {
        try {

            this.xmlFile = xmlFile;
            this.xsdFile = xsdFile;
            
            URL xml = getClass().getClassLoader().getResource(this.xmlFile);
            URL xsd = getClass().getClassLoader().getResource(this.xsdFile);

            if (Reader.validateXMLSchema(xsd.getPath(), xml.getPath())) {
                File file = new File(xml.getPath());

                DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder();

                this.doc = dBuilder.parse(file);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * 
     */
    public void read() {
        NodeList documents = this.doc.getElementsByTagName("document");

        for (int count = 0; count < documents.getLength(); count++) {

            Node document = documents.item(count);

            // make sure it's element node.
            if (document.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) document;

                String id = eElement.getElementsByTagName("id").item(0).getTextContent();
                String title = eElement.getElementsByTagName("title").item(0).getTextContent();
                String content = eElement.getElementsByTagName("content").item(0).getTextContent();
                                
                this.list.add(new Document(Integer.parseInt(id), title, content));
            }

        }
    }
    
    /**
     * 
     * @param id
     * @return 
     */
    public Document search(String id) {
        
        Document result = new Document(0, null, null);
        for(Document doc: this.list) {
            if (Integer.parseInt(id) == doc.getId()) {
                result = doc;
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param id
     * @return 
     */
    public boolean delete(String id) {
        
        boolean result = false;
        
        Iterator itr = this.list.iterator(); 
        while (itr.hasNext()) 
        { 
            Document doc = (Document)itr.next();
            
            if (Integer.parseInt(id) == doc.getId()) {
                result = true;
                itr.remove(); 
            }
                
        } 
                
        return result;
    }

    /**
     * 
     * @param xsdPath
     * @param xmlPath
     * @return 
     */
    public static boolean validateXMLSchema(String xsdPath, String xmlPath) {

        try {
            SchemaFactory factory
                    = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlPath)));
        } catch (IOException | SAXException e) {
            System.out.println("Exception: " + e.getMessage());
            return false;
        }
        return true;
    }
    
    public List<Document> getList() {
        return this.list;
    }
    
    /**
     * 
     */
    public void save() {
        
        try {
        
            this.doc.getDocumentElement().setTextContent("");
            
            Element root = this.doc.getDocumentElement();
        
            for(Document doc: this.list) {

                Element d = this.doc.createElement("document");
                root.appendChild(d);

                Element id = this.doc.createElement("id");
                id.appendChild(this.doc.createTextNode(Integer.toString(doc.getId())));
                d.appendChild(id);

                Element title = this.doc.createElement("title");
                title.appendChild(this.doc.createTextNode(doc.getTitle()));
                d.appendChild(title);

                Element content = this.doc.createElement("content");
                content.appendChild(this.doc.createTextNode(doc.getContent()));
                d.appendChild(content);
            }

            DOMSource source = new DOMSource(this.doc);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            URL xml = getClass().getClassLoader().getResource(this.xmlFile);
            StreamResult result = new StreamResult(xml.getPath());

            transformer.transform(source, result);
        } catch (TransformerException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 
     * @param id
     * @param title
     * @param content 
     */
    public boolean add(int id, String title, String content) {
        return this.list.add(new Document(id, title, content));
    }
}
