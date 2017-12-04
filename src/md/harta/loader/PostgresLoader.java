package md.harta.loader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import md.harta.db.*;
import md.harta.db.dao.*;
import md.harta.geometry.Bounds;
import md.harta.osm.*;
import md.harta.projector.AbstractProjector;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by sergpank on 15.05.15.
 */
public class PostgresLoader extends AbstractLoader
{
  private Map<Long, OsmNode> nodes;
  private Map<Long, Border> borders;
  private Connection connection;

  public PostgresLoader(String dbName)
  {
    this.connection = DbHelper.getConnection(dbName);
  }

  @Override
  public void load(String dbName, AbstractProjector projector)
  {
    minLon = Double.MAX_VALUE;
    minLat = Double.MAX_VALUE;
    maxLon = Double.MIN_VALUE;
    maxLat = Double.MIN_VALUE;
    connection = DbHelper.getConnection(dbName);
    loadNodes(connection);
    loadBorders(connection, projector);
  }

  private void loadNodes(Connection connection)
  {
    nodes = new HashMap<>();
    NodeDao nodeDao = new NodeDao(connection);
    Collection<OsmNode> osmNodes = nodeDao.loadAll(null);
    for (OsmNode osmNode : osmNodes)
    {
      nodes.put(osmNode.getId(), osmNode);
      registerMinMax(osmNode.getLat(), osmNode.getLon());
    }
  }

  private void loadBorders(Connection connection, AbstractProjector projector)
  {
    borders = new HashMap<>();
    BorderDao borderDao = new BorderDao(connection);
    Collection<Border> osmBorders = borderDao.loadAll(projector);
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
  public Map<Long, Highway> getHighways(AbstractProjector projector)
  {
    Collection<Highway> highways = new HighwayDao(connection).loadAll(projector);
    Map<Long, Highway> map = new HashMap<>(highways.size());
    for (Highway highway : highways)
    {
      map.put(highway.getId(), highway);
    }
    return map;
  }

  @Override
  public Map<Long, Building> getBuildings(AbstractProjector projector)
  {
    return new HashMap<>();
  }

  @Override
  public Map<Long, Leisure> getLeisure(AbstractProjector projector)
  {
    throw new NotImplementedException();
  }

  @Override
  public Map<Long, Natural> getNature(AbstractProjector projector)
  {
    throw new NotImplementedException();
  }

  @Override
  public Map<Long, Waterway> getWaterways(AbstractProjector projector) {
    throw new NotImplementedException();
  }

  @Override
  public Map<Long, Landuse> getLanduse(AbstractProjector projector) {
    throw new NotImplementedException();
  }

  @Override
  public Collection<Border> getBorders(int level, Bounds tileBounds, AbstractProjector projector)
  {
    return new BorderDao(connection).load(level, tileBounds, projector);
  }

  @Override
  public Collection<Highway> getHighways(int level, Bounds tileBounds, AbstractProjector projector)
  {
    return new HighwayDao(connection).load(level, tileBounds, projector);
  }

  @Override
  public Collection<Building> getBuildings(int level, Bounds tileBounds, AbstractProjector projector)
  {
    return new BuildingDao(connection).load(level, tileBounds, projector);
  }

  @Override
  public Collection<Leisure> getLeisure(int level, Bounds tileBounds, AbstractProjector projector)
  {
    throw new NotImplementedException();
  }

  @Override
  public Collection<Natural> getNature(int level, Bounds tileBounds, AbstractProjector projector)
  {
    throw new NotImplementedException();
  }

  @Override
  public Collection<Waterway> getWaterways(int level, Bounds tileBounds, AbstractProjector projector) {
    return new WaterwayDao(connection).load(level, tileBounds, projector);
  }

  @Override
  public Collection<Landuse> getLanduse(int level, Bounds tileBounds, AbstractProjector projector) {
    throw new NotImplementedException();
  }

  @Override
  public Bounds getBounds()
  {
    Bounds bounds = null;
    try(Statement st = connection.createStatement())
    {
      try (ResultSet rs = st.executeQuery("select min(lon), min(lat), max(lon), max(lat) from nodes"))
      {
        rs.next();
        double minLon = rs.getDouble(1);
        double minLat = rs.getDouble(2);
        double maxLon = rs.getDouble(3);
        double maxLat = rs.getDouble(4);
        bounds = new Bounds(null, minLat, minLon, maxLat, maxLon);
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return bounds;
  }
}
