package md.onemap.harta.db.gis;

import md.onemap.harta.db.DbHelper;
import md.onemap.harta.db.gis.entity.Node;
import md.onemap.harta.db.gis.entity.Tag;
import md.onemap.harta.geometry.BoundsLatLon;

import java.util.Collection;

public class NodeGisDao extends GisDao<Node>
{
  public static final String NODE_TABLE_NAME = "gis.node";

  private static final String INSERT = "INSERT INTO gis.node VALUES (?, ST_POINT(%f, %f))";

  @Override
  public void save(Node node)
  {
    DbHelper.getJdbcTemplate().update(String.format(INSERT, node.getLon(), node.getLat()), node.getId());
    new TagGisDao().save(new Tag(node.getId(), node.getTags()));
  }

  @Override
  public void saveAll(Collection<Node> nodes)
  {
    nodes.forEach(this::save);
  }

  @Override
  public Node load(long id)
  {
    return null;
  }

  @Override
  public Collection<Node> load(int zoomLevel, BoundsLatLon box)
  {
    return null;
  }

  @Override
  public Collection<Node> loadAll()
  {
    return null;
  }
}
