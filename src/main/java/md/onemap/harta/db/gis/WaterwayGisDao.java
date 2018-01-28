package md.onemap.harta.db.gis;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.Waterway;

import java.util.Collection;

/**
 * Created by serg on 07-Aug-16.
 */
public class WaterwayGisDao extends GisDao<Waterway> {

  @Override
  public void save(Waterway entity) {

  }

  @Override
  public void saveAll(Collection<Waterway> entities) {

  }

  @Override
  public Waterway load(long id) {
    return null;
  }

  @Override
  public Collection<Waterway> load(int zoomLevel, BoundsLatLon box) {
    return null;
  }

  @Override
  public Collection<Waterway> loadAll() {
    return null;
  }

  @Override
  public BoundsLatLon getBounds() {
    return null;
  }
}
