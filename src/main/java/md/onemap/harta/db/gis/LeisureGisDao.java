package md.onemap.harta.db.gis;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.Leisure;
import md.onemap.harta.osm.OsmNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class LeisureGisDao extends GisDao<Leisure>
{
  private static final Logger LOG = LoggerFactory.getLogger(LeisureGisDao.class);

  public static final String TABLE_NAME = "gis.leisure";

  @Override
  public void save(Leisure entity)
  {
    if (entity.getNodes().size() < 3)
    {
      LOG.error("Unable to save leisure {} with {} point(s) : {}-{}", entity.getId(), entity.getNodes().size(), entity.getName(), entity.getType());
    }
    else
    {
      saveEntity(TABLE_NAME, entity, true);
    }
  }

  @Override
  public void saveAll(Collection<Leisure> entities)
  {
    entities.forEach(this::save);
  }

  @Override
  public Leisure load(long id)
  {
    return null;
  }

  @Override
  public Collection<Leisure> load(int zoomLevel, BoundsLatLon box)
  {
    return loadTileEntities(box, TABLE_NAME, this::mapRow);
  }

  @Override
  public Collection<Leisure> loadAll()
  {
    return loadAllEntities(TABLE_NAME, this::mapRow);
  }

  public Leisure mapRow(ResultSet rs, int rowNum) throws SQLException
  {
    long id = rs.getLong("id");
    List<OsmNode> nodes = getOsmNodes(rs, "geometry");
    String type = rs.getString("type");
    String name = rs.getString("name");
    String nameRu = rs.getString("name_ru");
    String nameOld = rs.getString("name_old");

    return new Leisure(id, nodes, type, name, nameRu, nameOld);
  }
}
