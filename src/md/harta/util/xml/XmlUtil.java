package md.harta.util.xml;

import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import generated.Osm;
import org.w3c.dom.Document;
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
  public static NodeList getNodeList(String xmlFile, String query) {
    NodeList nodeList = null;
    try {
      XPath xPath = XPathFactory.newInstance().newXPath();
      Document doc = DocumentBuilderFactoryImpl.newInstance().newDocumentBuilder().parse(xmlFile);
      nodeList = (NodeList) xPath.compile(query).evaluate(doc, XPathConstants.NODESET);
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (XPathExpressionException e) {
      e.printStackTrace();
    }
    return nodeList;
  }

  public static NodeList getNodeList(Object source, String query) {
    NodeList nodeList = null;
    try {
      XPath xPath = XPathFactory.newInstance().newXPath();
      nodeList = (NodeList) xPath.compile(query).evaluate(source, XPathConstants.NODESET);
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
