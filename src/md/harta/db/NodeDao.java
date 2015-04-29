package md.harta.db;

import md.harta.osm.OsmNode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by sergpank on 21.04.15.
 */
public class NodeDao
{
  public static final String INSERT_SQL = "INSERT INTO nodes (node_id, lon, lat) VALUES (?, ?, ?)";
  private Connection con;

  public NodeDao(Connection con)
  {
    this.con = con;
  }

  public void save(OsmNode node)
  {
    try (PreparedStatement pStmt = con.prepareStatement(INSERT_SQL);)
    {
      pStmt.setLong(1, node.getId());
      pStmt.setDouble(2, node.getLat());
      pStmt.setDouble(3, node.getLon());
      pStmt.execute();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
}
