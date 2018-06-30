package md.onemap.harta.db.gis;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.Leisure;
import md.onemap.harta.osm.OsmNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

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
      jdbcTemplate.update(String.format(INSERT, "gis.leisure", createLineString(entity.getNodes())), entity.getId(), entity.getType(), entity.getName(), entity.getNameRu(), entity.getNameOld());
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
    double dLat = box.getMaxLat() - box.getMinLat();
    double dLon = box.getMaxLon() - box.getMinLon();
    String sql = String.format(SELECT_TILE, TABLE_NAME,
        box.getMinLon() - dLon, box.getMinLat() - dLat,
        box.getMinLon() - dLon, box.getMaxLat() + dLat,
        box.getMaxLon() + dLon, box.getMaxLat() + dLat,
        box.getMaxLon() + dLon, box.getMinLat() - dLat,
        box.getMinLon() - dLon, box.getMinLat() - dLat
    );

    return jdbcTemplate.query(sql, new LeisureMapper());
  }

  @Override
  public Collection<Leisure> loadAll()
  {
    return null;
  }

  private class LeisureMapper implements RowMapper<Leisure>
  {
    @Override
    public Leisure mapRow(ResultSet rs, int rowNum) throws SQLException
    {
      long id = rs.getLong("id");
      ArrayList<OsmNode> nodes = getOsmNodes(rs, "geometry");
      String type = rs.getString("type");
      String name = rs.getString("name");
      String nameRu = rs.getString("name_ru");
      String nameOld = rs.getString("name_old");

      return new Leisure(id, nodes, type, name, nameRu, nameOld);
    }
  }
}
