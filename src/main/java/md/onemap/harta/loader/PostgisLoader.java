package md.onemap.harta.loader;

import md.onemap.harta.db.DbHelper;
import md.onemap.harta.db.gis.entity.Node;
import md.onemap.harta.geometry.BoundsLatLon;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * Created by serg on 11/8/15.
 */
public class PostgisLoader extends AbstractLoader
{
  @Override
  public Map<Long, Node> getNodes()
  {
    return null;
  }

  @Override
  public void load(String dataSource)
  {

  }

  @Override
  public BoundsLatLon getBounds()
  {
    double minLonBuilding = 0;
    double minLatBuilding = 0;
    double maxLonBuilding = 0;
    double maxLatBuilding = 0;

    double minLonHighway = 0;
    double minLatHighway = 0;
    double maxLonHighway = 0;
    double maxLatHighway = 0;

    try (Connection connection = DbHelper.getConnection();
         Statement statement = connection.createStatement())
    {
      try (ResultSet rs = statement.executeQuery("select min(ST_XMin(building_geometry)), min(ST_YMin(building_geometry)), max(ST_XMax(building_geometry)), max(ST_YMax(building_geometry))from gis.buildings_gis"))
      {
        rs.next();
        minLonBuilding = rs.getDouble(1);
        minLatBuilding = rs.getDouble(2);
        maxLonBuilding = rs.getDouble(3);
        maxLatBuilding = rs.getDouble(4);
      }
      try (ResultSet rs = statement.executeQuery("select min(ST_XMin(highway_geometry)), min(ST_YMin(highway_geometry)), max(ST_XMax(highway_geometry)), max(ST_YMax(highway_geometry))from gis.highways_gis"))
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

    double minLat = Math.min(minLatBuilding, minLatHighway);
    double minLon = Math.min(minLonBuilding, minLonHighway);
    double maxLat = Math.max(maxLatBuilding, maxLatHighway);
    double maxLon = Math.max(maxLonBuilding, maxLonHighway);

    return new BoundsLatLon(minLat, minLon, maxLat, maxLon);
  }
}
