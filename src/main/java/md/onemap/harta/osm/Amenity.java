package md.onemap.harta.osm;

import md.onemap.harta.db.gis.entity.Node;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.Objects;

public class Amenity extends OsmWay
{
  public static final String AMENITY = "amenity";

  private String type;
  private String name;
  private String nameRu;

  public Amenity(long id, List<Node> nodes, Element element)
  {
    super(id, nodes);

    NodeList tags = element.getElementsByTagName("tag");
    for (int i = 0; i < tags.getLength(); i++) {
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      String value = item.getAttribute("v");
      switch (key) {
        case AMENITY:
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
    Amenity amenity = (Amenity) o;
    return Objects.equals(type, amenity.type) &&
        Objects.equals(name, amenity.name) &&
        Objects.equals(nameRu, amenity.nameRu);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(super.hashCode(), type, name, nameRu);
  }
}
