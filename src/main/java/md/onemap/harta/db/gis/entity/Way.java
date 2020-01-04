package md.onemap.harta.db.gis.entity;

import md.onemap.harta.geometry.BoundsLatLon;

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

  public void setType(String type)
  {
    this.type = type;
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
