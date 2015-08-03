package md.harta.db;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import md.harta.geometry.Bounds;
import md.harta.osm.OsmNode;
import md.harta.projector.AbstractProjector;

/**
 * Created by sergpank on 18.05.15.
 */
public abstract class Dao<T>
{
  protected Connection connection;

  public Dao(Connection connection)
  {
    this.connection = connection;
  }

  public abstract void save(T entity);

  public abstract void saveAll(List<T> entities);

  public abstract T load(long id, AbstractProjector projector);

  public abstract Collection<T> load(int zoomLevel, Bounds box, Map<Long, OsmNode> nodes, AbstractProjector projector);

  public abstract Collection<T> loadAll(AbstractProjector projector);

  public abstract Bounds getBounds();
}
