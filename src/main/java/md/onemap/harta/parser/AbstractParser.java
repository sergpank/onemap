package md.onemap.harta.parser;

import generated.KeyValueType;
import generated.NdType;
import generated.NodeType;
import generated.Osm;
import generated.WayType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergpank on 18.02.2015.
 */
public abstract class AbstractParser {
  protected static long nodeId = 1;
  protected static long wayId = 1;
  
  protected static NodeType createNode(String[] lonlat){
    NodeType node = new NodeType();
    node.setId(nodeId++);
    node.setLon(Double.parseDouble(lonlat[0].trim()));
    node.setLat(Double.parseDouble(lonlat[1].trim()));
    node.setVersion(1);
    node.setVisible(true);
    return node;
  }

  protected static WayType createWay(List<NodeType> nodes, String name){
    WayType way = new WayType();
    way.setId(wayId++);
    List<NdType> nds = new ArrayList<>();
    for (NodeType node : nodes) {
      NdType nd = new NdType();
      nd.setRef(node.getId());
      nds.add(nd);
    }
    way.getNd().addAll(nds);
    way.setVersion(1);
    way.setVisible(true);
    KeyValueType borderTag = new KeyValueType();
    borderTag.setK("border");
    borderTag.setV("local");
    way.getTag().add(borderTag);
    KeyValueType nameTag = new KeyValueType();
    nameTag.setK("name");
    nameTag.setV(name);
    way.getTag().add(nameTag);

    return way;
  }

  protected static Osm createOsm(List<NodeType> nodes, List<WayType> ways){
    Osm osm = new Osm();
    osm.getNode().addAll(nodes);
    osm.getWay().addAll(ways);
    osm.setVersion(0.6f);
    return osm;
  }
}
