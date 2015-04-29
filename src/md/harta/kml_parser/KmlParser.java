package md.harta.kml_parser;

import generated.*;
import md.harta.util.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sergpank on 13.02.2015.
 */
public class KmlParser {

  private long nodeId;
  private long wayId;
  private BoundsType bounds;
  private Osm osm;

  public KmlParser() {
    this.nodeId = 1;
    this.bounds = new BoundsType();

    this.osm = new Osm();
    this.osm.setBounds(bounds);

    this.bounds.setMinlat(90.0);
    this.bounds.setMinlon(180.0);
    this.bounds.setMaxlat(-90.0);
    this.bounds.setMaxlon(-180.0);
  }

  public static void main(String[] args) {
    String kmlFile = "Kontinents.kml";
    KmlParser parser = new KmlParser();
    Map<String, List<NodeType>> continents = parser.loadContinents(kmlFile);
    List<NodeType> nodes = new ArrayList<>();
    List<WayType> ways = new ArrayList<>();

//    System.out.println(String.format("%.2f:%.2f %.2f:%.2f",
//        parser.bounds.getMinlat(), parser.bounds.getMinlon(),
//        parser.bounds.getMaxlat(), parser.bounds.getMaxlon()));

    for (Map.Entry<String, List<NodeType>> entry : continents.entrySet()) {
      nodes.addAll(entry.getValue());
      WayType way = new WayType();
      way.setId(parser.wayId++);
      List<NdType> ndList = new ArrayList<>();
      for (NodeType node : entry.getValue()) {
        NdType nd = new NdType();
        nd.setRef(node.getId());
        ndList.add(nd);
      }
      way.getNd().addAll(ndList);
      ways.add(way);
    }
    parser.osm.getNode().addAll(nodes);
    parser.osm.getWay().addAll(ways);

    XmlUtil.marshalObject(parser.osm, new File("kml.osm"));
  }

  private Map<String, List<NodeType>> loadContinents(String kmlFile) {
    Map<String, List<NodeType>> continents = new HashMap<>();
    Document doc = XmlUtil.parseDocument(kmlFile);
    NodeList nodeList = XmlUtil.getNodeList(doc, "kml/Folder/Placemark/MultiGeometry/LineString");
    for (int i = 0; i < nodeList.getLength(); i++) {
      Element continent = (Element) nodeList.item(i);
      String id = continent.getAttribute("id");
      NodeList coordinates = continent.getElementsByTagName("coordinates");
      for (int j = 0; j < coordinates.getLength(); j++) {
        List<NodeType> continentNodes = new ArrayList<>();
        Element coordinate = (Element) coordinates.item(j);
        String coordinateValue = coordinate.getFirstChild().getNodeValue();
        String[] nodeCoordinates = coordinateValue.split(" ");
        for (String nodeCoordinate : nodeCoordinates) {
          if (!nodeCoordinate.trim().isEmpty()) {
            String[] points = nodeCoordinate.split(",");
            double lat = Double.parseDouble(points[0].trim());
            double lon = Double.parseDouble(points[1].trim());

            updateBounds(lat, lon);

            NodeType continentNode = new NodeType();
            continentNode.setId(nodeId++);
            continentNode.setLat(lat);
            continentNode.setLon(lon);
            continentNodes.add(continentNode);
          }
        }
        continents.put(id, continentNodes);
      }
    }
    return continents;
  }

  private void updateBounds(double lat, double lon) {
    if(lat > bounds.getMaxlat()){
      bounds.setMaxlat(lat);
    }
    if (lat < bounds.getMinlat()){
      bounds.setMinlat(lat);
    }
    if(lon > bounds.getMaxlon()){
      bounds.setMaxlon(lon);
    }
    if(lon < bounds.getMinlon()){
      bounds.setMinlon(lon);
    }
  }
}
