package md.onemap.harta.db.gis;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.Natural;
import md.onemap.harta.osm.OsmNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class NatureGisDao extends GisDao<Natural>
{
  private final Logger log = LoggerFactory.getLogger(NatureGisDao.class);

  public static final String TABLE_NAME = "gis.natural";

  @Override
  public void save(Natural entity)
  {
    if (entity.getNodes().size() < 3)
    {
      log.error("Unable to save Natural with {} points: {} --> {}", entity.getNodes().size(), entity.getId(), entity.getName());
    }
    else
    {
      saveEntity(TABLE_NAME, entity, true);
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
    return loadTileEntities(box, TABLE_NAME, this::mapRow);
  }

  @Override
  public Collection<Natural> loadAll()
  {
    return loadAllEntities(TABLE_NAME, this::mapRow);
  }

  private Natural mapRow(ResultSet rs, int rowNum) throws SQLException
  {
    long id = rs.getLong("id");
    List<OsmNode> nodes = getOsmNodes(rs, "geometry");
    String type = rs.getString("type");
    String name = rs.getString("name");
    String nameRu = rs.getString("name_ru");
    String nameOld = rs.getString("name_old");

    return new Natural(id, nodes, type, name, nameRu, nameOld);
  }
}
