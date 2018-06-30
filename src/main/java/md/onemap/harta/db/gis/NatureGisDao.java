package md.onemap.harta.db.gis;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.Natural;
import md.onemap.harta.osm.OsmNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

public class NatureGisDao extends GisDao<Natural>
{
  private final Logger log = LoggerFactory.getLogger(NatureGisDao.class);
  public static final String TABLE_NAME = "gis.natural";

  private static final String INSERT_SQL = "INSERT INTO %s (id, type, name, name_ru, name_old, geometry) VALUES (?, ?, ?, ?, ?, %s)";

  private static final String SELECT_SQL = "SELECT id, type, name, name_ru, name_old, geometry " +
      "FROM %s " +
      "WHERE ST_Intersects(" +
      "ST_GeomFromText('Polygon((" +
      "%f %f," +
      "%f %f," +
      "%f %f," +
      "%f %f," +
      "%f %f" +
      "))'), geometry)";

  @Override
  public void save(Natural entity)
  {
    if (entity.getNodes().size() < 3)
    {
      log.error("Unable to save Natural with {} points: {} --> {}", entity.getNodes().size(), entity.getId(), entity.getName());
    }
    else
    {
      jdbcTemplate.update(String.format(INSERT_SQL, TABLE_NAME, createLineString(entity.getNodes())),
          entity.getId(), entity.getType(),
          entity.getName(), entity.getNameRu(), entity.getNameOld());
    }
  }

  @Override
  public void saveAll(Collection<Natural> entities)
  {
    entities.forEach(this::save);
  }

  @Override
  public Natural load(long id)
  {
    return null;
  }

  @Override
  public Collection<Natural> load(int zoomLevel, BoundsLatLon box)
  {
    double dLat = box.getMaxLat() - box.getMinLat();
    double dLon = box.getMaxLon() - box.getMinLon();
    String sql = String.format(SELECT_SQL, TABLE_NAME,
        box.getMinLon() - dLon, box.getMinLat() - dLat,
        box.getMinLon() - dLon, box.getMaxLat() + dLat,
        box.getMaxLon() + dLon, box.getMaxLat() + dLat,
        box.getMaxLon() + dLon, box.getMinLat() - dLat,
        box.getMinLon() - dLon, box.getMinLat() - dLat
    );

    return jdbcTemplate.query(sql, (rs, rowNum) ->
        {
          long id = rs.getLong("id");
          ArrayList<OsmNode> nodes = getOsmNodes(rs, "geometry");
          String type = rs.getString("type");
          String name = rs.getString("name");
          String nameRu = rs.getString("name_ru");
          String nameOld = rs.getString("name_old");
          return new Natural(id, nodes, type, name, nameRu, nameOld);
        }
    );
  }

  @Override
  public Collection<Natural> loadAll()
  {
    return null;
  }
}
