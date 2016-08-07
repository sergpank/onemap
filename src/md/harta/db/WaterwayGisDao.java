package md.harta.db;

import md.harta.geometry.Bounds;
import md.harta.osm.OsmNode;
import md.harta.osm.Waterway;
import md.harta.projector.AbstractProjector;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by serg on 07-Aug-16.
 */
public class WaterwayGisDao extends GisDao<Waterway> {
  public WaterwayGisDao(Connection connection) {
    super(connection);
  }

  @Override
  public void save(Waterway entity) {

  }

  @Override
  public void saveAll(List<Waterway> entities) {

  }

  @Override
  public Waterway load(long id, AbstractProjector projector) {
    return null;
  }

  @Override
  public Collection<Waterway> load(int zoomLevel, Bounds box, Map<Long, OsmNode> nodes, AbstractProjector projector) {
    return null;
  }

  @Override
  public Collection<Waterway> loadAll(AbstractProjector projector) {
    return null;
  }

  @Override
  public Bounds getBounds() {
    return null;
  }
}
