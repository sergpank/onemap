package md.onemap.harta.util;

import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import generated.Osm;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;

/**
 * Created by sergpank on 13.02.2015.
 */
public class XmlUtil {

  private static XPath xPath = XPathFactory.newInstance().newXPath();

  public static Document parseDocument(String xmlFile){
    Document doc = null;
    try {
      doc = DocumentBuilderFactoryImpl.newInstance().newDocumentBuilder().parse(xmlFile);
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    }
    return doc;
  }

  public static NodeList getNodeList(Document doc, String query) {
    NodeList nodeList = null;
    try {
      nodeList = (NodeList) xPath.compile(query).evaluate(doc, XPathConstants.NODESET);
    } catch (XPathExpressionException e) {
      e.printStackTrace();
    }
    return nodeList;
  }

  public static NodeList getNodeList(Node node, String query) {
    NodeList nodeList = null;
    try {
      nodeList = (NodeList) xPath.compile(query).evaluate(node, XPathConstants.NODESET);
    } catch (XPathExpressionException e) {
      e.printStackTrace();
    }
    return nodeList;
  }

  public static void marshalObject(Object object, File outputFile){
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(Osm.class);
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(object, outputFile);
    } catch (JAXBException e) {
      e.printStackTrace();
    }
  }
}
