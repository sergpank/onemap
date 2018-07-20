package md.onemap.harta.loader;

import md.onemap.harta.db.gis.entity.Member;
import md.onemap.harta.db.gis.entity.Node;
import md.onemap.harta.db.gis.entity.Relation;
import md.onemap.harta.db.gis.entity.Way;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.OsmBounds;
import md.onemap.harta.util.XmlUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

  private Map<Long, Node> nodeMap = new HashMap<>();
  private Map<Long, Way> wayMap = new HashMap<>();
  private Map<Long, Relation> relationMap = new HashMap<>();

  private OsmBounds bounds;

  public OsmLoader() {
  }

  public Map<Long, Node> getNodes()
  {
    return nodeMap;
  }

  public Map<Long, Way> getWays()
  {
    return wayMap;
  }

  public Map<Long, Relation> getRelations()
  {
    return relationMap;
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

  private void readOsm(Document doc)
  {
    loadNodes(doc);
    LOG.info("Nodes loaded: " + nodeMap.size());

    loadWays(doc);
    LOG.info("Ways loaded: " + wayMap.size());

    loadRelations(doc);
    LOG.info("Relations loaded: " + relationMap.size());
  }

  private void loadRelations(Document doc)
  {
    LOG.info("Reading relations ...");
    NodeList relationList = XmlUtil.getNodeList(doc, "/osm/relation");
    for (int i = 0; i < relationList.getLength(); i++)
    {
      Element element = (Element)relationList.item(i);
      Long id = getId(element);
      Map<String, String> tags = getTags(element);

      NodeList elements = element.getElementsByTagName("member");

      Set<Member> members = IntStream
          .range(0, elements.getLength())
          .mapToObj(memberMapFunction(elements))
          .collect(Collectors.toSet());

      relationMap.put(id, new Relation(id, tags, members));
    }
  }

  private IntFunction<Member> memberMapFunction(NodeList members)
  {
    return m -> {
      Element e = (Element) members.item(m);
      String type = e.getAttribute("type");
      Long ref = Long.parseLong(e.getAttribute("ref"));
      String role = e.getAttribute("role");

      return new Member(type, ref, role);
    };
  }

  private void loadWays(Document doc)
  {
    LOG.info("Reading ways ...");
    NodeList wayList = XmlUtil.getNodeList(doc, "/osm/way");
    for (int i = 0; i < wayList.getLength(); i++)
    {
      Element element = (Element) wayList.item(i);
      Long id = getId(element);
      List<Node> nodes = getWayNodes(element);
      Map<String, String> tags = getTags(element);
      String type = Way.defineType(tags);

      wayMap.put(id, new Way(id, type, nodes, tags));
    }
  }

  private void   loadNodes(Document doc)
  {
    LOG.info("Reading nodes ...");
    NodeList nodeList = XmlUtil.getNodeList(doc, "/osm/node");
    for (int i = 0; i < nodeList.getLength(); i++)
    {
      Element element = (Element)nodeList.item(i);
      Long id = getId(element);
      Double lon = Double.parseDouble(element.getAttribute("lon"));
      Double lat = Double.parseDouble(element.getAttribute("lat"));
      Map<String, String> tags = getTags(element);

      nodeMap.put(id, new Node(id, lon, lat, tags));
      registerMinMax(lat, lon);
    }
  }

  private Long getId(Element element)
  {
    return Long.parseLong(element.getAttribute("id"));
  }

  private List<Node> getWayNodes(Element element)
  {
    NodeList wayNodeIds = element.getElementsByTagName("nd");

    List<Node> wayNodes = new ArrayList<>(wayNodeIds.getLength());
    for (int j = 0; j < wayNodeIds.getLength(); j++)
    {
      Element wayNode = (Element) wayNodeIds.item(j);
      long nodeId = Long.parseLong(wayNode.getAttribute("ref"));
      wayNodes.add(nodeMap.get(nodeId));
    }
    return wayNodes;
  }

  private Map<String, String> getTags(Element element)
  {
    NodeList tags = element.getElementsByTagName("tag");

    Map<String, String> tagMap = IntStream
        .range(0, tags.getLength())
        .mapToObj(tagMapFunction(tags))
        .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
    return tagMap;
  }

  private IntFunction<String[]> tagMapFunction(NodeList tags)
  {
    return i -> {
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      String value = item.getAttribute("v");
      return new String[] {key, value};
    };
  }

  public BoundsLatLon getBounds()
  {
    return new BoundsLatLon(minLat, minLon, maxLat, maxLon);
  }
}
