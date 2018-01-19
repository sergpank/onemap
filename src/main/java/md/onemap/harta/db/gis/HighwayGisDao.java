package md.onemap.harta.db.gis;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.Highway;
import md.onemap.harta.osm.OsmNode;
import org.postgis.PGgeometry;
import org.postgis.Point;

import java.sql.*;
import java.util.*;

/**
 * Created by serg on 11/7/15.
 */
public class HighwayGisDao extends GisDao<Highway>
{
  public static final String INSERT_SQL = "INSERT INTO gis.highways_gis " +
      "(highway_id, highway_name, highway_type, highway_geometry)" +
      " VALUES (?, ?, ?, %s);";

  public static final String SELECT_TILE = "SELECT highway_id, highway_name, highway_type, highway_geometry " +
      "FROM gis.highways_gis " +
      "WHERE " +
      "ST_Intersects(" +
      "ST_GeomFromText('Polygon((" +
      "%f %f," +
      "%f %f," +
      "%f %f," +
      "%f %f," +
      "%f %f" +
      "))'), highway_geometry)";

  public static final String SELECT_ALL = "SELECT highway_id, highway_name, highway_type, highway_geometry FROM gis.highways_gis";

  public HighwayGisDao(Connection connection)
  {
    super(connection);
  }

  @Override
  public void save(Highway highway)
  {
    if (highway.getNodes().size() < 2)
    {
      System.out.printf("Unable to save highway with 1 point: %s\n", highway.getName());
      return;
    }
    try(PreparedStatement pStmt = connection.prepareStatement(String.format(INSERT_SQL, createLineString(highway.getNodes()))))
    {
      int pos = 1;

      pStmt.setLong(pos++, highway.getId());
      pStmt.setString(pos++, highway.getName());
      pStmt.setString(pos++, highway.getType());
      pStmt.execute();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public void saveAll(List<Highway> highways)
  {
    highways.forEach(highway -> save(highway));
  }

  @Override
  public Highway load(long id)
  {
    return null;
  }

  @Override
  public Collection<Highway> load(int zoomLevel, BoundsLatLon box)
  {
    Set<Highway> highways = new HashSet<>();
    try (Statement stmt = connection.createStatement())
    {
      double dLat = box.getMaxLat() - box.getMinLat();
      double dLon = box.getMaxLon() - box.getMinLon();
      String sql = String.format(SELECT_TILE,
          box.getMinLon() - dLon, box.getMinLat() - dLat,
          box.getMinLon() - dLon, box.getMaxLat() + dLat,
          box.getMaxLon() + dLon, box.getMaxLat() + dLat,
          box.getMaxLon() + dLon, box.getMinLat() - dLat,
          box.getMinLon() - dLon, box.getMinLat() - dLat
      );
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next())
      {
        long id = rs.getLong("highway_id");
        String name = rs.getString("highway_name");
        String type = rs.getString("highway_type");

        ArrayList<OsmNode> nodes = new ArrayList<>();
        PGgeometry geometry = (PGgeometry)rs.getObject("highway_geometry");
        for (int i = 0; i < geometry.getGeometry().numPoints(); i++)
        {
          Point point = geometry.getGeometry().getPoint(i);
          nodes.add(new OsmNode(i, point.getY(), point.getX()));
        }

        highways.add(new Highway(id, name, type, nodes));
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return highways;
  }

  @Override
  public Collection<Highway> loadAll()
  {
    Set<Highway> highways = new HashSet<>();
    try (Statement stmt = connection.createStatement())
    {
      ResultSet rs = stmt.executeQuery(SELECT_ALL);
      while (rs.next())
      {
        long id = rs.getLong("highway_id");
        String name = rs.getString("highway_name");
        String type = rs.getString("highway_type");

        ArrayList<OsmNode> nodes = new ArrayList<>();
        PGgeometry geometry = (PGgeometry)rs.getObject("highway_geometry");
        for (int i = 0; i < geometry.getGeometry().numPoints(); i++)
        {
          Point point = geometry.getGeometry().getPoint(i);
          nodes.add(new OsmNode(i, point.getY(), point.getX()));
        }

        highways.add(new Highway(id, name, type, nodes));
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return highways;
  }

  @Override
  public BoundsLatLon getBounds()
  {
    return null;
  }
}
