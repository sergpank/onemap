package md.harta.loader;

import md.harta.db.BuildingGisDao;
import md.harta.db.DbHelper;
import md.harta.db.HighwayGisDao;
import md.harta.geometry.Bounds;
import md.harta.osm.Border;
import md.harta.osm.Building;
import md.harta.osm.Highway;
import md.harta.osm.OsmNode;
import md.harta.projector.AbstractProjector;

import java.sql.Connection;
import java.util.Collection;
import java.util.Map;

/**
 * Created by serg on 11/8/15.
 */
public class PostgisLoader extends AbstractLoader
{
  private Connection connection;

  public PostgisLoader(String dbName)
  {
    this.connection = DbHelper.getConnection(dbName);;
  }

  @Override
  public Map<Long, OsmNode> getNodes()
  {
    return null;
  }

  @Override
  public void load(String dataSource, AbstractProjector projector)
  {

  }

  @Override
  public Map<Long, Highway> getHighways(AbstractProjector projector)
  {
    return null;
  }

  @Override
  public Map<Long, Building> getBuildings(AbstractProjector projector)
  {
    return null;
  }

  @Override
  public Collection<Border> getBorders(int level, Bounds tileBounds, Map<Long, OsmNode> nodes, AbstractProjector projector)
  {
    return null;
  }

  @Override
  public Collection<Highway> getHighways(int level, Bounds tileBounds, Map<Long, OsmNode> nodeMap, AbstractProjector projector)
  {
    return new HighwayGisDao(connection).load(level, tileBounds, nodeMap, projector);
  }

  @Override
  public Collection<Building> getBuildings(int level, Bounds tileBounds, Map<Long, OsmNode> nodeMap, AbstractProjector projector)
  {
    return new BuildingGisDao(connection).load(level, tileBounds, nodeMap, projector);
  }

  @Override
  public Bounds getBounds()
  {
    return null;
  }
}
