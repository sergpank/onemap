package md.onemap.harta.db.gis;

import md.onemap.harta.db.DbHelper;
import md.onemap.harta.db.gis.entity.Member;
import md.onemap.harta.db.gis.entity.Node;
import md.onemap.harta.db.gis.entity.Relation;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.loader.OsmLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class RelationGisDao extends GisDao<Relation>
{
  public static final String RELATION_TABLE_NAME = "gis.relation";
  public static final String RELATION_MEMBERS_TABLE_NAME = "gis.relation_members";

  private static final String INSERT = "INSERT INTO " + RELATION_TABLE_NAME + " (id, bounding_box) VALUES (?, %s)";
  private static final String INSERT_MEMBER = "INSERT INTO " + RELATION_MEMBERS_TABLE_NAME + " (relation_id, member_id, type, role) VALUES (?, ?, ?, ?)";

  private OsmLoader osmLoader;

  public RelationGisDao(OsmLoader osmLoader)
  {
    this.osmLoader = osmLoader;
  }

  @Override
  public void save(Relation entity)
  {
    BoundsLatLon bBox = calcBoundingBox(entity);
    DbHelper.getJdbcTemplate().update(String.format(INSERT, createPolygon(bBox)), entity.getId());
    for (Member m : entity.getMembers())
    {
      DbHelper.getJdbcTemplate().update(INSERT_MEMBER, entity.getId(), m.getRef(), m.getType().name(), m.getRole());
    }
  }

  protected String createPolygon(BoundsLatLon bBox)
  {
    StringBuilder geometry = new StringBuilder("ST_GeomFromText('POLYGON((");
    geometry.append(bBox.getMinLon()).append(' ').append(bBox.getMinLat()).append(',');
    geometry.append(bBox.getMinLon()).append(' ').append(bBox.getMaxLat()).append(',');
    geometry.append(bBox.getMaxLon()).append(' ').append(bBox.getMaxLat()).append(',');
    geometry.append(bBox.getMaxLon()).append(' ').append(bBox.getMinLat()).append(',');
    geometry.append(bBox.getMinLon()).append(' ').append(bBox.getMinLat()).append("))')");

    return geometry.toString();
  }

  private BoundsLatLon calcBoundingBox(Relation relation)
  {
    List<Node> nodes = getNodes(relation);
    return new BoundsLatLon(nodes);
  }

  private List<Node> getNodes(Relation relation)
  {
    List<Node> result = new ArrayList<>();
    Set<Member> members = relation.getMembers();
    for (Member m : members)
    {
      switch (m.getType())
      {
        case NODE: result.add(osmLoader.getNodes().get(m.getRef())); break;
        case WAY: result.addAll(osmLoader.getWays().get(m.getRef()).getNodes()); break;
        case RELATION: result.addAll(getNodes(osmLoader.getRelations().get(m.getRef()))); break;
      }
    }
    return result;
  }

  @Override
  public void saveAll(Collection<Relation> entities)
  {
    entities.forEach(this::save);
  }

  @Override
  public Relation load(long id)
  {
    return null;
  }

  @Override
  public Collection<Relation> load(int zoomLevel, BoundsLatLon box)
  {
    return null;
  }

  @Override
  public Collection<Relation> loadAll()
  {
    return null;
  }
}
