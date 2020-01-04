package md.onemap.harta.osm;

import md.onemap.harta.db.gis.entity.Node;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.List;

public class Waterway extends OsmWay {

  public static final String WATERWAY = "waterway";

  public Waterway(long id, List<Node> nodes, String type, String name, String nameRu, String nameOld) {
    super(id, nodes, type, name, nameRu, nameOld);
  }

  public Waterway(Long id, List<Node> nodes, Element element) {
    super(id, nodes);

    NodeList tags = element.getElementsByTagName("tag");
    for (int i = 0; i < tags.getLength(); i++) {
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      switch (key) {
        case WATERWAY:
          type = item.getAttribute("v");
          break;
        case NAME:
          name = item.getAttribute("v");
          break;
        case NAME_RU:
          nameRu = item.getAttribute("v");
          break;
        case NAME_OLD:
          nameOld = item.getAttribute("v");
          break;
        default:
          break;
      }
    }
  }

  @Override
  public String toString() {
    return "Waterway{" +
        "type='" + type + '\'' +
        ", name='" + name + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    Waterway waterway = (Waterway) o;

    if (type != null ? !type.equals(waterway.type) : waterway.type != null) return false;
    return name != null ? name.equals(waterway.name) : waterway.name == null;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (type != null ? type.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    return result;
  }
}
