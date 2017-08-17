package md.harta.db.dao;

import md.harta.geometry.Bounds;
import md.harta.osm.OsmNode;
import md.harta.osm.Waterway;
import md.harta.projector.AbstractProjector;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by serg on 07-Aug-16.
 */
public class WaterwayDao extends Dao<Waterway> {

  private static final String TABLE = "waterways";

  private static final String INSERT_SQL = "INSERT INTO waterways (waterway_id, waterway_type, waterway_name, waterway_nodes, min_lat, max_lat, min_lon, max_lon) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

  public WaterwayDao(Connection connection) {
    super(connection);
  }

  @Override
  public void save(Waterway entity) {
    try (PreparedStatement pStmt = connection.prepareStatement(INSERT_SQL)){
      prepareStatement(pStmt, entity);
      pStmt.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void saveAll(List<Waterway> entities) {
    throw new NotImplementedException();
  }

  private void prepareStatement(PreparedStatement pStmt, Waterway entity) throws SQLException {
    int index = 1;
    //waterway_id, waterway_type, waterway_name, waterway_nodes, min_lat, max_lat, min_lon, max_lon
    pStmt.setLong(index++, entity.getId());
    pStmt.setString(index++, entity.getType());
    pStmt.setString(index++, entity.getName());
    pStmt.setArray(index++, connection.createArrayOf("bigint", getNodeIds(entity.getNodes())));
    pStmt.setDouble(index++, entity.getBounds().getMinLat());
    pStmt.setDouble(index++, entity.getBounds().getMaxLat());
    pStmt.setDouble(index++, entity.getBounds().getMinLon());
    pStmt.setDouble(index++, entity.getBounds().getMaxLon());
  }

  @Override
  public Waterway load(long id, AbstractProjector projector) {
    throw new NotImplementedException();
  }

  @Override
  public Collection<Waterway> load(int zoomLevel, Bounds box, Map<Long, OsmNode> nodes, AbstractProjector projector) {
    List<Waterway> waterways = new ArrayList<>();
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
          Waterway waterway = readWaterway(resultSet, nodes, projector);
          waterways.add(waterway);
        }
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }

    return waterways;
  }

  private Waterway readWaterway(ResultSet resultSet, Map<Long, OsmNode> nodeMap, AbstractProjector projector)
      throws SQLException {
    long id = resultSet.getLong("highway_id");
    String name = resultSet.getString("waterway_type");
    String type = resultSet.getString("waterway_name");
    Array wayNodes = resultSet.getArray("waterway_nodes");
    ResultSet nodeSet = wayNodes.getResultSet();
    List<OsmNode> nodes = new ArrayList<>();
    while (nodeSet.next())
    {
      long nodeId = nodeSet.getLong(2);
      nodes.add(nodeMap.get(nodeId));
    }
    return new Waterway(id, name, type, nodes, projector);
  }

  @Override
  public Collection<Waterway> loadAll(AbstractProjector projector) {
    return null;
  }

  @Override
  public Bounds getBounds() {
    return null;
  }
}
