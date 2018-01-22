package md.onemap.harta.db.dao;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.Highway;
import md.onemap.harta.osm.OsmNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

  private NodeDao nodeDao;

  public HighwayDao(Connection connection)
  {
    super(connection);
    this.nodeDao = new NodeDao(connection);
  }

  @Override
  public void save(Highway highway)
  {
    try (PreparedStatement pStmt = connection.prepareStatement(INSERT_SQL))
    {
      prepareStatement(pStmt, highway);
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
    try (PreparedStatement pStmt = connection.prepareStatement(INSERT_SQL))
    {
      int batchSize = 0;
      for (Highway highway : highways)
      {
        prepareStatement(pStmt, highway);
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

  private void prepareStatement(PreparedStatement pStmt, Highway highway) throws SQLException
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
    try (PreparedStatement stmt = connection.prepareStatement(String.format(SELECT_TILE, TABLE)))
    {
      int i = 1;
      stmt.setDouble(i++, box.getMinLon());
      stmt.setDouble(i++, box.getMaxLon());

      stmt.setDouble(i++, box.getMinLat());
      stmt.setDouble(i++, box.getMaxLat());

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
    ResultSet nodeSet = wayNodes.getResultSet();
    List<OsmNode> nodes = new ArrayList<>();
    while (nodeSet.next())
    {
      long nodeId = nodeSet.getLong(2);
      nodes.add(nodeDao.load(nodeId));
    }
    return new Highway(id, name, nameRu, oldName, type, nodes);
  }

  @Override
  public Collection<Highway> loadAll()
  {
    List<Highway> highways = new ArrayList<>();
    try (Statement st = connection.createStatement())
    {
      try (ResultSet rs = st.executeQuery(SELECT_ALL))
      {
        while (rs.next())
        {
          highways.add(readHighway(rs));
        }
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
    throw new NotImplementedException();
  }
}
