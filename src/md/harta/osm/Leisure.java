package md.harta.osm;

import md.harta.projector.AbstractProjector;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * Created by serg on 6/28/16.
 */
public class Leisure extends OsmWay
{
  public static final String PARK = "park";
  String type;

  public Leisure(long id, List<OsmNode> nodes, Element element, AbstractProjector projector)
  {
    super(id, nodes, projector);

    NodeList tags = element.getElementsByTagName("tag");
    for (int i = 0; i < tags.getLength(); i++) {
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      String value = item.getAttribute("v");
      switch (key) {
        case "leisure":
          type = value;
          break;
        default:
          break;
      }
    }
  }

  public String getType()
  {
    return type;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    if (!super.equals(o))
    {
      return false;
    }

    Leisure leisure = (Leisure) o;

    return !(type != null ? !type.equals(leisure.type) : leisure.type != null);

  }

  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + (type != null ? type.hashCode() : 0);
    return result;
  }

  public boolean isPark()
  {
    return type.equalsIgnoreCase(PARK);
  }
}
