package md.harta.osm;

import md.harta.projector.AbstractProjector;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * Created by serg on 6/28/16.
 */
public class Natural extends OsmWay
{
  String type;

  public Natural(long id, List<OsmNode> nodes, Element element, AbstractProjector projector)
  {
    super(id, nodes, projector);

    NodeList tags = element.getElementsByTagName("tag");
    for (int i = 0; i < tags.getLength(); i++) {
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      String value = item.getAttribute("v");
      switch (key) {
        case "natural":
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

    Natural natural = (Natural) o;

    return !(type != null ? !type.equals(natural.type) : natural.type != null);

  }

  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + (type != null ? type.hashCode() : 0);
    return result;
  }
}
