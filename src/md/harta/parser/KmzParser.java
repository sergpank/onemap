package md.harta.parser;

import generated.NdType;
import generated.NodeType;
import generated.WayType;
import md.harta.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sergpank on 17.02.2015.
 */
public class KmzParser extends AbstractParser
{

  public static void main(String[] args)
  {
    KmzParser parser = new KmzParser();
    parser.parse("/home/sergpank/Downloads/UKR_adm0.kml");

//    parser.findLongestWay(parser);
//
//    XmlUtil.marshalObject(createOsm(parser.longestWayNodes, Arrays.asList(parser.longestWay)),
//        new File("OsmData/" + parser.placeName + ".osm"))
    XmlUtil.marshalObject(createOsm(parser.allNodes, parser.allWays), new File("Ukraine_0.osm"));
  }

  private List<NodeType> allNodes;
  private List<WayType> allWays;

  private WayType longestWay;
  private List<NodeType> longestWayNodes;

  private String placeName;

  public KmzParser()
  {
    allNodes = new ArrayList<>();
    allWays = new ArrayList<>();
  }

  public void parse(String xmlFile)
  {
    Document doc = XmlUtil.parseDocument(xmlFile);
    NodeList placeMarks = XmlUtil.getNodeList(doc, "/kml/Document/Placemark/name");

    for (int i = 0; i < placeMarks.getLength(); i++)
    {
      Node item = placeMarks.item(i);
      placeName = item.getFirstChild().getNodeValue();
//      System.out.println(placeName);

      List<NodeType> placeNodes = new ArrayList<>();
      List<WayType> placeWays = new ArrayList<>();

      NodeList placeMarkCoordinates = XmlUtil.getNodeList(item, "../MultiGeometry//coordinates");

      for (int j = 0; j < placeMarkCoordinates.getLength(); j++)
      {
        List<NodeType> polygonNodes = new ArrayList<>();
        String coordinates = placeMarkCoordinates.item(j).getFirstChild().getNodeValue();
        String[] split = coordinates.split("\n");

        for (int k = 0; k < split.length; k++)
        {
          if (split[k].trim().length() == 0)
          {
            continue;
          }
          String[] lonlat = split[k].split(",");
          polygonNodes.add(createNode(lonlat));
        }
        placeWays.add(createWay(polygonNodes, placeName));
        placeNodes.addAll(polygonNodes);
      }

      allNodes.addAll(placeNodes);
      allWays.addAll(placeWays);
    }
  }

  public List<NodeType> getAllNodes()
  {
    return allNodes;
  }

  public List<WayType> getAllWays()
  {
    return allWays;
  }

  private void findLongestWay(KmzParser parser)
  {
    Map<Long, NodeType> nodeMap = new HashMap<>();

    longestWay = parser.getAllWays().get(0);
    longestWayNodes = new ArrayList<>();

    for (NodeType node : parser.getAllNodes())
    {
      nodeMap.put(node.getId(), node);
    }
    for (WayType way : parser.getAllWays())
    {
      if (way.getNd().size() > longestWay.getNd().size())
      {
        longestWay = way;
      }
    }
    for (NdType nd : longestWay.getNd())
    {
      longestWayNodes.add(nodeMap.get(nd.getRef()));
    }
  }
}