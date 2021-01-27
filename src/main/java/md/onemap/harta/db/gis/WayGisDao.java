package md.onemap.harta.db.gis;

import md.onemap.harta.db.DbHelper;
import md.onemap.harta.db.gis.entity.Node;
import md.onemap.harta.db.gis.entity.Tag;
import md.onemap.harta.db.gis.entity.Unit;
import md.onemap.harta.db.gis.entity.Way;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.Building;
import md.onemap.harta.osm.Highway;
import md.onemap.harta.osm.Waterway;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.postgis.Point;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class WayGisDao extends GisDao<Way>
{
  private static final Logger LOG = LogManager.getLogger();

  public static final int MAX_BUILDING_LEVEL = 15;

  public static final String WAY_TABLE_NAME = "gis.way";

  private static final String INSERT_WAY = "INSERT INTO " + WAY_TABLE_NAME + " (id, type, geometry) VALUES (?, ?, %s)";
  private static final String SELECT_WAY = "SELECT w.id, w.type, w.geometry, t.key, t.value, ST_Envelope(w.geometry) " +
      "FROM gis.way w " +
      "LEFT JOIN gis.tag t ON w.id = t.id " +
      "WHERE w.id = ";
  private static final String SELECT_TILE = "SELECT w.id, w.type, w.geometry, t.key, t.value, ST_Envelope(w.geometry) " +
      "FROM " + WAY_TABLE_NAME + " w " +
      "JOIN " + TagGisDao.TAG_TABLE_NAME + " t ON w.id = t.id " +
      "WHERE ST_Intersects( " +
      "ST_GeomFromText('Polygon(( " +
      "%f %f, " +
      "%f %f, " +
      "%f %f, " +
      "%f %f, " +
      "%f %f " +
      "))'), geometry)";
  private static final String SELECT_ALL_HIGHWAYS = "SELECT w.id, w.type, w.geometry, t.key, t.value, ST_Envelope(w.geometry) " +
      "FROM " + WAY_TABLE_NAME + " w " +
      "JOIN " + TagGisDao.TAG_TABLE_NAME + " t ON w.id = t.id " +
      "WHERE w.type = '" + Highway.HIGHWAY +"'";


  @Override
  public void save(Way way)
  {
    Map<String, String> tags = way.getTags();
    Object geometry = createGeometry(way);

    if (geometry == null)
    {
      // That means that WAY contains insufficient points and can't be converted to GEOMETRY
      return;
    }

    String sql = String.format(INSERT_WAY, geometry);
    DbHelper.getJdbcTemplate().update(sql, way.getId(), Unit.defineType(tags));
    new TagGisDao().save(new Tag(way.getId(), tags));
  }

  @Override
  public void saveAll(Collection<Way> entities)
  {
    entities.forEach(this::save);
  }

  @Override
  public Way load(long id)
  {
    Collection<Way> ways = DbHelper.getJdbcTemplate().query(SELECT_WAY + id, this::extractData);
    if (ways == null || ways.isEmpty())
    {
//      LOG.error("Way not found, id: {}", id);
      return null;
    }
    else
    {
      return ways.iterator().next();
    }
  }

  @Override
  public Collection<Way> load(int zoomLevel, BoundsLatLon box)
  {
    double dLat = box.getMaxLat() - box.getMinLat();
    double dLon = box.getMaxLon() - box.getMinLon();
    String sql = String.format(Locale.ENGLISH, SELECT_TILE,
        box.getMinLon() - dLon, box.getMinLat() - dLat,
        box.getMinLon() - dLon, box.getMaxLat() + dLat,
        box.getMaxLon() + dLon, box.getMaxLat() + dLat,
        box.getMaxLon() + dLon, box.getMinLat() - dLat,
        box.getMinLon() - dLon, box.getMinLat() - dLat
    );

    if (zoomLevel < MAX_BUILDING_LEVEL)
    {
      // Exclude buildings from less detailed levels
      sql += " AND type <> '" + Building.BUILDING + "'";
    }

    Collection<Way> ways = new HashSet<>();
    Collection<Way> query = DbHelper.getJdbcTemplate().query(sql, this::extractData);
    if (query != null)
    {
      ways.addAll(query);
    }
    return ways;
  }

  @Override
  public Collection<Way> loadAll()
  {
    return null;
  }

  public Collection<Way> loadAllHighways()
  {
    Collection<Way> highways = DbHelper.getJdbcTemplate().query(SELECT_ALL_HIGHWAYS, this::extractData);
    return highways;
  }

  private Object createGeometry(Way way)
  {
    List<Node> nodes = way.getNodes().stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    if (way.getTags().containsKey(Highway.HIGHWAY)
        || way.getTags().containsKey(Waterway.WATERWAY)
        || way.getTags().containsKey("barrier"))
    {
      if (nodes.size() < 2)
      {
        LOG.error("Way {} has only {} points and can't be converted to LineString. Tags: {}", way.getId(), nodes.size(), way.getTags());
        return null;
      }
      if (nodes.size() == 2 && nodes.get(0).equals(nodes.get(1)))
      {
        LOG.error("Way {} has 2 points and they are same. Probably it stays on border.", way.getId());
        return null;
      }
      return createLineString(nodes);
    }
    else
    {
      if (nodes.size() < 3)
      {
        LOG.error("Way {} has only {} points and can't be converted to Polygon. Tags: {}", way.getId(), nodes.size(), way.getTags());
        return null;
      }
      return createPolygon(nodes);
    }
  }

  private Collection<Way> extractData(ResultSet rs) throws SQLException
  {
    Map<Long, Way> resultMap = new HashMap<>();

    while (rs.next())
    {
      Long id = rs.getLong("id");
      String key = rs.getString("key");
      String value = rs.getString("value");

      Way way = resultMap.get(id);
      if (way == null)
      {
        String type = rs.getString("type");
        List<Node> nodes = getNodes(rs, "geometry");
        Geometry envelope = ((PGgeometry) rs.getObject("st_envelope")).getGeometry();
        BoundsLatLon boundsLatLon = getBounds(envelope);

        way = new Way(id, type, nodes, new HashMap<>(), boundsLatLon);
        resultMap.put(id, way);
      }
      way.getTags().put(key, value);
    }

    return resultMap.values();
  }

  private BoundsLatLon getBounds(Geometry envelope)
  {
    Point minLonLat;
    Point maxLonLat;
    if (envelope.numPoints() == 2){
      // This is a LineString
      minLonLat = envelope.getPoint(0);
      maxLonLat = envelope.getPoint(1);
    }
    else
    {
      // This is a Polygon
      minLonLat = envelope.getPoint(0);
      maxLonLat = envelope.getPoint(2);
    }
    return new BoundsLatLon(minLonLat.y, minLonLat.x, maxLonLat.y, maxLonLat.x);
  }

  public static void main(String[] args)
  {
    Way way = new WayGisDao().load(206834772);

    System.out.println(way);
    System.out.println(way.getBoundsLatLon());
    way.getNodes().forEach(System.out::println);

    System.out.println();


    System.out.println("\n\nLoading tile:\n\n");
    BoundsLatLon bounds = new BoundsLatLon(47.022, 28.835, 47.023, 28.845);
    System.out.println("My bounds: " + bounds);

    Collection<Way> load = new WayGisDao().load(18, bounds);
    load.forEach(System.out::println);
  }
}
