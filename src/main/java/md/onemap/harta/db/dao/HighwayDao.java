package md.onemap.harta.db.dao;

import md.onemap.exception.NotImplementedException;
import md.onemap.harta.db.DbHelper;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.Highway;
import md.onemap.harta.osm.OsmNode;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by sergpank on 22.04.15.
 */
public class HighwayDao extends Dao<Highway>
{
  public static final String TABLE = "highways";

  public static final String INSERT_SQL = "INSERT INTO highways " +
      "(highway_id, highway_name, highway_name_ru, highway_name_old, highway_type, highway_nodes, min_lat, max_lat, min_lon, max_lon)" +
      " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

  public static final String SELECT_ALL = "SELECT * FROM highways";

//  public static final String SELECT_TILE = "SELECT * FROM highways " +
//      "WHERE (min_lon BETWEEN ? AND ? OR max_lon BETWEEN ? AND ?) " +
//      "AND (min_lat BETWEEN ? AND ? OR max_lat BETWEEN ? and ?)";

  @Override
  public void save(Highway highway)
  {
    try (Connection connection = DbHelper.getConnection())
    {
      PreparedStatement pStmt = connection.prepareStatement(INSERT_SQL);
      prepareStatement(pStmt, connection, highway);
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
    try (Connection connection = DbHelper.getConnection())
    {
      PreparedStatement pStmt = connection.prepareStatement(INSERT_SQL);
      int batchSize = 0;
      for (Highway highway : highways)
      {
        prepareStatement(pStmt, connection, highway);
        pStmt.addBatch();
        if (batchSize++ > 999)
        {
          batchSize = 0;
          pStmt.executeBatch();
        }
      }
      if (batchSize > 0)
      {
        pStmt.executeBatch();
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  private void prepareStatement(PreparedStatement pStmt, Connection connection, Highway highway) throws SQLException
  {
    int pos = 1;
    double minLat = 90, maxLat = -90, minLon = 180, maxLon = -180;

    Object[] nodeIds = new Long[highway.getNodes().size()];
    for (int i = 0; i < highway.getNodes().size(); i++)
    {
      OsmNode node = highway.getNodes().get(i);
      nodeIds[i] = node.getId();
      maxLat = node.getLat() > maxLat ? node.getLat() : maxLat;
      minLat = node.getLat() < minLat ? node.getLat() : minLat;
      maxLon = node.getLon() > maxLon ? node.getLon() : maxLon;
      minLon = node.getLon() < minLon ? node.getLon() : minLon;
    }

    pStmt.setLong(pos++, highway.getId());
    pStmt.setString(pos++, highway.getName());
    pStmt.setString(pos++, highway.getNameRu());
    pStmt.setString(pos++, highway.getNameOld());
    pStmt.setString(pos++, highway.getType());
    pStmt.setArray(pos++, connection.createArrayOf("bigint", nodeIds));
    pStmt.setDouble(pos++, minLat);
    pStmt.setDouble(pos++, maxLat);
    pStmt.setDouble(pos++, minLon);
    pStmt.setDouble(pos++, maxLon);
  }

  @Override
  public Highway load(long id)
  {
    throw new NotImplementedException();
  }

  @Override
  public Collection<Highway> load(int zoomLevel, BoundsLatLon box)
  {
    List<Highway> highways = new ArrayList<>();
    try (Connection connection = DbHelper.getConnection())
    {
      PreparedStatement stmt = connection.prepareStatement(String.format(SELECT_TILE, TABLE));

      // Bounding Box extension is necessary to fix road "cuts" on the tile border.
      // It happens because Road is DB is Just a thick line, but I draw it as a wide ribbon.
      // And in such case if real road is not on the tile - its extended version won't be drawn.
      double latExt = Math.abs(box.getMaxLat() - box.getMinLat()) / 8;
      double lonExt = Math.abs(box.getMaxLon() - box.getMinLon()) / 8;

      int i = 1;
      stmt.setDouble(i++, box.getMinLon() - lonExt);
      stmt.setDouble(i++, box.getMaxLon() + lonExt);

      stmt.setDouble(i++, box.getMinLat() - latExt);
      stmt.setDouble(i, box.getMaxLat() + latExt);

      try (ResultSet resultSet = stmt.executeQuery())
      {
        while (resultSet.next())
        {
          Highway highway = readHighway(resultSet);
          highways.add(highway);
        }
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }

    return highways;
  }

  private Highway readHighway(ResultSet resultSet)
  throws SQLException
  {
    long id = resultSet.getLong("highway_id");
    String name = resultSet.getString("highway_name");
    String nameRu = resultSet.getString("highway_name_ru");
    String oldName = resultSet.getString("highway_name_old");
    String type = resultSet.getString("highway_type");
    Array wayNodes = resultSet.getArray("highway_nodes");
    List<OsmNode> nodes = new NodeDao().loadNodes(wayNodes);
    return new Highway(id, name, nameRu, oldName, type, nodes);
  }

  @Override
  public Collection<Highway> loadAll()
  {
    List<Highway> highways = new ArrayList<>();
    try (Connection connection = DbHelper.getConnection())
    {
      ResultSet rs = connection.createStatement().executeQuery(SELECT_ALL);

      while (rs.next())
      {
        highways.add(readHighway(rs));
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return highways;
  }
}
