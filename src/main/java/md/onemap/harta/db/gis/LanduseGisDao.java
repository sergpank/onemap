package md.onemap.harta.db.gis;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.Landuse;
import md.onemap.harta.osm.OsmNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class LanduseGisDao extends GisDao<Landuse>
{
  private static final Logger LOG = LoggerFactory.getLogger(LanduseGisDao.class);

  public static final String TABLE_NAME = "gis.landuse";

  @Override
  public void save(Landuse entity)
  {
    if (entity.getNodes().size() < 3)
    {
      LOG.error("Unable to save landuse {} with {} point(s) : {}-{}", entity.getId(), entity.getNodes().size(), entity.getName(), entity.getType());
    }
    else
    {
      saveEntity(TABLE_NAME, entity, true);
    }
  }

  @Override
  public void saveAll(Collection<Landuse> entities)
  {
    entities.forEach(this::save);
  }

  @Override
  public Landuse load(long id)
  {
    return null;
  }

  @Override
  public Collection<Landuse> load(int zoomLevel, BoundsLatLon box)
  {
    return loadTileEntities(box, TABLE_NAME, this::mapRow);
  }

  @Override
  public Collection<Landuse> loadAll()
  {
    return loadAllEntities(TABLE_NAME, this::mapRow);
  }

  private Landuse mapRow(ResultSet rs, int rowNum) throws SQLException
  {
    long id = rs.getLong("id");
    List<OsmNode> nodes = getOsmNodes(rs, "geometry");
    String type = rs.getString("type");
    String name = rs.getString("name");
    String nameRu = rs.getString("name_ru");
    String nameOld = rs.getString("name_old");

    return new Landuse(id, nodes, type, name, nameRu, nameOld);
  }
}
