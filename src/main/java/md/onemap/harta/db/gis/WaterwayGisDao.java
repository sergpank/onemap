package md.onemap.harta.db.gis;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.OsmNode;
import md.onemap.harta.osm.Waterway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Created by serg on 07-Aug-16.
 */
public class WaterwayGisDao extends GisDao<Waterway>
{
  private static final Logger LOG = LoggerFactory.getLogger(WaterwayGisDao.class);

  public static final String TABLE_NAME = "gis.waterways";

  @Override
  public void save(Waterway entity) {
    if (entity.getNodes().size() < 2)
    {
      LOG.error("Unable to save waterway {} with {} nodes: {} -> {}", entity.getId(), entity.getNodes().size(), entity.getName(), entity.getType());
    }
    else
    {
      saveEntity(TABLE_NAME, entity, false);
    }
  }

  @Override
  public void saveAll(Collection<Waterway> entities) {
    entities.forEach(this::save);
  }

  @Override
  public Waterway load(long id) {
    return null;
  }

  @Override
  public Collection<Waterway> load(int zoomLevel, BoundsLatLon box) {
    return loadTileEntities(box, TABLE_NAME, this::mapRow);
  }

  @Override
  public Collection<Waterway> loadAll() {
    return loadAllEntities(TABLE_NAME, this::mapRow);
  }

  private Waterway mapRow(ResultSet rs, int rowNum) throws SQLException
  {
    long id = rs.getLong("id");
    List<OsmNode> nodes = getOsmNodes(rs, "geometry");
    String type = rs.getString("type");
    String name = rs.getString("name");
    String nameRu = rs.getString("name_ru");
    String nameOld = rs.getString("name_old");

    return new Waterway(id, nodes, type, name, nameRu, nameOld);
  }
}
