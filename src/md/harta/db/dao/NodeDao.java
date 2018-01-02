package md.harta.db.dao;

import md.harta.geometry.BoundsLatLon;
import md.harta.osm.OsmNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sergpank on 21.04.15.
 */
public class NodeDao extends Dao<OsmNode>
{
  public static final String INSERT_SQL = "INSERT INTO nodes (node_id, lat, lon) VALUES (?, ?, ?)";
  public static final String SELECT_ALL = "SELECT node_id, lat, lon FROM nodes";
  public static final String SELECT_BOUNDS = "SELECT min(lat) AS min_lat, min(lon) AS min_lon, max(lat) AS max_lat, max(lon) AS max_lon FROM nodes;";
  public static final String SELECT_ID_SQL = "SELECT node_id FROM nodes WHERE lat = ? AND lon = ?";
  public static final String SELECT_NODE = "SELECT lat, lon FROM nodes WHERE node_id = ?";
  public static final String SELECT_NODES = "SELECT node_id, lat, lon FROM nodes WHERE node_id in (%s)";

  public NodeDao(Connection con)
  {
    super(con);
  }

  public void saveAll(List<OsmNode> nodes)
  {
    try (PreparedStatement insertStmt = connection.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS))
    {
      int count = 0;
      for (OsmNode node : nodes)
      {
        int pos = 1;
        insertStmt.setLong(pos++, node.getId());
        insertStmt.setDouble(pos++, node.getLat());
        insertStmt.setDouble(pos++, node.getLon());
        insertStmt.addBatch();
        if (count++ > 999)
        {
          count = 0;
          insertStmt.executeBatch();
        }
      }
      if (count > 0)
      {
        insertStmt.executeBatch();
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public void save(OsmNode node)
  {
    try (PreparedStatement insertStmt = connection.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS))
    {
      int pos = 1;
      insertStmt.setLong(pos++, node.getId());
      insertStmt.setDouble(pos++, node.getLat());
      insertStmt.setDouble(pos++, node.getLon());
      insertStmt.executeUpdate();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public OsmNode load(long id)
  {
    OsmNode node = null;
    try (PreparedStatement pStmt = connection.prepareStatement(SELECT_NODE))
    {
      pStmt.setLong(1, id);
      try (ResultSet rs = pStmt.executeQuery())
      {
        if (rs.next())
        {
          double lat = rs.getDouble("lat");
          double lon = rs.getDouble("lon");
          node = new OsmNode(id, lat, lon);
        }
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return node;
  }

  @Override
  public Collection<OsmNode> load(int zoomLevel, BoundsLatLon box)
  {
    throw new NotImplementedException();
  }

  @Override
  public Collection<OsmNode> loadAll()
  {
    List<OsmNode> nodes = new ArrayList<>();
    try (PreparedStatement pStmt = connection.prepareStatement(SELECT_ALL);
         ResultSet rs = pStmt.executeQuery())
    {
      while (rs.next())
      {
        long id = rs.getLong("node_id");
        double lat = rs.getDouble("lat");
        double lon = rs.getDouble("lon");
        nodes.add(new OsmNode(id, lat, lon));
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return nodes;
  }

  @Override
  public BoundsLatLon getBounds()
  {
    BoundsLatLon bounds = null;

    try (PreparedStatement pStmt = connection.prepareStatement(SELECT_BOUNDS))
    {
      try (ResultSet resultSet = pStmt.executeQuery())
      {
        resultSet.next();
        double minLat = resultSet.getDouble("min_lat");
        double minLon = resultSet.getDouble("min_lon");
        double maxLat = resultSet.getDouble("max_lat");
        double maxLon = resultSet.getDouble("max_lon");
        bounds = new BoundsLatLon(minLat, minLon, maxLat, maxLon);
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return bounds;
  }

  public List<OsmNode> loadNodes(List<Long> nodeIds)
  {
    List<OsmNode> nodes = new ArrayList<>();
    String collectIds = nodeIds.stream().map(Object::toString).collect(Collectors.joining(","));
    String sql = String.format(SELECT_NODES, collectIds);
    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(sql))
    {
      while (rs.next())
      {
        long id = rs.getLong("node_id");
        double lat = rs.getDouble("lat");
        double lon = rs.getDouble("lon");
        nodes.add(new OsmNode(id, lat, lon));
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return nodes;
  }
}
