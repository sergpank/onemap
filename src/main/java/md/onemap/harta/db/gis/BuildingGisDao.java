package md.onemap.harta.db.gis;

import md.onemap.harta.db.DbHelper;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.Building;
import md.onemap.harta.osm.OsmNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Created by serg on 11/6/15.
 */
public class BuildingGisDao extends GisDao<Building>
{
  private static final Logger LOG = LoggerFactory.getLogger(BuildingGisDao.class);

  public static final String TABLE_NAME = "gis.buildings";

  private static final String INSERT_SQL = "INSERT INTO " + TABLE_NAME +
      " (id, housenumber, height, street, design, levels, geometry) " +
      " VALUES (?, ?, ?, ?, ?, ?, %s)";

  @Override
  public void save(Building building)
  {
    if (building.getNodes().size() < 3)
    {
      LOG.error("Unable to save building {} {} {}, not enough nodes: {}",
          building.getId(), building.getStreet(), building.getHouseNumber(), building.getNodes().size());
    }
    else
    {
      String geometry = createPolygon(building.getNodes());
      DbHelper.getJdbcTemplate().update(String.format(INSERT_SQL, geometry),
          building.getId(), building.getHouseNumber(), building.getHeight(),
          building.getStreet(), building.getDesign(), building.getLevels());
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
    return loadTileEntities(box, TABLE_NAME, this::mapRow);
  }

  @Override
  public Collection<Building> loadAll()
  {
    return loadAllEntities(TABLE_NAME, this::mapRow);
  }

  private Building mapRow(ResultSet rs, int rowNum) throws SQLException
  {
    long id = rs.getLong("id");
    String houseNumber = rs.getString("housenumber");
    String street = rs.getString("street");
    String height = rs.getString("height");
    int levels = rs.getInt("levels");
    String design = rs.getString("design");
    List<OsmNode> nodes = getOsmNodes(rs, "geometry");

    return new Building(id, nodes, houseNumber, street, height, levels, design);
  }
}
