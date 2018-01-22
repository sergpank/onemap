package md.onemap.harta.db.gis;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.Building;
import md.onemap.harta.osm.OsmNode;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * Created by serg on 11/6/15.
 */
public class BuildingGisDao extends GisDao<Building>
{
  private static final Logger LOG = LoggerFactory.getLogger(BuildingGisDao.class);

  public static final String INSERT_SQL = "INSERT INTO gis.buildings_gis " +
      "(building_id, housenumber, height, street, design, levels, building_geometry) " +
      "VALUES (?, ?, ?, ?, ?, ?, %s)";

  public static final String SELECT_TILE = "SELECT building_id, housenumber, height, street, design, levels, building_geometry " +
      "FROM gis.buildings_gis " +
      "WHERE ST_Intersects(" +
      "ST_GeomFromText('Polygon((" +
      "%f %f," +
      "%f %f," +
      "%f %f," +
      "%f %f," +
      "%f %f" +
      "))'), building_geometry)";

  public static final String SELECT_ALL = "SELECT building_id, housenumber, height, street, design, levels, building_geometry FROM gis.buildings_gis";

  public BuildingGisDao(Connection connection)
  {
    super(connection);
  }

  @Override
  public void save(Building building)
  {
    if (building.getNodes().size() < 3)
    {
      LOG.error("Unable to save building %s %s, not enough nodes: %d\n",
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
      LOG.error("{} : {}", building, e.getMessage());
    }
    try
    {
      if (connection.isClosed()) {
        LOG.error("Connection is closed");
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public void saveAll(Collection<Building> buildings)
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
  public Collection<Building> load(int zoomLevel, BoundsLatLon box)
  {
    Set<Building> buildings = new HashSet<>();
    try (Statement stmt = connection.createStatement())
    {
      double dLat = box.getMaxLat() - box.getMinLat();
      double dLon = box.getMaxLon() - box.getMinLon();
      String sql = String.format(SELECT_TILE,
          box.getMinLon() - dLon, box.getMinLat() - dLat,
          box.getMinLon() - dLon, box.getMaxLat() + dLat,
          box.getMaxLon() + dLon, box.getMaxLat() + dLat,
          box.getMaxLon() + dLon, box.getMinLat() - dLat,
          box.getMinLon() - dLon, box.getMinLat() - dLat
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

        buildings.add(new Building(id, nodes, houseNumber, street, height, levels, design));
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
    Set<Building> buildings = new HashSet<>();
    try (Statement stmt = connection.createStatement())
    {
      ResultSet rs = stmt.executeQuery(SELECT_ALL);
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

        buildings.add(new Building(id, nodes, houseNumber, street, height, levels, design));
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return buildings;

  }

  @Override
  public BoundsLatLon getBounds()
  {
    return null;
  }
}
