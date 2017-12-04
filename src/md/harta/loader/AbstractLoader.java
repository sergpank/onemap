package md.harta.loader;

import java.util.Collection;
import java.util.Map;
import md.harta.geometry.Bounds;
import md.harta.osm.*;
import md.harta.projector.AbstractProjector;

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

  public abstract void load(String dataSource, AbstractProjector projector);

  public abstract Map<Long, Highway> getHighways(AbstractProjector projector);

  public abstract Map<Long, Building> getBuildings(AbstractProjector projector);

  public abstract Map<Long, Leisure> getLeisure(AbstractProjector projector);

  public abstract Map<Long, Natural> getNature(AbstractProjector projector);

  public abstract Map<Long, Waterway> getWaterways(AbstractProjector projector);

  public abstract Map<Long, Landuse> getLanduse(AbstractProjector projector);

  public abstract Collection<Border> getBorders(int level, Bounds tileBounds, AbstractProjector projector);

  public abstract Collection<Highway> getHighways(int level, Bounds tileBounds, AbstractProjector projector);

  public abstract Collection<Building> getBuildings(int level, Bounds tileBounds, AbstractProjector projector);

  public abstract Collection<Leisure> getLeisure(int level, Bounds tileBounds, AbstractProjector projector);

  public abstract Collection<Natural> getNature(int level, Bounds tileBounds, AbstractProjector projector);

  public abstract Collection<Waterway> getWaterways(int level, Bounds tileBounds, AbstractProjector projector);

  public abstract Collection<Landuse> getLanduse(int level, Bounds tileBounds, AbstractProjector projector);

  public abstract Bounds getBounds();

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
