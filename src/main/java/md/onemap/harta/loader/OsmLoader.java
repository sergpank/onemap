package md.onemap.harta.loader;

import md.onemap.exception.NotImplementedException;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.*;
import md.onemap.harta.util.XmlUtil;

import org.postgis.PGgeometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by sergpank on 20.01.2015.
 */
public class OsmLoader extends AbstractLoader
{
  private static final Logger LOG = LoggerFactory.getLogger(OsmLoader.class);

  private Map<Long, OsmNode> nodeMap = new HashMap<>();
  private Map<Long, Way> wayMap = new HashMap<>();

  private HashMap<Long, Building> buildingMap = new HashMap<>();
  private HashMap<Long, Highway>  highwayMap = new HashMap<>();
  private HashMap<Long, Leisure>  leisureMap = new HashMap<>();
  private HashMap<Long, Natural>  natureMap = new HashMap<>();
  private HashMap<Long, Border>   borderMap = new HashMap<>();
  private HashMap<Long, Waterway> waterwayMap = new HashMap<>();
  private HashMap<Long, Landuse>  landuseMap = new HashMap<>();
  private HashMap<Long, Amenity>  amenityMap = new HashMap<>();
  private OsmBounds bounds;

  public OsmLoader() {
  }

  public Map<Long, OsmNode> getNodes() {
    return nodeMap;
  }

  public Map<Long, Way> getWays()
  {
    return wayMap;
  }

  public Map<Long, Highway> getHighways() {
    return highwayMap;
  }

  public Map<Long, Building> getBuildings() {
    return buildingMap;
  }

  @Override
  public Map<Long, Leisure> getLeisure()
  {
    return leisureMap;
  }

  @Override
  public Map<Long, Natural> getNature()
  {
    return natureMap;
  }

  @Override
  public Map<Long, Waterway> getWaterways() {
    return waterwayMap;
  }

  @Override
  public Map<Long, Landuse> getLanduse() {
    return landuseMap;
  }

  @Override
  public Collection<Border> getBorders(int level, BoundsLatLon tileBounds)
  {
    throw new NotImplementedException();
  }

  @Override
  public Collection<Highway> getHighways(int level, BoundsLatLon tileBounds)
  {
    return getHighways().values();
  }

  @Override
  public Collection<Building> getBuildings(int level, BoundsLatLon tileBounds)
  {
    return getBuildings().values();
  }

  @Override
  public Collection<Leisure> getLeisure(int level, BoundsLatLon tileBounds)
  {
    return leisureMap.values();
  }

  @Override
  public Collection<Natural> getNature(int level, BoundsLatLon tileBounds)
  {
    return natureMap.values();
  }

  @Override
  public Collection<Waterway> getWaterways(int level, BoundsLatLon tileBounds)
  {
    return waterwayMap.values();
  }

  @Override
  public Collection<Landuse> getLanduse(int level, BoundsLatLon tileBounds)
  {
    return landuseMap.values();
  }

  public Map<Long, Border> getBorders()
  {
    return borderMap;
  }

  public OsmBounds getOsmBounds() {
    return bounds;
  }

  public void load(String xmlFile) {
    minLon = Double.MAX_VALUE;
    minLat = Double.MAX_VALUE;
    maxLon = Double.MIN_VALUE;
    maxLat = Double.MIN_VALUE;

    Document doc = XmlUtil.parseDocument(xmlFile);

    nodeMap.clear();
    getNodes(doc);

    buildingMap.clear();
    highwayMap.clear();
    leisureMap.clear();
    natureMap.clear();
    waterwayMap.clear();
    borderMap.clear();
    landuseMap.clear();
    readOsm(doc);

    bounds = getBounds(doc);
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

  private void readOsm(Document doc) {
    NodeList nodeList = XmlUtil.getNodeList(doc, "/osm/way");
    for (int i = 0; i < nodeList.getLength(); i++)
    {
      Node item = nodeList.item(i);
      if (item.getNodeType() == Node.ELEMENT_NODE)
      {
        Element element = (Element) item;
        Long id = Long.parseLong(element.getAttribute("id"));
        List<OsmNode> nodes = getWayNodes(element);
        Map<String, String> tags = getTags(element);
        String type = Way.defineType(tags);

        wayMap.put(id, new Way(id, type, nodes, tags));

//        if (tags.containsKey(Building.BUILDING))
//        {
//          Building building = new Building(id, nodes, element);
//          buildingMap.put(id, building);
//        }
//        else if (tags.containsKey(Highway.HIGHWAY))
//        {
//          Highway highway = new Highway(id, nodes, element);
//          highwayMap.put(id, highway);
//        }
//        else if (tags.containsKey(Leisure.LEISURE))
//        {
//          Leisure leisure = new Leisure(id, nodes, element);
//          leisureMap.put(id, leisure);
//        }
//        else if (tags.containsKey(Natural.NATURAL))
//        {
//          Natural natural = new Natural(id, nodes, element);
//          natureMap.put(id, natural);
//        }
//        else if (tags.containsKey(Landuse.LANDUSE))
//        {
//          Landuse landuse = new Landuse(id, nodes, element);
//          landuseMap.put(id, landuse);
//        }
//        else if (tags.containsKey(Waterway.WATERWAY))
//        {
//          Waterway waterway = new Waterway(id, nodes, element);
//          waterwayMap.put(id, waterway);
//        }
//        else if (tags.containsKey(Border.BORDER))
//        {
//          Border border = new Border(id, nodes, element);
//          borderMap.put(id, border);
//        }
//        else
//        {
//          LOG.error("Unknown OsmWay type: id={}", id);
//        }
//
//        // Some buildings or landuse or whatever ... can be amenity too.
//        // It is ok to have duplicates
//        if (tags.containsKey(Amenity.AMENITY))
//        {
//          Amenity amenity = new Amenity(id, nodes, element);
//          amenityMap.put(id, amenity);
//        }
      }
    }
  }

  private List<OsmNode> getWayNodes(Element element)
  {
    NodeList wayNodeIds = element.getElementsByTagName("nd");

    List<OsmNode> wayNodes = new ArrayList<>(wayNodeIds.getLength());
    for (int j = 0; j < wayNodeIds.getLength(); j++)
    {
      long nodeId = Long.parseLong(((Element) wayNodeIds.item(j)).getAttribute("ref"));
      wayNodes.add(nodeMap.get(nodeId));
    }
    return wayNodes;
  }

  private Map<String, String> getTags(Element element)
  {
    NodeList tags = element.getElementsByTagName("tag");

    Map<String, String> tagMap = IntStream
        .range(0, tags.getLength())
        .mapToObj(mapFunction(tags))
        .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
    return tagMap;
  }

  private IntFunction<String[]> mapFunction(NodeList tags)
  {
    return i -> {
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      String value = item.getAttribute("v");
      return new String[] {key, value};
    };
  }

  @Override
  public BoundsLatLon getBounds()
  {
    return new BoundsLatLon(minLat, minLon, maxLat, maxLon);
  }
}
