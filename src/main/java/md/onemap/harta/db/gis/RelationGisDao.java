package md.onemap.harta.db.gis;

import md.onemap.harta.db.DbHelper;
import md.onemap.harta.db.gis.entity.Member;
import md.onemap.harta.db.gis.entity.Node;
import md.onemap.harta.db.gis.entity.Relation;
import md.onemap.harta.db.gis.entity.Tag;
import md.onemap.harta.db.gis.entity.Way;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.loader.OsmLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RelationGisDao extends GisDao<Relation> {
  private static final Logger log = LogManager.getLogger();

  public static final String RELATION_TABLE_NAME = "gis.relation";
  public static final String RELATION_MEMBERS_TABLE_NAME = "gis.relation_members";

  private static final String INSERT_RELATION = "INSERT INTO " + RELATION_TABLE_NAME + " (id, bounding_box, type, landuse, nature) VALUES (?, %s, ?, ?, ?)";
  private static final String INSERT_MEMBER = "INSERT INTO " + RELATION_MEMBERS_TABLE_NAME + " (relation_id, member_id, type, role) VALUES (?, ?, ?, ?)";

  private static final String LOAD = "SELECT r.id, t.key, t.value, m.member_id, m.type, m.role " +
      "FROM " + RELATION_TABLE_NAME + " r " +
      "JOIN " + TagGisDao.TAG_TABLE_NAME + " t on t.id = r.id " +
      "JOIN " + RELATION_MEMBERS_TABLE_NAME + " m on m.relation_id = r.id " +
      "WHERE r.id = ";

  private static final String LOAD_TILE = "SELECT r.id, t.key, t.value, m.member_id, m.type, m.role " +
      "FROM " + RELATION_TABLE_NAME + " r " +
      "JOIN " + TagGisDao.TAG_TABLE_NAME + " t ON t.id = r.id " +
      "JOIN " + RELATION_MEMBERS_TABLE_NAME + " m on m.relation_id = r.id " +
      // THIS IS DONE FOR QUERY PERFORMANCE OPTIMISATION !!!
      "WHERE landuse IN ('cemetery', 'recreation_ground', 'forest') " +
      "OR nature IN ('water', 'wood', 'spring', 'wetland') " +
      // OTHERWISE WE WILL SELECT ALL BORDERS AND STREETS AND SO ON ... FOR EACH TILE !!!
      "AND ST_Intersects( " +
      "ST_GeomFromText('Polygon(( " +
      "%f %f, " +
      "%f %f, " +
      "%f %f, " +
      "%f %f, " +
      "%f %f " +
      "))'), bounding_box)";

  private OsmLoader osmLoader;

  public RelationGisDao(OsmLoader osmLoader) {
    this.osmLoader = osmLoader;
  }

  @Override
  public void save(Relation entity) {
    BoundsLatLon bBox = calcBoundingBox(entity);
    String type = entity.getTags().get("type");
    String landuse = entity.getTags().get("landuse");
    String natural = entity.getTags().get("natural");
    DbHelper.getJdbcTemplate().update(String.format(INSERT_RELATION, createPolygon(bBox)), entity.getId(), type, landuse, natural);

    new TagGisDao().save(new Tag(entity.getId(), entity.getTags()));

    for (Member m : entity.getMembers()) {
      DbHelper.getJdbcTemplate().update(INSERT_MEMBER, entity.getId(), m.getRef(), m.getType().name(), m.getRole());
    }
  }

  protected String createPolygon(BoundsLatLon bBox) {
    StringBuilder geometry = new StringBuilder("ST_GeomFromText('POLYGON((");
    geometry.append(bBox.getMinLon()).append(' ').append(bBox.getMinLat()).append(',');
    geometry.append(bBox.getMinLon()).append(' ').append(bBox.getMaxLat()).append(',');
    geometry.append(bBox.getMaxLon()).append(' ').append(bBox.getMaxLat()).append(',');
    geometry.append(bBox.getMaxLon()).append(' ').append(bBox.getMinLat()).append(',');
    geometry.append(bBox.getMinLon()).append(' ').append(bBox.getMinLat()).append("))')");

    return geometry.toString();
  }

  private BoundsLatLon calcBoundingBox(Relation relation) {
    List<Node> nodes = getNodes(relation);
    return new BoundsLatLon(nodes);
  }

  private List<Node> getNodes(Relation relation) {
    List<Node> result = new ArrayList<>();
    Set<Member> members = relation.getMembers();
    for (Member m : members) {
      switch (m.getType()) {
        case NODE: {
          Node node = osmLoader.getNodes().get(m.getRef());
          if (node != null) {
            result.add(node);
          }
          break;
        }
        case WAY: {
          Way way = osmLoader.getWays().get(m.getRef());
          if (way != null) {
            result.addAll(way.getNodes().stream().filter(Objects::nonNull).collect(Collectors.toList()));
          }
          break;
        }
        case RELATION: {
          Relation subrelation = osmLoader.getRelations().get(m.getRef());
          if (subrelation != null) {
            result.addAll(getNodes(subrelation).stream().filter(Objects::nonNull).collect(Collectors.toList()));
          }
          break;
        }
      }
    }
    return result;
  }

  @Override
  public void saveAll(Collection<Relation> entities) {
    entities.forEach(this::save);
  }

  @Override
  public Relation load(long id) {
    return DbHelper.getJdbcTemplate().query(LOAD + id, this::extract).iterator().next();
  }

  @Override
  public Collection<Relation> load(int zoomLevel, BoundsLatLon box) {
    double dLat = box.getMaxLat() - box.getMinLat();
    double dLon = box.getMaxLon() - box.getMinLon();

    log.info("Reading tile ...");

    String sql = String.format(Locale.ENGLISH, LOAD_TILE,
        box.getMinLon() - dLon, box.getMinLat() - dLat,
        box.getMinLon() - dLon, box.getMaxLat() + dLat,
        box.getMaxLon() + dLon, box.getMaxLat() + dLat,
        box.getMaxLon() + dLon, box.getMinLat() - dLat,
        box.getMinLon() - dLon, box.getMinLat() - dLat
    );

    Collection<Relation> relations = new HashSet<>();
    Collection<Relation> query = DbHelper.getJdbcTemplate().query(sql, this::extract);
    if (query != null)
    {
      relations.addAll(query);
    }
    return relations;
  }

  @Override
  public Collection<Relation> loadAll() {
    return null;
  }

  private Collection<Relation> extract(ResultSet rs) throws SQLException {
    Map<Long, Set<Member>> membersMap = new HashMap<>();
    Map<Long, Map<String, String>> tagsMap = new HashMap<>();

    while (rs.next()) {
      long id = rs.getLong("id");

      String key = rs.getString("key");
      String value = rs.getString("value");

      Long memberId = rs.getLong("member_id");
      String memberType = rs.getString("type");
      String memberRole = rs.getString("role");

      Map<String, String> tags = tagsMap.computeIfAbsent(id, k -> new HashMap<>());
      tags.put(key, value);

      Set<Member> members = membersMap.computeIfAbsent(id, k -> new HashSet<>());
      members.add(new Member(memberType, memberId, memberRole));
    }

    List<Relation> relations = new ArrayList<>();
    for (Long id : membersMap.keySet()) {
      relations.add(new Relation(id, tagsMap.get(id), membersMap.get(id)));
    }

    return relations;
  }
}
