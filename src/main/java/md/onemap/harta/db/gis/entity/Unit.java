package md.onemap.harta.db.gis.entity;

import md.onemap.harta.osm.*;

import java.util.Map;

public class Unit
{
  protected long id;
  private UnitType unitType;

  public Unit(long id, UnitType type)
  {
    this.id = id;
    this.unitType = type;
  }

  public long getId()
  {
    return id;
  }

  public UnitType getUnitType()
  {
    return unitType;
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
    else if (tags.containsKey(Boundary.BOUNDARY))
    {
      return Boundary.BOUNDARY;
    }
    else
    {
      return null;
    }
  }
}
