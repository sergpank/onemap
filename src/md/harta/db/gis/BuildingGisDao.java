package md.harta.db.gis;

import md.harta.geometry.Bounds;
import md.harta.osm.Building;
import md.harta.osm.OsmNode;
import md.harta.projector.AbstractProjector;
import org.postgis.PGgeometry;
import org.postgis.Point;

import java.sql.*;
import java.util.*;

/**
 * Created by serg on 11/6/15.
 */
public class BuildingGisDao extends GisDao<Building>
{
  public static final String INSERT_SQL = "INSERT INTO buildings_gis " +
      "(building_id, housenumber, height, street, design, levels, building_geometry) " +
      "VALUES (?, ?, ?, ?, ?, ?, %s)";

  public static final String SELECT_TILE = "SELECT building_id, housenumber, height, street, design, levels, building_geometry " +
      "FROM buildings_gis " +
      "WHERE ST_Intersects(" +
      "ST_GeomFromText('Polygon((" +
      "%f %f," +
      "%f %f," +
      "%f %f," +
      "%f %f," +
      "%f %f" +
      "))'), building_geometry)";

  public BuildingGisDao(Connection connection)
  {
    super(connection);
  }

  @Override
  public void save(Building building)
  {
    if (building.getNodes().size() < 3)
    {
      System.out.printf("Unable to save building %s %s, not enough nodes: %d\n",
          building.getStreet(), building.getHouseNumber(), building.getNodes().size());
      return;
    }
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
      System.out.println(building);
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
  public Building load(long id)
  {

    return null;
  }

  @Override
  public Collection<Building> load(int zoomLevel, Bounds box, AbstractProjector projector)
  {
    Set<Building> buildings = new HashSet<>();
    try (Statement stmt = connection.createStatement())
    {
      String sql = String.format(SELECT_TILE,
          box.getMinLon(), box.getMinLat(),
          box.getMinLon(), box.getMaxLat(),
          box.getMaxLon(), box.getMaxLat(),
          box.getMaxLon(), box.getMinLat(),
          box.getMinLon(), box.getMinLat()
          );
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next())
      {
        long id = rs.getLong("building_id");
        String houseNumber = rs.getString("housenumber");
        String street = rs.getString("street");
        String height = rs.getString("height");
        int levels = rs.getInt("levels");
        String design = rs.getString("design");

        ArrayList<OsmNode> nodes = new ArrayList<>();
        PGgeometry geometry = (PGgeometry)rs.getObject("building_geometry");
        for (int i = 0; i < geometry.getGeometry().numPoints() - 1; i++)
        {
          Point point = geometry.getGeometry().getPoint(i);
          nodes.add(new OsmNode(i, point.getY(), point.getX()));
        }

        buildings.add(new Building(id, nodes, houseNumber, street, height, levels, design, projector));
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return buildings;
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
