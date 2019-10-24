package md.onemap.harta.osm;

import md.onemap.harta.db.gis.entity.Node;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * Created by sergpank on 05.05.15.
 */
public class Boundary extends OsmWay
{
  public static final String BOUNDARY = "boundary";
  public static final String ADMIN_LEVEL = "admin_level";

  private String adminLevel;

  public Boundary(long id, Double minLat, Double minLon, Double maxLat, Double maxLon, List<Node> nodes, String name, String type)
  {
    super(id, nodes, minLat, minLon, maxLat, maxLon);
    this.name = name;
    this.type = type;
  }

  public Boundary(long id, List<Node> nodes, Element element)
  {
    super(id, nodes);

    NodeList tags = element.getElementsByTagName("tag");
    for (int i = 0; i < tags.getLength(); i++){
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      switch(key){
        case BOUNDARY:
          type = item.getAttribute("v");
          break;
        case ADMIN_LEVEL:
          adminLevel = item.getAttribute("v");
          break;
        default:
          break;
      }
    }
  }

  public String getAdminLevel()
  {
    return adminLevel;
  }

  public String getType()
  {
    return type;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (!(o instanceof Boundary)) return false;

    return super.equals(o);
  }

  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    return result;
  }

  @Override
  public String toString()
  {
    return "Boundary{" +
        "name='" + name + '\'' +
        ", type='" + type + '\'' +
        '}' + super.toString();
  }
}
