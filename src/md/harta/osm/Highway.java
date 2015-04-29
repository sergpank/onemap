package md.harta.osm;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * Created by sergpank on 28.02.2015.
 */
public class Highway extends OsmWay {

  public static final int WIDTH_METERS = 4;

  private String name;
  private String type;

  public Highway(long id, List<OsmNode> nodes, Element element) {
    super(id, nodes);

    NodeList tags = element.getElementsByTagName("tag");
    for (int i = 0; i < tags.getLength(); i++){
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      switch(key){
        case "highway":
          type = item.getAttribute("v");
          break;
        case "name":
          name = item.getAttribute("v");
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

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (name == null ? 0 : name.hashCode());
    return result;
  }
}
