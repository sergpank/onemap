package md.harta.osm;

import md.harta.projector.AbstractProjector;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * Created by sergpank on 05.05.15.
 */
public class Border extends OsmWay
{
  public static final String BORDER = "border";
  private String name;
  private String type;

  public Border (long id, Double minLat, Double minLon, Double maxLat, Double maxLon, List<OsmNode> nodes, String name, String type)
  {
    super(id, nodes, minLat, minLon, maxLat, maxLon);
    this.name = name;
    this.type = type;
  }

  public Border(long id, List<OsmNode> nodes, Element element, AbstractProjector projector)
  {
    super(id, nodes, projector);

    NodeList tags = element.getElementsByTagName("tag");
    for (int i = 0; i < tags.getLength(); i++){
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      switch(key){
        case BORDER:
          type = item.getAttribute("v");
          break;
        case "name":
          name = item.getAttribute("v");
          break;
      }
    }
  }

  public String getName()
  {
    return name;
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
    if (!(o instanceof Border)) return false;
    if (!super.equals(o)) return false;

    Border border = (Border) o;

    if (name != null ? !name.equals(border.name) : border.name != null) return false;
    return !(type != null ? !type.equals(border.type) : border.type != null);
  }

  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (type != null ? type.hashCode() : 0);
    return result;
  }

  @Override
  public String toString()
  {
    return "Border{" +
        "name='" + name + '\'' +
        ", type='" + type + '\'' +
        '}' + super.toString();
  }
}
