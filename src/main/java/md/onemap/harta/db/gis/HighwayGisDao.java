package md.onemap.harta.db.gis;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.Highway;
import md.onemap.harta.osm.OsmNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Created by serg on 11/7/15.
 */
public class HighwayGisDao extends GisDao<Highway>
{
  private static final Logger LOG = LoggerFactory.getLogger(HighwayGisDao.class);

  public static final String TABLE_NAME = "gis.highways";

  @Override
  public void save(Highway highway)
  {
    if (highway.getNodes().size() < 2)
    {
      LOG.error("Unable to save highway with {} point(s); name: {}; id: {}",
          highway.getNodes().size(), highway.getName(), highway.getId());
    }
    else
    {
      saveEntity(TABLE_NAME, highway, false);
    }
  }

  @Override
  public void saveAll(Collection<Highway> highways)
  {
    highways.forEach(this::save);
  }

  @Override
  public Highway load(long id)
  {
    return null;
  }

  @Override
  public Collection<Highway> load(int zoomLevel, BoundsLatLon box)
  {
    return loadTileEntities(box, TABLE_NAME, this::mapRow);
  }

  @Override
  public Collection<Highway> loadAll()
  {
    return loadAllEntities(TABLE_NAME, this::mapRow);
  }

  private Highway mapRow(ResultSet rs, int rowNum) throws SQLException
  {
    long id = rs.getLong("id");
    String name = rs.getString("name");
    String nameRu = rs.getString("name_ru");
    String oldName = rs.getString("name_old");
    String type = rs.getString("type");
    List<OsmNode> nodes = getOsmNodes(rs, "geometry");

    return new Highway(id, name, nameRu, oldName, type, nodes);
  }
}
