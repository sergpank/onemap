package md.onemap.harta.db.dao;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.Border;
import md.onemap.harta.osm.OsmNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by sergpank on 08.05.15.
 */
public class BorderDao extends Dao<Border>
{
  public static final String INSERT_SQL = "INSERT INTO " +
      "borders (name, min_level, max_level, border_nodes, min_lat, max_lat, min_lon, max_lon) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
  public static final String SELECT_ALL = "SELECT border_id, name, min_level, max_level, border_nodes, min_lat, max_lat, min_lon, max_lon FROM borders";
  public static final String SELECT_BOUNDS = "select min(min_lat) as min_lat, min(min_lon) as min_lon, max(max_lat) as max_lat, max(max_lon) as max_lon from borders";
  public static final String SELECT_TILE = "SELECT * FROM borders WHERE " +
      "min_level <= ? AND max_level >= ? " +
      "AND NOT( ((max_lon < ?) OR (min_lon > ?)) " +
      "     AND ((max_lat < ?) OR (min_lat > ?)) )";
  public static final String RAION = "raion";

  private NodeDao nodeDao;

  public BorderDao(Connection connection)
  {
    super(connection);
    this.nodeDao = new NodeDao(connection);
  }

  @Override
  public void save(Border border)
  {
    try (PreparedStatement insertStmt = connection.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS);)
    {
      Object[] nodeIds = new Long[border.getNodes().size()];
      for (int i = 0; i < border.getNodes().size(); i++)
      {
        nodeIds[i] = border.getNodes().get(i).getId();
      }

      int pos = 1;
      insertStmt.setString(pos++, border.getName());
      insertStmt.setInt(pos++, 1);
      insertStmt.setInt(pos++, 20);
      insertStmt.setArray(pos++, connection.createArrayOf("bigint", nodeIds));
      insertStmt.setDouble(pos++, border.getBounds().getMinLon());
      insertStmt.setDouble(pos++, border.getBounds().getMaxLon());
      insertStmt.setDouble(pos++, border.getBounds().getMinLat());
      insertStmt.setDouble(pos++, border.getBounds().getMaxLat());

      insertStmt.executeUpdate();

      ResultSet generatedKeys = insertStmt.getGeneratedKeys();
      if (generatedKeys.next())
      {
        border.setId(generatedKeys.getLong(1));
      }
      else
      {
        throw new RuntimeException("Border was not saved");
      }

      System.out.println(border.getName() + " - " + border.getId());
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public void saveAll(List<Border> entities)
  {
    throw new NotImplementedException();
  }

  @Override
  public Border load(long id)
  {
    return null;
  }

  @Override
  public Collection<Border> load(int zoomLevel, BoundsLatLon box)
  {
    long start = System.currentTimeMillis();
    List<Border> borders = new ArrayList<>();
    try (PreparedStatement stmt = connection.prepareStatement(SELECT_TILE))
    {
      int i = 1;
      stmt.setInt(i++, zoomLevel);
      stmt.setInt(i++, zoomLevel);
      stmt.setDouble(i++, box.getMinLon());
      stmt.setDouble(i++, box.getMaxLon());
      stmt.setDouble(i++, box.getMinLat());
      stmt.setDouble(i++, box.getMaxLat());

      try (ResultSet resultSet = stmt.executeQuery())
      {
        while (resultSet.next())
        {
          Border border = readBorder(resultSet);
          borders.add(border);
        }
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    long end = System.currentTimeMillis();
    System.out.println("Borders select: " + (end - start) + " ms");
    return borders;
  }

  @Override
  public Collection<Border> loadAll()
  {
    List<Border> borders = new ArrayList<>();
    try (PreparedStatement pStmt = connection.prepareStatement(SELECT_ALL);
         ResultSet rs = pStmt.executeQuery())
    {
      while (rs.next())
      {
        Border border = readBorder(rs);
        borders.add(border);
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return borders;
  }

  private Border readBorder(ResultSet rs) throws SQLException
  {
    // border_id, name, min_level, max_level, border_nodes, min_lat, max_lat, min_lon, max_lon
    List<OsmNode> nodes = new ArrayList<>();

    long id = rs.getLong("border_id");
    double minLat = rs.getDouble("min_lat");
    double minLon = rs.getDouble("min_lon");
    double maxLat = rs.getDouble("max_lat");
    double maxLon = rs.getDouble("max_lon");
    String name = rs.getString("name");
    Array borderNodes = rs.getArray("border_nodes");
    try (ResultSet nodeSet = borderNodes.getResultSet())
    {
      while (nodeSet.next())
      {
        long nodeId = nodeSet.getLong(2);
        nodes.add(nodeDao.load(nodeId));
      }
    }
    return new Border(id, minLat, minLon, maxLat, maxLon, nodes, name, RAION);
  }

  @Override
  public BoundsLatLon getBounds()
  {
    BoundsLatLon bounds;

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
}
