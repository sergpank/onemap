package md.onemap.harta.db.dao;

import md.onemap.exception.NotImplementedException;
import md.onemap.harta.db.DbHelper;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.OsmNode;
import md.onemap.harta.osm.Waterway;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by serg on 07-Aug-16.
 */
public class WaterwayDao extends Dao<Waterway>
{

  private static final String TABLE = "waterways";

  private static final String INSERT_SQL = "INSERT INTO waterways (waterway_id, waterway_type, waterway_name, waterway_nodes, min_lat, max_lat, min_lon, max_lon) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

  @Override
  public void save(Waterway entity)
  {
    try (Connection connection = DbHelper.getConnection())
    {
      PreparedStatement pStmt = connection.prepareStatement(INSERT_SQL);
      prepareStatement(pStmt, connection, entity);
      pStmt.execute();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public void saveAll(Collection<Waterway> entities)
  {
    throw new NotImplementedException();
  }

  private void prepareStatement(PreparedStatement pStmt, Connection connection, Waterway entity) throws SQLException
  {
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
  public Waterway load(long id)
  {
    throw new NotImplementedException();
  }

  @Override
  public Collection<Waterway> load(int zoomLevel, BoundsLatLon box)
  {
    List<Waterway> waterways = new ArrayList<>();
    try (Connection connection = DbHelper.getConnection())
    {
      PreparedStatement stmt = connection.prepareStatement(String.format(SELECT_TILE, TABLE));

      int i = 1;
      stmt.setDouble(i++, box.getMinLon());
      stmt.setDouble(i++, box.getMaxLon());

      stmt.setDouble(i++, box.getMinLat());
      stmt.setDouble(i++, box.getMaxLat());

      try (ResultSet resultSet = stmt.executeQuery())
      {
        while (resultSet.next())
        {
          Waterway waterway = readWaterway(resultSet);
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

  private Waterway readWaterway(ResultSet resultSet)
  throws SQLException
  {
    long id = resultSet.getLong("highway_id");
    String type = resultSet.getString("waterway_type");
    String name = resultSet.getString("waterway_name");
    String nameRu = resultSet.getString("waterway_name_ru");
    String nameOld = resultSet.getString("waterway_name_Old");
    Array wayNodes = resultSet.getArray("waterway_nodes");
    List<OsmNode> nodes = new NodeDao().loadNodes(wayNodes);

    return new Waterway(id, nodes, type, name, nameRu, nameOld);
  }

  @Override
  public Collection<Waterway> loadAll()
  {
    return null;
  }
}
