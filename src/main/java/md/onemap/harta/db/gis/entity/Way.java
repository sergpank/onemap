package md.onemap.harta.db.gis.entity;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Way extends Unit
{
  private String type;
  private List<Node> nodes;
  private Map<String, String> tags;
  private BoundsLatLon boundsLatLon;

  public Way(long id, String type, List<Node> nodes, Map<String, String> tags)
  {
    this(id, type, nodes, tags, null);
  }

  public Way(long id, String type, List<Node> nodes, Map<String, String> tags, BoundsLatLon boundsLatLon)
  {
    super(id, UnitType.WAY);
    this.type = type;
    this.nodes = nodes;
    this.tags = tags;
    this.boundsLatLon = boundsLatLon;
  }

  public String getType()
  {
    return type;
  }

  public List<Node> getNodes()
  {
    return nodes;
  }

  public Map<String, String> getTags()
  {
    return tags;
  }

  public BoundsLatLon getBoundsLatLon()
  {
    return boundsLatLon;
  }

  public static String defineType(Map<String, String> tags)
  {
    if (tags.containsKey(Building.BUILDING))
    {
      return Building.BUILDING;
    }
    else if (tags.containsKey(Highway.HIGHWAY))
    {
      return Highway.HIGHWAY;
    }
    else if (tags.containsKey(Landuse.LANDUSE))
    {
      return Landuse.LANDUSE;
    }
    else if (tags.containsKey(Leisure.LEISURE))
    {
      return Leisure.LEISURE;
    }
    else if (tags.containsKey(Natural.NATURAL))
    {
      return Natural.NATURAL;
    }
    else if (tags.containsKey(Waterway.WATERWAY))
    {
      return Natural.NATURAL;
    }
    else if (tags.containsKey(Amenity.AMENITY))
    {
      return Amenity.AMENITY;
    }
    else if (tags.containsKey(Border.BORDER))
    {
      return Border.BORDER;
    }
    else
    {
      return null;
    }
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder("Way{");
    sb.append("id=").append(id);
    sb.append(", type='").append(type).append('\'');
    sb.append(", tags=").append(tags);
    sb.append('}');
    return sb.toString();
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
    Way way = (Way) o;
    return id == way.id;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(id);
  }
}
