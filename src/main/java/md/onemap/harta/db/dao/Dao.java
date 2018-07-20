package md.onemap.harta.db.dao;

import md.onemap.harta.db.gis.entity.Node;
import md.onemap.harta.geometry.BoundsLatLon;

import generated.BoundsType;

import java.util.Collection;
import java.util.List;

/**
 * Created by sergpank on 18.05.15.
 */
public abstract class Dao<T>
{
  public BoundsType getBounds(List<Node> nodes)
  {
    double minLat = 90, maxLat = -90, minLon = 180, maxLon = -180;

    for (Node node : nodes)
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

  public abstract void save(T entity);

  public abstract void saveAll(Collection<T> entities);

  public abstract T load(long id);

  public abstract Collection<T> load(int zoomLevel, BoundsLatLon box);

  public abstract Collection<T> loadAll();
}
