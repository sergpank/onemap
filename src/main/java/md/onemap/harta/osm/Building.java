package md.onemap.harta.osm;

import md.onemap.harta.db.gis.entity.Node;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * Created by sergpank on 28.02.2015.
 */
public class Building extends OsmWay{
  public static final String BUILDING = "building";

  private String type;
  private String houseNumber;
  private String street;
  private String height;
  private String design;
  private int levels;

  public Building(long id, List<Node> nodes, Element element) {
    super(id, nodes);

    NodeList tags = element.getElementsByTagName("tag");
    for (int i = 0; i < tags.getLength(); i++) {
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      String value = item.getAttribute("v");
      switch (key) {
        case BUILDING:
          type = value;
          break;
        case "addr:housenumber":
          houseNumber = value;
          break;
        case "addr:street":
          street = value;
          break;
        case "building:height":
          height = value;
          break;
        case "building:levels":
          if (value.matches("\\d*"))
          {
            levels = Integer.valueOf(value);
          }
          break;
        case "design":
          design = value;
      }
    }
  }

  public Building(long id, List<Node> nodes, String houseNumber, String street, String height, int levels, String design) {
    super(id, nodes);
    this.houseNumber = houseNumber;
    this.street = street;
    this.height = height;
    this.levels = levels;
    this.design = design;
  }

  public String getHouseNumber() {
    return houseNumber;
  }

  public String getStreet() {
    return street;
  }

  public String getHeight() {
    return height;
  }

  public int getLevels() {
    return levels;
  }

  public String getDesign()
  {
    return design;
  }

  @Override
  public String toString()
  {
    return "Building{" +
        "houseNumber='" + houseNumber + '\'' +
        ", street='" + street + '\'' +
        ", height='" + height + '\'' +
        ", design='" + design + '\'' +
        ", levels=" + levels +
        "}\n" + super.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Building)) return false;
    if (!super.equals(o)) return false;

    Building building = (Building) o;

    if (levels != building.levels) return false;
    if (houseNumber != null ? !houseNumber.equals(building.houseNumber) : building.houseNumber != null) return false;
    if (street != null ? !street.equals(building.street) : building.street != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (houseNumber != null ? houseNumber.hashCode() : 0);
    result = 31 * result + (street != null ? street.hashCode() : 0);
    result = 31 * result + levels;
    return result;
  }
}
