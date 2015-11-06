package md.harta.db;

import md.harta.geometry.Bounds;
import md.harta.osm.Highway;
import md.harta.osm.OsmNode;
import md.harta.projector.AbstractProjector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by serg on 11/7/15.
 */
public class HighwayGisDao extends GisDao<Highway>
{
  public static final String INSERT_SQL = "INSERT INTO highways_gis " +
      "(highway_id, highway_name, highway_type, highway_geometry)" +
      " VALUES (?, ?, ?, %s);";

  public HighwayGisDao(Connection connection)
  {
    super(connection);
  }

  @Override
  public void save(Highway highway)
  {
    try(PreparedStatement pStmt = connection.prepareStatement(String.format(INSERT_SQL, createLineString(highway.getNodes()))))
    {
      int pos = 1;

      pStmt.setLong(pos++, highway.getId());
      pStmt.setString(pos++, highway.getName());
      pStmt.setString(pos++, highway.getType());
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
    highways.forEach(highway -> save(highway));
  }

  @Override
  public Highway load(long id, AbstractProjector projector)
  {
    return null;
  }

  @Override
  public Collection<Highway> load(int zoomLevel, Bounds box, Map<Long, OsmNode> nodes, AbstractProjector projector)
  {
    return null;
  }

  @Override
  public Collection<Highway> loadAll(AbstractProjector projector)
  {
    return null;
  }

  @Override
  public Bounds getBounds()
  {
    return null;
  }
}
