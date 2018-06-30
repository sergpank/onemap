package md.onemap.harta.db.dao;

import md.onemap.harta.db.DbHelper;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.Building;
import md.onemap.harta.osm.OsmNode;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by sergpank on 23.04.15.
 */
public class BuildingDao extends Dao<Building>
{
  public static final String INSERT_SQL = "INSERT INTO buildings " +
      "(building_id, housenumber, height, street, design, levels, building_nodes, min_lat, max_lat, min_lon, max_lon) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

  public static final String SELECT_ALL_SQL = "SELECT building_id, housenumber, height, street, design, levels, " +
      "building_nodes, min_lat, max_lat, min_lon, max_lon FROM buildings";

  public static final String SELECT_TILE = "SELECT * FROM buildings WHERE " +
      "NOT( ((max_lon < ?) OR (min_lon > ?)) " +
      " AND ((max_lat < ?) OR (min_lat > ?)) )";

//  public static final String SELECT_TILE = "SELECT * FROM buildings " +
//      "WHERE ((min_lon BETWEEN ? AND ?) OR (max_lon BETWEEN ? AND ?)) " +
//      "AND ((min_lat BETWEEN ? AND ?) OR (max_lat BETWEEN ? and ?))";

  public void save(Building building)
  {
    try (Connection connection = DbHelper.getConnection())
    {
      PreparedStatement pStmt = connection.prepareStatement(INSERT_SQL);
      int pos = 1;
      double minLat = 90, maxLat = -90, minLon = 180, maxLon = -180;
      Object[] nodeIds = new Long[building.getNodes().size()];
      for (int i = 0; i < building.getNodes().size(); i++)
      {
        OsmNode node = building.getNodes().get(i);
        nodeIds[i] = node.getId();
        maxLat = node.getLat() > maxLat ? node.getLat() : maxLat;
        minLat = node.getLat() < minLat ? node.getLat() : minLat;
        maxLon = node.getLon() > maxLon ? node.getLon() : maxLon;
        minLon = node.getLon() < minLon ? node.getLon() : minLon;
      }

      pStmt.setLong(pos++, building.getId());
      pStmt.setString(pos++, building.getHouseNumber());
      pStmt.setString(pos++, building.getHeight());
      pStmt.setString(pos++, building.getStreet());
      pStmt.setString(pos++, building.getDesign());
      pStmt.setInt(pos++, building.getLevels());
      pStmt.setArray(pos++, connection.createArrayOf("bigint", nodeIds));
      pStmt.setDouble(pos++, minLat);
      pStmt.setDouble(pos++, maxLat);
      pStmt.setDouble(pos++, minLon);
      pStmt.setDouble(pos, maxLon);
      pStmt.execute();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public void saveAll(Collection<Building> buildings)
  {
    try (Connection connection = DbHelper.getConnection())
    {
      PreparedStatement pStmt = connection.prepareStatement(INSERT_SQL);
      int batchSize = 0;
      for (Building building : buildings)
      {
        int pos = 1;
        double minLat = 90, maxLat = -90, minLon = 180, maxLon = -180;
        Object[] nodeIds = new Long[building.getNodes().size()];
        for (int i = 0; i < building.getNodes().size(); i++)
        {
          OsmNode node = building.getNodes().get(i);
          nodeIds[i] = node.getId();
          maxLat = node.getLat() > maxLat ? node.getLat() : maxLat;
          minLat = node.getLat() < minLat ? node.getLat() : minLat;
          maxLon = node.getLon() > maxLon ? node.getLon() : maxLon;
          minLon = node.getLon() < minLon ? node.getLon() : minLon;
        }

        pStmt.setLong(pos++, building.getId());
        pStmt.setString(pos++, building.getHouseNumber());
        pStmt.setString(pos++, building.getHeight());
        pStmt.setString(pos++, building.getStreet());
        pStmt.setString(pos++, building.getDesign());
        pStmt.setInt(pos++, building.getLevels());
        pStmt.setArray(pos++, connection.createArrayOf("bigint", nodeIds));
        pStmt.setDouble(pos++, minLat);
        pStmt.setDouble(pos++, maxLat);
        pStmt.setDouble(pos++, minLon);
        pStmt.setDouble(pos, maxLon);
        pStmt.execute();
        if (batchSize > 999)
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

  @Override
  public Building load(long id)
  {
    return null;
  }

  @Override
  public Collection<Building> load(int zoomLevel, BoundsLatLon box)
  {
    List<Building> buildings = new ArrayList<>();
    try (Connection connection = DbHelper.getConnection())
    {
      PreparedStatement pStmt = connection.prepareStatement(SELECT_TILE);

      int i = 1;
      pStmt.setDouble(i++, box.getMinLon());
      pStmt.setDouble(i++, box.getMaxLon());

      pStmt.setDouble(i++, box.getMinLat());
      pStmt.setDouble(i++, box.getMaxLat());
      try (ResultSet rs = pStmt.executeQuery())
      {
        while (rs.next())
        {
          long id = rs.getLong("building_id");
          Array wayNodes = rs.getArray("building_nodes");
          List<OsmNode> nodes = new NodeDao().loadNodes(wayNodes);
          String houseNumber = rs.getString("housenumber");
          String street = rs.getString("street");
          String height = rs.getString("height");
          int levels = rs.getInt("levels");
          String design = rs.getString("design");
          buildings.add(new Building(id, nodes, houseNumber, street, height, levels, design));
        }
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return buildings;
  }

  @Override
  public Collection<Building> loadAll()
  {
    return null;
  }
}
