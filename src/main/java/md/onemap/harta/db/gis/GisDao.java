package md.onemap.harta.db.gis;

import md.onemap.harta.db.DbHelper;
import md.onemap.harta.db.dao.Dao;
import md.onemap.harta.osm.OsmNode;
import md.onemap.harta.osm.OsmWay;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by serg on 11/7/15.
 */
public abstract class GisDao<T> extends Dao<T>
{
  protected static final String INSERT = "INSERT INTO %s (id, type, name, name_ru, name_old, geometry) VALUES (?, ?, ?, ?, ?, %s)";
  protected static final String SELECT_TILE = "SELECT id, type, name, name_ru, name_old, geometry " +
      "FROM %s " +
      "WHERE ST_Intersects(" +
      "ST_GeomFromText('Polygon((" +
      "%f %f," +
      "%f %f," +
      "%f %f," +
      "%f %f," +
      "%f %f" +
      "))'), geometry)";
  protected JdbcTemplate jdbcTemplate = DbHelper.getJdbcTemplate();

  protected void saveEntity(String tableName, OsmWay entity)
  {
    jdbcTemplate.update(String.format(INSERT, tableName, createLineString(entity.getNodes())), entity.getId(), entity.getType(), entity.getName(), entity.getNameRu(), entity.getNameOld());
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

  protected ArrayList<OsmNode> getOsmNodes(ResultSet rs, String geometryColumn) throws SQLException
  {
    ArrayList<OsmNode> nodes = new ArrayList<>();
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
