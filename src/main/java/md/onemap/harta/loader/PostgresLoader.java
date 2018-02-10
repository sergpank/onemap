package md.onemap.harta.loader;

import md.onemap.exception.NotImplementedException;
import md.onemap.harta.db.DbHelper;
import md.onemap.harta.db.dao.*;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sergpank on 15.05.15.
 */
public class PostgresLoader extends AbstractLoader
{
  private Map<Long, OsmNode> nodes;
  private Map<Long, Border> borders;

  @Override
  public void load(String dbName)
  {
    minLon = Double.MAX_VALUE;
    minLat = Double.MAX_VALUE;
    maxLon = Double.MIN_VALUE;
    maxLat = Double.MIN_VALUE;
    loadNodes();
    loadBorders();
  }

  private void loadNodes()
  {
    nodes = new HashMap<>();
    Collection<OsmNode> osmNodes = new NodeDao().loadAll();
    for (OsmNode osmNode : osmNodes)
    {
      nodes.put(osmNode.getId(), osmNode);
      registerMinMax(osmNode.getLat(), osmNode.getLon());
    }
  }

  private void loadBorders()
  {
    borders = new HashMap<>();
    BorderDao borderDao = new BorderDao();
    Collection<Border> osmBorders = borderDao.loadAll();
    for (Border border : osmBorders)
    {
      borders.put(border.getId(), border);
    }
  }

  @Override
  public Map<Long, OsmNode> getNodes()
  {
    return nodes;
  }

  @Override
  public Map<Long, Highway> getHighways()
  {
    Collection<Highway> highways = new HighwayDao().loadAll();
    Map<Long, Highway> map = new HashMap<>(highways.size());
    for (Highway highway : highways)
    {
      map.put(highway.getId(), highway);
    }
    return map;
  }

  @Override
  public Map<Long, Building> getBuildings()
  {
    return new HashMap<>();
  }

  @Override
  public Map<Long, Leisure> getLeisure()
  {
    throw new NotImplementedException();
  }

  @Override
  public Map<Long, Natural> getNature()
  {
    throw new NotImplementedException();
  }

  @Override
  public Map<Long, Waterway> getWaterways()
  {
    throw new NotImplementedException();
  }

  @Override
  public Map<Long, Landuse> getLanduse()
  {
    throw new NotImplementedException();
  }

  @Override
  public Collection<Border> getBorders(int level, BoundsLatLon tileBounds)
  {
    return new BorderDao().load(level, tileBounds);
  }

  @Override
  public Collection<Highway> getHighways(int level, BoundsLatLon tileBounds)
  {
    return new HighwayDao().load(level, tileBounds);
  }

  @Override
  public Collection<Building> getBuildings(int level, BoundsLatLon tileBounds)
  {
    return new BuildingDao().load(level, tileBounds);
  }

  @Override
  public Collection<Leisure> getLeisure(int level, BoundsLatLon tileBounds)
  {
    throw new NotImplementedException();
  }

  @Override
  public Collection<Natural> getNature(int level, BoundsLatLon tileBounds)
  {
    throw new NotImplementedException();
  }

  @Override
  public Collection<Waterway> getWaterways(int level, BoundsLatLon tileBounds)
  {
    return new WaterwayDao().load(level, tileBounds);
  }

  @Override
  public Collection<Landuse> getLanduse(int level, BoundsLatLon tileBounds)
  {
    throw new NotImplementedException();
  }

  @Override
  public BoundsLatLon getBounds()
  {
    BoundsLatLon bounds = null;
    try (Connection connection = DbHelper.getConnection())
    {
      try (ResultSet rs = connection.createStatement().executeQuery("select min(lon), min(lat), max(lon), max(lat) from nodes"))
      {
        rs.next();
        double minLon = rs.getDouble(1);
        double minLat = rs.getDouble(2);
        double maxLon = rs.getDouble(3);
        double maxLat = rs.getDouble(4);
        bounds = new BoundsLatLon(minLat, minLon, maxLat, maxLon);
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return bounds;
  }
}
