package md.harta.db;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import md.harta.geometry.Bounds;
import md.harta.osm.Highway;
import md.harta.osm.OsmNode;
import md.harta.projector.AbstractProjector;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by sergpank on 22.04.15.
 */
public class HighwayDao extends Dao<Highway>
{
//  delete from nodes where node_id in (select unnest(highway_nodes) from highways where highway_id = 25652283)
//  delete from highways where highway_id = 25652283

  public static final String TABLE = "highways";

  public static final String INSERT_SQL = "INSERT INTO highways " +
      "(highway_id, highway_name, highway_type, highway_nodes, min_lat, max_lat, min_lon, max_lon)" +
      " VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

  public static final String SELECT_ALL = "SELECT * FROM highways";

//  public static final String SELECT_TILE = "SELECT * FROM highways " +
//      "WHERE (min_lon BETWEEN ? AND ? OR max_lon BETWEEN ? AND ?) " +
//      "AND (min_lat BETWEEN ? AND ? OR max_lat BETWEEN ? and ?)";

  public HighwayDao(Connection connection)
  {
    super(connection);
  }

  @Override
  public void save(Highway highway)
  {
    try (PreparedStatement pStmt = connection.prepareStatement(INSERT_SQL);)
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
  public void saveAll(List<Highway> highways)
  {
    try (PreparedStatement pStmt = connection.prepareStatement(INSERT_SQL);)
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
    pStmt.setString(pos++, highway.getType());
    pStmt.setArray(pos++, connection.createArrayOf("bigint", nodeIds));
    pStmt.setDouble(pos++, minLat);
    pStmt.setDouble(pos++, maxLat);
    pStmt.setDouble(pos++, minLon);
    pStmt.setDouble(pos++, maxLon);
  }

  @Override
  public Highway load(long id, AbstractProjector projector)
  {
    throw new NotImplementedException();
  }

  @Override
  public Collection<Highway> load(int zoomLevel, Bounds box, Map<Long, OsmNode> nodes, AbstractProjector projector)
  {
//    long start = System.currentTimeMillis();
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
          Highway highway = readHighway(resultSet, nodes, projector);
          highways.add(highway);
        }
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }

//    long end = System.currentTimeMillis();
//    System.out.println("Highway select: " + (end - start) + " ms");

    return highways;
  }

  private Highway readHighway(ResultSet resultSet, Map<Long, OsmNode> nodeMap, AbstractProjector projector)
      throws SQLException
  {
    long id = resultSet.getLong("highway_id");
    String name = resultSet.getString("highway_name");
    String type = resultSet.getString("highway_type");
    Array wayNodes = resultSet.getArray("highway_nodes");
    ResultSet nodeSet = wayNodes.getResultSet();
    List<OsmNode> nodes = new ArrayList<>();
    while (nodeSet.next())
    {
      long nodeId = nodeSet.getLong(2);
      nodes.add(nodeMap.get(nodeId));
    }
    return new Highway(id, name, type, nodes, projector);
  }

  @Override
  public Collection<Highway> loadAll(AbstractProjector projector)
  {
    Collection<OsmNode> nodes = new NodeDao(connection).loadAll(null);
    Map<Long, OsmNode> nodeMap = new HashMap<>(nodes.size());
    for (OsmNode node : nodes)
    {
      nodeMap.put(node.getId(), node);
    }
    List<Highway> highways = new ArrayList<>();
    try (Statement st = connection.createStatement())
    {
      try (ResultSet rs = st.executeQuery(SELECT_ALL))
      {
        while (rs.next())
        {
          highways.add(readHighway(rs, nodeMap, projector));
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
  public Bounds getBounds()
  {
    throw new NotImplementedException();
  }
}
