package md.onemap.harta.db.dao;

import generated.BoundsType;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.OsmNode;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

/**
 * Created by sergpank on 18.05.15.
 */
public abstract class Dao<T>
{
  public static final String SELECT_TILE = "SELECT * FROM %s WHERE " +
      "NOT( ((max_lon < ?) OR (min_lon > ?)) " +
      " AND ((max_lat < ?) OR (min_lat > ?)) )";

  protected Connection connection;

  public Dao(Connection connection)
  {
    this.connection = connection;
  }

  public BoundsType getBounds(List<OsmNode> nodes)
  {
    double minLat = 90, maxLat = -90, minLon = 180, maxLon = -180;

    for (OsmNode node : nodes)
    {
      maxLat = node.getLat() > maxLat ? node.getLat() : maxLat;
      minLat = node.getLat() < minLat ? node.getLat() : minLat;
      maxLon = node.getLon() > maxLon ? node.getLon() : maxLon;
      minLon = node.getLon() < minLon ? node.getLon() : minLon;
    }

    BoundsType bounds = new BoundsType();
    bounds.setMinlat(minLat);
    bounds.setMaxlat(maxLat);
    bounds.setMinlon(minLon);
    bounds.setMaxlon(maxLon);

    return bounds;
  }

  public Long[] getNodeIds(List<OsmNode> nodes)
  {
    Long[] ids = new Long[nodes.size()];
    for (int i = 0; i < nodes.size(); i++)
    {
      ids[i] = nodes.get(i).getId();
    }

    return ids;
  }

  public abstract void save(T entity);

  public abstract void saveAll(Collection<T> entities);

  public abstract T load(long id);

  public abstract Collection<T> load(int zoomLevel, BoundsLatLon box);

  public abstract Collection<T> loadAll();

  public abstract BoundsLatLon getBounds();
}
