package md.harta.db.dao;

import md.harta.geometry.Bounds;
import md.harta.osm.Leisure;
import md.harta.projector.AbstractProjector;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

/**
 * Created by serg on 7/2/16.
 */
public class LeisureDao extends Dao<Leisure>
{
  public LeisureDao(Connection connection) {
    super(connection);
  }

  @Override
  public void save(Leisure entity)
  {

  }

  @Override
  public void saveAll(List<Leisure> entities)
  {

  }

  @Override
  public Leisure load(long id)
  {
    return null;
  }

  @Override
  public Collection<Leisure> load(int zoomLevel, Bounds box, AbstractProjector projector)
  {
    return null;
  }

  @Override
  public Collection<Leisure> loadAll(AbstractProjector projector)
  {
    return null;
  }

  @Override
  public Bounds getBounds()
  {
    return null;
  }
}
