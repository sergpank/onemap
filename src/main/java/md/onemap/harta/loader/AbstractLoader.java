package md.onemap.harta.loader;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.*;

import java.util.Collection;
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

  public abstract Map<Long, OsmNode> getNodes();

  public abstract void load(String dataSource);

  public abstract Map<Long, Highway> getHighways();

  public abstract Map<Long, Building> getBuildings();

  public abstract Map<Long, Leisure> getLeisure();

  public abstract Map<Long, Natural> getNature();

  public abstract Map<Long, Waterway> getWaterways();

  public abstract Map<Long, Landuse> getLanduse();

  public abstract Collection<Border> getBorders(int level, BoundsLatLon tileBounds);

  public abstract Collection<Highway> getHighways(int level, BoundsLatLon tileBounds);

  public abstract Collection<Building> getBuildings(int level, BoundsLatLon tileBounds);

  public abstract Collection<Leisure> getLeisure(int level, BoundsLatLon tileBounds);

  public abstract Collection<Natural> getNature(int level, BoundsLatLon tileBounds);

  public abstract Collection<Waterway> getWaterways(int level, BoundsLatLon tileBounds);

  public abstract Collection<Landuse> getLanduse(int level, BoundsLatLon tileBounds);

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
