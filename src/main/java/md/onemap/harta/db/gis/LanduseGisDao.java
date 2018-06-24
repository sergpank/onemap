package md.onemap.harta.db.gis;

import md.onemap.harta.db.DbHelper;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.Landuse;
import md.onemap.harta.osm.OsmNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Collection;

public class LanduseGisDao extends GisDao<Landuse>
{
  private static final Logger LOG = LoggerFactory.getLogger(LanduseGisDao.class);

  private static final String INSERT = "INSERT INTO gis.landuse(id, type, name, name_ru, geometry) " +
      "VALUES(?, ?, ?, ?, %s)";

  private JdbcTemplate jdbcTemplate;

  public LanduseGisDao()
  {
    this.jdbcTemplate = DbHelper.getJdbcTemplate();
  }

  @Override
  public void save(Landuse entity)
  {
    if (entity.getNodes().size() < 3)
    {
      LOG.error("Unable to save landuse {} with {} point(s) : {}-{}", entity.getId(), entity.getNodes().size(), entity.getName(), entity.getType());
    }
    else
    {
      jdbcTemplate.update(String.format(INSERT, createLineString(entity.getNodes())), entity.getId(), entity.getType(), entity.getName(), entity.getNameRu());
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
    double dLat = box.getMaxLat() - box.getMinLat();
    double dLon = box.getMaxLon() - box.getMinLon();
    String sql = String.format(SELECT_TILE, "gis.landuse",
        box.getMinLon() - dLon, box.getMinLat() - dLat,
        box.getMinLon() - dLon, box.getMaxLat() + dLat,
        box.getMaxLon() + dLon, box.getMaxLat() + dLat,
        box.getMaxLon() + dLon, box.getMinLat() - dLat,
        box.getMinLon() - dLon, box.getMinLat() - dLat
    );

    return jdbcTemplate.query(sql, (rs, rowNum) -> {
      long id = rs.getLong("id");
      ArrayList<OsmNode> nodes = getOsmNodes(rs, "geometry");
      String type = rs.getString("type");
      String name = rs.getString("name");
      String nameRu = rs.getString("name_ru");
      return new Landuse(id, nodes, type, name, nameRu);
    });
  }

  @Override
  public Collection<Landuse> loadAll()
  {
    return null;
  }

  @Override
  public BoundsLatLon getBounds()
  {
    return null;
  }
}
