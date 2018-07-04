package md.onemap.harta.db.gis;

import md.onemap.harta.db.DbHelper;
import md.onemap.harta.db.dao.Dao;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.OsmNode;
import md.onemap.harta.osm.OsmWay;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by serg on 11/7/15.
 */
public abstract class GisDao<T> extends Dao<T>
{
  protected static final String INSERT = "INSERT INTO %s (id, type, name, name_ru, name_old, geometry) VALUES (?, ?, ?, ?, ?, %s)";
  private static final String SELECT_ALL = "SELECT * FROM %s";
  private static final String SELECT_TILE = "SELECT * " +
      "FROM %s " +
      "WHERE ST_Intersects(" +
      "ST_GeomFromText('Polygon((" +
      "%f %f," +
      "%f %f," +
      "%f %f," +
      "%f %f," +
      "%f %f" +
      "))'), geometry)";

  void saveEntity(String tableName, OsmWay entity, boolean isPolygon)
  {
    String geometry = isPolygon ? createPolygon(entity.getNodes()) : createLineString(entity.getNodes());
    DbHelper.getJdbcTemplate().update(String.format(INSERT, tableName, geometry), entity.getId(), entity.getType(), entity.getName(), entity.getNameRu(), entity.getNameOld());
  }

  <E> Collection<E> loadTileEntities(BoundsLatLon box, String tableName, RowMapper<E> mapper)
  {
    double dLat = box.getMaxLat() - box.getMinLat();
    double dLon = box.getMaxLon() - box.getMinLon();
    String sql = String.format(SELECT_TILE, tableName,
        box.getMinLon() - dLon, box.getMinLat() - dLat,
        box.getMinLon() - dLon, box.getMaxLat() + dLat,
        box.getMaxLon() + dLon, box.getMaxLat() + dLat,
        box.getMaxLon() + dLon, box.getMinLat() - dLat,
        box.getMinLon() - dLon, box.getMinLat() - dLat
    );

    return DbHelper.getJdbcTemplate().query(sql, mapper);
  }

  <E> Collection<E> loadAllEntities(String tableName, RowMapper<E> mapper)
  {
        return DbHelper.getJdbcTemplate().query(String.format(SELECT_ALL, tableName), mapper);
  }

  protected void toGisConnection(Connection connection)
  {
    try
    {
      /*
       * Add the geometry types to the connection. Note that you
       * must cast the connection to the pgsql-specific connection
       * implementation before calling the addDataType() method.
       */
      Class<? extends PGobject> geometryClass = (Class<? extends PGobject>) Class.forName("org.postgis.PGgeometry");
      ((org.postgresql.PGConnection)connection).addDataType("geometry", geometryClass);
    }
    catch (SQLException | ClassNotFoundException e)
    {
      e.printStackTrace();
    }
  }

  List<OsmNode> getOsmNodes(ResultSet rs, String geometryColumn) throws SQLException
  {
    List<OsmNode> nodes = new ArrayList<>();
    PGgeometry geometry = (PGgeometry) rs.getObject(geometryColumn);
    for (int i = 0; i < geometry.getGeometry().numPoints(); i++)
    {
      Point point = geometry.getGeometry().getPoint(i);
      nodes.add(new OsmNode(i, point.getY(), point.getX()));
    }
    return nodes;
  }

  protected String createPolygon(List<OsmNode> nodes)
  {
    StringBuilder geometry = new StringBuilder("ST_GeomFromText('POLYGON((");
    for (OsmNode node : nodes)
    {
      geometry.append(node.getLon()).append(' ').append(node.getLat()).append(',');
    }
    geometry.append(nodes.get(0).getLon()).append(' ').append(nodes.get(0).getLat()).append("))')");

    return geometry.toString();
  }

  protected String createLineString(List<OsmNode> nodes)
  {
    StringBuilder geometry = new StringBuilder("ST_GeomFromText('LINESTRING(");
    for (OsmNode node : nodes)
    {
      geometry.append(node.getLon()).append(' ').append(node.getLat()).append(',');
    }
    geometry.delete(geometry.length() - 1, geometry.length());
    geometry.append(")')");

    return geometry.toString();
  }
}
