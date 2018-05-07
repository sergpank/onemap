package md.onemap.harta.db.gis;

import md.onemap.harta.db.DbHelper;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.Highway;
import md.onemap.harta.osm.OsmNode;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by serg on 11/7/15.
 */
public class HighwayGisDao extends GisDao<Highway>
{
  private static final Logger LOG = LoggerFactory.getLogger(HighwayGisDao.class);

  public static final String INSERT_SQL = "INSERT INTO gis.highways_gis " +
      "(highway_id, highway_name, highway_name_ru, highway_name_old, highway_type, highway_geometry)" +
      " VALUES (?, ?, ?, ?, ?, %s);";

  public static final String SELECT_TILE = "SELECT highway_id, highway_name, highway_name_ru, highway_name_old, highway_type, highway_geometry " +
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

  public static final String SELECT_ALL = "SELECT highway_id, highway_name, highway_name_ru, highway_name_old, highway_type, highway_geometry FROM gis.highways_gis";

  @Override
  public void save(Highway highway)
  {
    if (highway.getNodes().size() < 2)
    {
      LOG.error("Unable to save highway with {} point(s); name: {}; id: {}",
          highway.getNodes().size(), highway.getName(), highway.getId());
      return;
    }
    try (Connection connection = DbHelper.getConnection())
    {
      String lineString = createLineString(highway.getNodes());
      PreparedStatement pStmt = connection.prepareStatement(String.format(INSERT_SQL, lineString));
      int pos = 1;

      pStmt.setLong(pos++, highway.getId());
      pStmt.setString(pos++, highway.getName());
      pStmt.setString(pos++, highway.getNameRu());
      pStmt.setString(pos++, highway.getNameOld());
      pStmt.setString(pos++, highway.getType());
      pStmt.execute();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public void saveAll(Collection<Highway> highways)
  {
    highways.forEach(this::save);
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
    try (Connection connection = DbHelper.getConnection())
    {
      Statement stmt = connection.createStatement();

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
        readHighway(highways, rs);
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
    try (Connection connection = DbHelper.getConnection())
    {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(SELECT_ALL);
      while (rs.next())
      {
        readHighway(highways, rs);
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return highways;
  }

  private void readHighway(Set<Highway> highways, ResultSet rs) throws SQLException
  {
    long id = rs.getLong("highway_id");
    String name = rs.getString("highway_name");
    String nameRu = rs.getString("highway_name_ru");
    String oldName = rs.getString("highway_name_old");
    String type = rs.getString("highway_type");

    ArrayList<OsmNode> nodes = new ArrayList<>();
    PGgeometry geometry = (PGgeometry) rs.getObject("highway_geometry");
    for (int i = 0; i < geometry.getGeometry().numPoints(); i++)
    {
      Point point = geometry.getGeometry().getPoint(i);
      nodes.add(new OsmNode(i, point.getY(), point.getX()));
    }

    highways.add(new Highway(id, name, nameRu, oldName, type, nodes));
  }

  @Override
  public BoundsLatLon getBounds()
  {
    return null;
  }
}
