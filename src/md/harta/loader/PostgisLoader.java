package md.harta.loader;

import md.harta.db.gis.BuildingGisDao;
import md.harta.db.DbHelper;
import md.harta.db.gis.HighwayGisDao;
import md.harta.geometry.Bounds;
import md.harta.osm.*;
import md.harta.projector.AbstractProjector;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;

/**
 * Created by serg on 11/8/15.
 */
public class PostgisLoader extends AbstractLoader
{
  private Connection connection;

  public PostgisLoader(String dbName)
  {
    this.connection = DbHelper.getConnection(dbName);;
  }

  @Override
  public Map<Long, OsmNode> getNodes()
  {
    return null;
  }

  @Override
  public void load(String dataSource, AbstractProjector projector)
  {

  }

  @Override
  public Map<Long, Highway> getHighways(AbstractProjector projector)
  {
    return null;
  }

  @Override
  public Map<Long, Building> getBuildings(AbstractProjector projector)
  {
    return null;
  }

  @Override
  public Map<Long, Leisure> getLeisure(AbstractProjector projector)
  {
    return null;
  }

  @Override
  public Map<Long, Natural> getNature(AbstractProjector projector)
  {
    return null;
  }

  @Override
  public Map<Long, Waterway> getWaterways(AbstractProjector projector) {
    return null;
  }

  @Override
  public Map<Long, Landuse> getLanduse(AbstractProjector projector) {
    throw new NotImplementedException();
  }

  @Override
  public Collection<Border> getBorders(int level, Bounds tileBounds, AbstractProjector projector)
  {
    return null;
  }

  @Override
  public Collection<Highway> getHighways(int level, Bounds tileBounds, AbstractProjector projector)
  {
    return new HighwayGisDao(connection).load(level, tileBounds, projector);
  }

  @Override
  public Collection<Building> getBuildings(int level, Bounds tileBounds, AbstractProjector projector)
  {
    return new BuildingGisDao(connection).load(level, tileBounds, projector);
  }

  @Override
  public Collection<Leisure> getLeisure(int level, Bounds tileBounds, AbstractProjector projector)
  {
    return null;
  }

  @Override
  public Collection<Natural> getNature(int level, Bounds tileBounds, AbstractProjector projector)
  {
    return null;
  }

  @Override
  public Collection<Waterway> getWaterways(int level, Bounds tileBounds, AbstractProjector projector) {
    return null;
  }

  @Override
  public Collection<Landuse> getLanduse(int level, Bounds tileBounds, AbstractProjector projector) {
    return null;
  }

  @Override
  public Bounds getBounds()
  {
    double minLonBuilding = 0;
    double minLatBuilding = 0;
    double maxLonBuilding = 0;
    double maxLatBuilding = 0;

    double minLonHighway = 0;
    double minLatHighway = 0;
    double maxLonHighway = 0;
    double maxLatHighway = 0;

    try (Statement statement = connection.createStatement();)
    {
      try (ResultSet rs = statement.executeQuery("select min(ST_XMin(building_geometry)), min(ST_YMin(building_geometry)), max(ST_XMax(building_geometry)), max(ST_YMax(building_geometry))from buildings_gis"))
      {
        rs.next();
        minLonBuilding = rs.getDouble(1);
        minLatBuilding = rs.getDouble(2);
        maxLonBuilding = rs.getDouble(3);
        maxLatBuilding = rs.getDouble(4);
      }
      try (ResultSet rs = statement.executeQuery("select min(ST_XMin(highway_geometry)), min(ST_YMin(highway_geometry)), max(ST_XMax(highway_geometry)), max(ST_YMax(highway_geometry))from highways_gis"))
      {
        rs.next();
        minLonHighway = rs.getDouble(1);
        minLatHighway = rs.getDouble(2);
        maxLonHighway = rs.getDouble(3);
        maxLatHighway = rs.getDouble(4);
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return new Bounds(null, Math.min(minLatBuilding, minLatHighway), Math.min(minLonBuilding, minLonHighway),
        Math.max(maxLatBuilding, maxLatHighway), Math.max(maxLonBuilding, maxLonHighway));
  }
}
