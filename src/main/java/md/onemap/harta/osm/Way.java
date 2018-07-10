package md.onemap.harta.osm;

import md.onemap.harta.db.gis.WayGisDao;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.projector.MercatorProjector;
import md.onemap.harta.tile.TileBoundsCalculator;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Way
{
  private long id;
  private String type;
  private List<OsmNode> nodes;
  private Map<String, String> tags;
  private BoundsLatLon boundsLatLon;

  public Way(long id, String type, List<OsmNode> nodes, Map<String, String> tags)
  {
    this(id, type, nodes, tags, null);
  }

  public Way(long id, String type, List<OsmNode> nodes, Map<String, String> tags, BoundsLatLon boundsLatLon)
  {
    this.id = id;
    this.type = type;
    this.nodes = nodes;
    this.tags = tags;
    this.boundsLatLon = boundsLatLon;
  }

  public long getId()
  {
    return id;
  }

  public String getType()
  {
    return type;
  }

  public List<OsmNode> getNodes()
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
//    sb.append(", nodes=").append(nodes);
    sb.append('}');
    return sb.toString();
  }

  public static void main(String[] args)
  {
    BoundsLatLon tileBounds = new TileBoundsCalculator(512, new MercatorProjector(16)).getTileBounds(19008, 11521);
    Collection<Way> ways = new WayGisDao().load(16, tileBounds);

    ways.forEach(System.out::println);
  }
}
