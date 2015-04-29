package md.harta.db;

import md.harta.osm.Highway;
import md.harta.osm.OsmNode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by sergpank on 22.04.15.
 */
public class HighwayDao
{
  public static final String INSERT_SQL = "INSERT INTO highways " +
      "(highway_id, highway_name, highway_type, highway_nodes, min_lat, max_lat, min_lon, max_lon)" +
      " VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
  private Connection con;

  public HighwayDao(Connection con)
  {
    this.con = con;
  }

  public void save(Highway highway)
  {
    try (PreparedStatement pStmt = con.prepareStatement(INSERT_SQL);)
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
      pStmt.setArray(pos++, con.createArrayOf("bigint", nodeIds));
      pStmt.setDouble(pos++, minLat);
      pStmt.setDouble(pos++, maxLat);
      pStmt.setDouble(pos++, minLon);
      pStmt.setDouble(pos++, maxLon);
      pStmt.execute();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
}
