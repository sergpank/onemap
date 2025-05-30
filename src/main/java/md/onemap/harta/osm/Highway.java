package md.onemap.harta.osm;

import md.onemap.harta.db.gis.entity.Node;
import md.onemap.harta.painter.HighwayType;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * Created by sergpank on 28.02.2015.
 */
public class Highway extends OsmWay {

  public static final String HIGHWAY = "highway";

  private HighwayType highwayType;

  @Override
  public String toString()
  {
    return name + super.toString();
  }

  public Highway(long id, List<Node> nodes, Element element) {
    super(id, nodes);

    NodeList tags = element.getElementsByTagName("tag");
    for (int i = 0; i < tags.getLength(); i++){
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      String value = item.getAttribute("v");
      switch(key){
        case HIGHWAY:
          highwayType = defineType(value);
          type = value;
          break;
        case NAME:
          name = value;
          break;
        case NAME_RU:
          nameRu = value;
          break;
        case NAME_OLD:
          nameOld = value;
          break;
      }
    }
  }

  public static HighwayType defineType(String type)
  {
    if (null == type)
    {
      return HighwayType.unclassified;
    }
    try
    {
      return HighwayType.valueOf(type);
    }
    catch (IllegalArgumentException e)
    {
      return HighwayType.unclassified;
    }
  }

  public Highway(Long id, String name, String nameRu, String nameOld, String type, List<Node> nodes)
  {
    super(id, nodes);
    this.name = name;
    this.nameRu = nameRu;
    this.nameOld = nameOld;
    this.type = type;
    this.highwayType = defineType(type);
  }

  public HighwayType getHighwayType()
  {
    return highwayType;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Highway)) return false;
    if (!super.equals(o)) return false;

    Highway highway = (Highway) o;

    if (name != null ? !name.equals(highway.name) : highway.name != null) return false;
    if (type != null ? !type.equals(highway.type) : highway.type != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (name == null ? 0 : name.hashCode());
    result = 31 * result + (type == null ? 0 : type.hashCode());
    return result;
  }
}
