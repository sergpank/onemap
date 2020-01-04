package md.onemap.experiments;

import md.onemap.harta.util.XmlUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class XmlAnalyzer
{
  public static void main(String[] args)
  {
    Document doc = XmlUtil.parseDocument("osm/Кишинев.osm");
    /*
    Result for Кишинев.osm:
        node = 264307
      bounds = 1
         way = 50131
    relation = 951
     */
    NodeList nodeList = XmlUtil.getNodeList(doc, "/osm/*");

    Map<String, Long> summary = new HashMap<>();

    IntStream.range(0, nodeList.getLength()).forEach(i -> {
      Node item = nodeList.item(i);
      String nodeName = item.getNodeName();
      Long cnt = summary.getOrDefault(nodeName, 0L);
      summary.put(nodeName, cnt + 1);
    });

    summary.entrySet().forEach(System.out::println);
  }
}
