package md.onemap.harta.db.gis;

import md.onemap.harta.db.DbHelper;
import md.onemap.harta.db.gis.entity.Tag;
import md.onemap.harta.geometry.BoundsLatLon;

import java.util.Collection;

public class TagGisDao extends GisDao<Tag>
{
  public static final String TAG_TABLE_NAME = "gis.tag";
  private static final String INSERT_TAG = "INSERT INTO " + TAG_TABLE_NAME + " (id, key, value) VALUES (?, ?, ?)";

  @Override
  public void save(Tag tag)
  {
    tag.getTags()
        .forEach((key, value)
            -> DbHelper.getJdbcTemplate().update(INSERT_TAG, tag.getParentId(), key, value));
  }

  @Override
  public void saveAll(Collection<Tag> tags)
  {
    tags.forEach(this::save);
  }

  @Override
  public Tag load(long id)
  {
    return null;
  }

  @Override
  public Collection<Tag> load(int zoomLevel, BoundsLatLon box)
  {
    return null;
  }

  @Override
  public Collection<Tag> loadAll()
  {
    return null;
  }
}
