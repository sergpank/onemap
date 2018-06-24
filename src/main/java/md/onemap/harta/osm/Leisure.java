package md.onemap.harta.osm;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * Created by serg on 6/28/16.
 */
public class Leisure extends OsmWay
{
  public static final String LEISURE = "leisure";

  public static final String PARK = "park";
  public static final String PLAYGROUND = "playground";
  public static final String STADIUM = "stadium";

  private String type;
  private String name;
  private String nameRu;

  public Leisure(long id, List<OsmNode> nodes, Element element)
  {
    super(id, nodes);

    NodeList tags = element.getElementsByTagName("tag");
    for (int i = 0; i < tags.getLength(); i++) {
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      String value = item.getAttribute("v");
      switch (key) {
        case LEISURE:
          type = value;
          break;
        case NAME:
          name = value;
          break;
        case NAME_RU:
          nameRu = value;
          break;
        default:
          break;
      }
    }
  }

  public Leisure(Long id, List<OsmNode> nodes, String type, String name, String nameRu)
  {
    super(id, nodes);
    this.type = type;
    this.name = name;
    this.nameRu = nameRu;
  }

  public String getType()
  {
    return type;
  }

  public String getName()
  {
    return name;
  }

  public String getNameRu()
  {
    return nameRu;
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
    return type.equals(PARK);
  }

  public boolean isPlayground()
  {
    return type.equals(PLAYGROUND);
  }

  public boolean isStadium()
  {
    return type.equals(STADIUM);
  }
}
