package md.harta.kml_parser;

import generated.NodeType;
import generated.Osm;
import generated.WayType;
import md.harta.util.xml.XmlUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sergpank on 18.02.2015.
 */
public class TxtParser extends AbstractParser {
  public static void main(String[] args) {
    try {
      List<String> strings = Arrays.asList("Greenland.txt", "australia.txt");
      List<NodeType> nodes = new ArrayList<>();
      List<WayType> ways = new ArrayList<>();
      for (String dataFile : strings) {
        List<NodeType> wayNodes = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(dataFile));
        String line;
        while ((line = br.readLine()) != null) {
          String[] lonlat = line.split(",");
          NodeType node = createNode(lonlat);
          wayNodes.add(node);
        }
        ways.add(createWay(wayNodes));
        nodes.addAll(wayNodes);
      }
      KmzParser kmzParser = new KmzParser();
      kmzParser.parse("ATA_adm0.kml");
      nodes.addAll(kmzParser.getAllNodes());
      ways.addAll(kmzParser.getAllWays());

      Osm osm = createOsm(nodes, ways);
      XmlUtil.marshalObject(osm, new File("OsmData/AnAuGl.osm"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
