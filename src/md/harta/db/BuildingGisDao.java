package md.harta.db;

import md.harta.geometry.Bounds;
import md.harta.osm.Building;
import md.harta.osm.OsmNode;
import md.harta.projector.AbstractProjector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by serg on 11/6/15.
 */
public class BuildingGisDao extends GisDao<Building>
{
  public static final String INSERT_SQL = "INSERT INTO buildings_gis " +
      "(building_id, housenumber, height, street, design, levels, building_geometry) " +
      "VALUES (?, ?, ?, ?, ?, ?, %s)";

  public BuildingGisDao(Connection connection)
  {
    super(connection);
  }

  @Override
  public void save(Building building)
  {
    try(PreparedStatement pStmt = connection.prepareStatement(String.format(INSERT_SQL, createPolygon(building.getNodes()))))
    {
      int pos = 1;

      pStmt.setLong(pos++, building.getId());
      pStmt.setString(pos++, building.getHouseNumber());
      pStmt.setString(pos++, building.getHeight());
      pStmt.setString(pos++, building.getStreet());
      pStmt.setString(pos++, building.getDesign());
      pStmt.setInt(pos++, building.getLevels());
      pStmt.execute();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public void saveAll(List<Building> buildings)
  {
    for (Building building : buildings)
    {
      save(building);
    }
  }

  @Override
  public Building load(long id, AbstractProjector projector)
  {
    return null;
  }

  @Override
  public Collection<Building> load(int zoomLevel, Bounds box, Map<Long, OsmNode> nodes, AbstractProjector projector)
  {
    return null;
  }

  @Override
  public Collection<Building> loadAll(AbstractProjector projector)
  {
    return null;
  }

  @Override
  public Bounds getBounds()
  {
    return null;
  }
}
