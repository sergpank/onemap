package md.harta.db;

import md.harta.osm.Building;
import md.harta.osm.OsmNode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by sergpank on 23.04.15.
 */
public class BuildingDao
{
  public static final String INSERT_SQL = "INSERT INTO buildings " +
      "(building_id, housenumber, height, street, design, levels, building_nodes, min_lat, max_lat, min_lon, max_lon) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
  private Connection con;

  public BuildingDao(Connection con)
  {
    this.con = con;
  }

  public void save(Building building)
  {
    try(PreparedStatement pStmt = con.prepareStatement(INSERT_SQL))
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
      pStmt.setArray(pos++, con.createArrayOf("bigint", nodeIds));
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
}
