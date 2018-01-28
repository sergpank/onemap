package md.onemap.harta.db.dao;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.Leisure;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

/**
 * Created by serg on 7/2/16.
 */
public class LeisureDao extends Dao<Leisure>
{
  @Override
  public void save(Leisure entity)
  {

  }

  @Override
  public void saveAll(Collection<Leisure> entities)
  {

  }

  @Override
  public Leisure load(long id)
  {
    return null;
  }

  @Override
  public Collection<Leisure> load(int zoomLevel, BoundsLatLon box)
  {
    return null;
  }

  @Override
  public Collection<Leisure> loadAll()
  {
    return null;
  }

  @Override
  public BoundsLatLon getBounds()
  {
    return null;
  }
}
