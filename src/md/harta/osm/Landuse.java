package md.harta.osm;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * Primitive Landuse - OsmWay that has only "landuse" tag.
 *
 * Created by serg on 08-Aug-16.
 */
public class Landuse extends OsmWay{

  public static final String LANDUSE = "landuse";

  private String type;

  public Landuse(long id, List<OsmNode> nodes, Element element) {
    super(id, nodes);

    NodeList tags = element.getElementsByTagName("tag");
    for (int i = 0; i < tags.getLength(); i++){
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      switch(key){
        case LANDUSE:
          type = item.getAttribute("v");
          break;
      }
    }
  }

  public Landuse(Long id, List<OsmNode> nodes, Double minLat, Double minLon, Double maxLat, Double maxLon) {
    super(id, nodes, minLat, minLon, maxLat, maxLon);
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
