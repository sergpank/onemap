package md.harta.loader;

import java.util.Collection;
import md.harta.geometry.Bounds;
import md.harta.osm.Border;
import md.harta.osm.Building;
import md.harta.osm.Highway;
import md.harta.osm.OsmBounds;
import md.harta.osm.OsmNode;
import md.harta.projector.AbstractProjector;
import md.harta.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by sergpank on 20.01.2015.
 */
public class OsmLoader extends AbstractLoader{
  private Map<Long, OsmNode> nodeMap;
  private HashMap<Long, Highway> highwayMap;
  private HashMap<Long, Building> buildingMap;
  private HashMap<Long, Border> borderMap;
  private OsmBounds bounds;

  public OsmLoader() {
  }

  public Map<Long, OsmNode> getNodes() {
    return nodeMap;
  }

  public Map<Long, Highway> getHighways(AbstractProjector projector) {
    return highwayMap;
  }

  public Map<Long, Building> getBuildings(AbstractProjector projector) {
    return buildingMap;
  }

  @Override
  public Collection<Border> getBorders(int level, Bounds tileBounds, Map<Long, OsmNode> nodes, AbstractProjector projector)
  {
    throw new NotImplementedException();
  }

  @Override
  public Collection<Highway> getHighways(int level, Bounds tileBounds, Map<Long, OsmNode> nodeMap, AbstractProjector projector)
  {
    return getHighways(projector).values();
  }

  @Override
  public Collection<Building> getBuildings(int level, Bounds tileBounds, Map<Long, OsmNode> nodeMap, AbstractProjector projector)
  {
    return getBuildings(projector).values();
  }

  public Map<Long, Border> getBorders()
  {
    return borderMap;
  }

  public OsmBounds getOsmBounds() {
    return bounds;
  }

  public void load(String xmlFile, AbstractProjector projector) {
    minLon = Double.MAX_VALUE;
    minLat = Double.MAX_VALUE;
    maxLon = Double.MIN_VALUE;
    maxLat = Double.MIN_VALUE;

    this.nodeMap = new HashMap<>();
    this.highwayMap = new HashMap<>();
    this.buildingMap = new HashMap<>();
    this.borderMap = new HashMap<>();

    Document doc = XmlUtil.parseDocument(xmlFile);

    getNodes(doc);
    getWays(doc, projector);
    bounds = getBounds(doc);
//    System.out.printf("Min lat = %f\n" +
//                      "Max lat = %f\n" +
//                      "Min lon = %f\n" +
//                      "Max lon = %f\n\n", minLat, maxLat, minLon, maxLon);
  }

  private OsmBounds getBounds(Document doc) {
    NodeList nodeList = XmlUtil.getNodeList(doc, "/osm/bounds");
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

  private void getNodes(Document doc) {
    NodeList nodeList = XmlUtil.getNodeList(doc, "/osm/node");
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node item = nodeList.item(i);
      if (item.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) item;
        Long id = Long.parseLong(element.getAttribute("id"));
        Double lat = Double.parseDouble(element.getAttribute("lat"));
        Double lon = Double.parseDouble(element.getAttribute("lon"));
        nodeMap.put(id, new OsmNode(id, lat, lon));

        registerMinMax(lat, lon);
      }
    }
  }

  private void getWays(Document doc, AbstractProjector projector) {
    NodeList nodeList = XmlUtil.getNodeList(doc, "/osm/way");
    for (int i = 0; i < nodeList.getLength(); i++)
    {
      Node item = nodeList.item(i);
      if (item.getNodeType() == Node.ELEMENT_NODE)
      {
        Element element = (Element) item;
        Long id = Long.parseLong(element.getAttribute("id"));
        NodeList wayNodeIds = element.getElementsByTagName("nd");
        List<OsmNode> wayNodes = new ArrayList<>(wayNodeIds.getLength());
        for (int j = 0; j < wayNodeIds.getLength(); j++)
        {
          long nodeId = Long.parseLong(((Element) wayNodeIds.item(j)).getAttribute("ref"));
          wayNodes.add(nodeMap.get(nodeId));
        }
        if (isBuilding(element))
        {
          Building building = new Building(id, wayNodes, element, projector);
          buildingMap.put(id, building);
        }
        else if (isHighway(element))
        {
          Highway highway = new Highway(id, wayNodes, element, projector);
          highwayMap.put(id, highway);
        }
        else if (isBorder(element))
        {
          Border border = new Border(id, wayNodes, element, projector);
          borderMap.put(id, border);
        }
      }
    }
  }

  private boolean isHighway(Element element) {
    NodeList tags = getTags(element);
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
    NodeList tags = getTags(element);
    for(int i = 0; i < tags.getLength(); i++){
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      if (key.equals("building")){
        String value = item.getAttribute("v");
        return value.equals("yes") || value.equals("house");
      }
    }
    return false;
  }

  private boolean isBorder(Element element)
  {
    NodeList tags = getTags(element);
    for (int i = 0; i < tags.getLength(); i++)
    {
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      if (key.equals("border"))
      {
        return true;
      }
    }
    return false;
  }

  private NodeList getTags(Element element)
  {
    return element.getElementsByTagName("tag");
  }

  @Override
  public Bounds getBounds()
  {
    return new Bounds(null, minLat, minLon, maxLat, maxLon);
  }
}
