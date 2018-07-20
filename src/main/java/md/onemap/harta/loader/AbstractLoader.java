package md.onemap.harta.loader;

import md.onemap.harta.db.gis.entity.Node;
import md.onemap.harta.geometry.BoundsLatLon;

import java.util.Map;

/**
 * Created by sergpank on 15.05.15.
 */
public abstract class AbstractLoader
{
  protected double minLon;
  protected double minLat;
  protected double maxLon;
  protected double maxLat;

  public abstract Map<Long, Node> getNodes();

  public abstract void load(String dataSource);

  public abstract BoundsLatLon getBounds();

  public double getMinLon()
  {
    return minLon;
  }

  public double getMinLat()
  {
    return minLat;
  }

  public double getMaxLon()
  {
    return maxLon;
  }

  public double getMaxLat()
  {
    return maxLat;
  }

  protected void registerMinMax(Double lat, Double lon)
  {
    if (lat < minLat) {
      minLat = lat;
    }
    if (lat > maxLat) {
      maxLat = lat;
    }

    if (lon < minLon) {
      minLon = lon;
    }
    if (lon > maxLon) {
      maxLon = lon;
    }
  }
}
