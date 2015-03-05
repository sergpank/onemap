package md.harta.util;

import md.harta.osm.*;
import md.harta.util.xml.XmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * Created by sergpank on 20.01.2015.
 */
public class OsmLoader {
  public static final int SCALE = 1;

  private Map<Long, OsmNode> nodeMap;
  private HashMap<Long, Highway> highwayMap;
  private HashMap<Long, Building> buildingMap;
  private OsmBounds bounds;

  private double minLon;
  private double minLat;
  private double maxLon;
  private double maxLat;

  public OsmLoader() {
  }

  public Map<Long, OsmNode> getNodeMap() {
    return nodeMap;
  }

  public Map<Long, Highway> getHighways() {
    return highwayMap;
  }

  public Map<Long, Building> getBuildings() {
    return buildingMap;
  }

  public OsmBounds getBounds() {
    return bounds;
  }

  public double getMinLon() {
    return minLon;
  }

  public double getMinLat() {
    return minLat;
  }

  public double getMaxLon() {
    return maxLon;
  }

  public double getMaxLat() {
    return maxLat;
  }

  public void load(String xmlFile) {
    minLon = Double.MAX_VALUE;
    minLat = Double.MAX_VALUE;
    maxLon = Double.MIN_VALUE;
    maxLat = Double.MIN_VALUE;

    this.nodeMap = new HashMap<>();
    this.highwayMap = new HashMap<>();
    this.buildingMap = new HashMap<>();

    getNodes(xmlFile);
    getWays(xmlFile);
    bounds = getBounds(xmlFile);
  }

  OsmBounds getBounds(String xmlFile) {
    NodeList nodeList = XmlUtil.getNodeList(xmlFile, "/osm/bounds");
    Element item = (Element) nodeList.item(0);
    if (item != null) {
      double minlat = Double.parseDouble(item.getAttribute("minlat"));
      double minlon = Double.parseDouble(item.getAttribute("minlon"));
      double maxlat = Double.parseDouble(item.getAttribute("maxlat"));
      double maxlon = Double.parseDouble(item.getAttribute("maxlon"));
      return new OsmBounds(minlat, minlon, maxlat, maxlon);
    }
    return null;
  }

  private void getNodes(String xmlFile) {
    NodeList nodeList = XmlUtil.getNodeList(xmlFile, "/osm/node");
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node item = nodeList.item(i);
      if (item.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) item;
        Long id = Long.parseLong(element.getAttribute("id"));
        Double lat = Double.parseDouble(element.getAttribute("lat"));
        Double lon = Double.parseDouble(element.getAttribute("lon"));
        nodeMap.put(id, new OsmNode(id, lat, lon));

        if (lat < minLat) {
          minLat = lat;
        }
        if (lat > maxLat) {
          maxLat = lat;
        }

        if (lon < minLon) {
          minLon = lon;
        }
        if (lon > maxLon) {
          maxLon = lon;
        }
      }
    }
  }

  private void getWays(String xmlFile) {
    NodeList nodeList = XmlUtil.getNodeList(xmlFile, "/osm/way");
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node item = nodeList.item(i);
      if (item.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) item;
        Long id = Long.parseLong(element.getAttribute("id"));
        NodeList wayNodeIds = element.getElementsByTagName("nd");
        List<OsmNode> wayNodes = new ArrayList<>(wayNodeIds.getLength());
        for (int j = 0; j < wayNodeIds.getLength(); j++) {
          long nodeId = Long.parseLong(((Element) wayNodeIds.item(j)).getAttribute("ref"));
          wayNodes.add(nodeMap.get(nodeId));
        }
        if (isBuilding(element)){
          Building building = new Building(id, wayNodes, element);
          buildingMap.put(id, building);
        } else if (isHighway(element)){
          Highway highway = new Highway(id, wayNodes, element);
          highwayMap.put(id, highway);
        }
      }
    }
  }

  private boolean isHighway(Element element) {
    NodeList tags = element.getElementsByTagName("tag");
    for (int i = 0; i < tags.getLength(); i++){
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      if (key.equals("highway")){
        return true;
      }
    }
    return false;
  }

  private boolean isBuilding(Element element) {
    NodeList tags = element.getElementsByTagName("tag");
    for(int i = 0; i < tags.getLength(); i++){
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      if (key.equals("building")){
        String value = item.getAttribute("v");
        return value.equals("yes");
      }
    }
    return false;
  }
}
