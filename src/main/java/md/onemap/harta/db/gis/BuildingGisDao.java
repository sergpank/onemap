package md.onemap.harta.db.gis;

import md.onemap.harta.db.DbHelper;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.Building;
import md.onemap.harta.osm.OsmNode;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by serg on 11/6/15.
 */
public class BuildingGisDao extends GisDao<Building>
{
  private static final Logger LOG = LoggerFactory.getLogger(BuildingGisDao.class);

  public static final String TABLE_NAME = "gis.buildings";

  public static final String INSERT_SQL = "INSERT INTO " + TABLE_NAME +
      " (id, housenumber, height, street, design, levels, geometry) " +
      " VALUES (?, ?, ?, ?, ?, ?, %s)";

  public static final String SELECT_TILE = "SELECT id, housenumber, height, street, design, levels, geometry " +
      " FROM " + TABLE_NAME +
      " WHERE ST_Intersects(" +
      " ST_GeomFromText('Polygon((" +
      "%f %f," +
      "%f %f," +
      "%f %f," +
      "%f %f," +
      "%f %f" +
      "))'), geometry)";

  public static final String SELECT_ALL = "SELECT id, housenumber, height, street, design, levels, geometry FROM " + TABLE_NAME;

  @Override
  public void save(Building building)
  {
    if (building.getNodes().size() < 3)
    {
      LOG.error("Unable to save building {} {} {}, not enough nodes: {}",
          building.getId(), building.getStreet(), building.getHouseNumber(), building.getNodes().size());
      return;
    }
    try (Connection connection = DbHelper.getConnection())
    {
      PreparedStatement pStmt = connection.prepareStatement(String.format(INSERT_SQL, createPolygon(building.getNodes())));

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
  }

  @Override
  public void saveAll(Collection<Building> buildings)
  {
    buildings.forEach(this::save);
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
    try (Connection connection = DbHelper.getConnection())
    {
      Statement stmt = connection.createStatement();
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
        long id = rs.getLong("id");
        String houseNumber = rs.getString("housenumber");
        String street = rs.getString("street");
        String height = rs.getString("height");
        int levels = rs.getInt("levels");
        String design = rs.getString("design");

        ArrayList<OsmNode> nodes = new ArrayList<>();
        PGgeometry geometry = (PGgeometry) rs.getObject("geometry");
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
    try (Connection connection = DbHelper.getConnection())
    {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(SELECT_ALL);
      while (rs.next())
      {
        long id = rs.getLong("id");
        String houseNumber = rs.getString("housenumber");
        String street = rs.getString("street");
        String height = rs.getString("height");
        int levels = rs.getInt("levels");
        String design = rs.getString("design");

        ArrayList<OsmNode> nodes = new ArrayList<>();
        PGgeometry geometry = (PGgeometry) rs.getObject("geometry");
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
}
