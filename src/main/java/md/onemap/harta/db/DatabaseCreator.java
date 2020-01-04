package md.onemap.harta.db;

import md.onemap.harta.db.gis.NodeGisDao;
import md.onemap.harta.db.gis.RelationGisDao;
import md.onemap.harta.db.gis.TagGisDao;
import md.onemap.harta.db.gis.WayGisDao;
import md.onemap.harta.db.statistics.TileStatistics;
import md.onemap.harta.db.statistics.VisitorStatistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by sergpank on 30.06.15.
 */
public class DatabaseCreator
{
  private static Logger log = LoggerFactory.getLogger(DatabaseCreator.class);

  public static final String DROP_TABLE = "DROP TABLE %s";

  public static void createGisDb(String dbName)
  {
    try (Connection con = createDbIfNotExists(dbName))
    {
      createTable(getCreateNormalizedHighwaysTable(), "normalized_highways");

      con.createStatement().execute("CREATE EXTENSION IF NOT EXISTS Postgis;");
      con.createStatement().execute("CREATE SCHEMA IF NOT EXISTS gis;");

      con.createStatement().execute("CREATE SCHEMA IF NOT EXISTS statistics;");
      createTable(getCreateTileCountTable(), "statistics.tile_calls_count");
      createTable(getCreateVisitorsTable(), "statistics.visitors");

      createTable(getCreateNodeGisTable(), NodeGisDao.NODE_TABLE_NAME);
      createTable(getCreateWayGisTable(), WayGisDao.WAY_TABLE_NAME);
      createTable(getCreateRelationTable(), RelationGisDao.RELATION_TABLE_NAME);
      createTable(getCreateRelationMembersTable(), RelationGisDao.RELATION_MEMBERS_TABLE_NAME);
      createTable(getCreateTagTable(), TagGisDao.TAG_TABLE_NAME);

      con.createStatement().execute("CREATE INDEX ON " + TagGisDao.TAG_TABLE_NAME + " (id)");
      con.createStatement().execute("CREATE INDEX ON " + WayGisDao.WAY_TABLE_NAME + " USING gist (geometry)");
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  private static void createTable(String query, String tableName)
  {
    try (Statement statement = DbHelper.getConnection().createStatement())
    {
      statement.execute(query);
      log.info("Table \"{}\" is created.", tableName);
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  private static void reCreateTable(Connection con, String query, String tableName)
  {
    if (!isTableExists(con, tableName))
    {
      createTable(query, tableName);
    }
    else
    {
      try (Statement st = con.createStatement())
      {
        log.info("\"{}\" table already exists.", tableName);
        st.execute(String.format(DROP_TABLE, tableName));
        st.execute(query);
        log.info("Table \"{}\" is REcreated.\n", tableName);
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
    }
  }

  private static boolean isTableExists(Connection con, String tableName)
  {
    boolean result = false;
    try (Statement stmt = con.createStatement())
    {
      ResultSet rs = stmt.executeQuery("SELECT 1 FROM pg_catalog.pg_tables WHERE schemaname = 'public' " +
          "AND tablename  = '" + tableName + "'");
      result = rs.next();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return result;
  }

  private static Connection createDbIfNotExists(String dbName)
  {
    dbName = dbName.toLowerCase();
    try (Connection connection = DbHelper.getConnection(""))
    {
      Statement statement = connection.createStatement();
      ResultSet rs = statement.executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'");
      if (!rs.next())
      {
        log.info("Database '{}' does not exist.\n", dbName);
        try (Statement stmt = connection.createStatement())
        {
          stmt.execute("CREATE DATABASE " + dbName);
          log.info("Database '{}' is created.\n", dbName);
        }
      }
      else
      {
        log.info("Database '{}' already exists", dbName);
        log.info("Drop database ...");
        statement.execute("DROP DATABASE " + dbName);
        log.info("Recreating database '{}' ...", dbName);
        statement.execute("CREATE DATABASE " + dbName);
        log.info("Database '{}' is created.\n", dbName);
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return DbHelper.getConnection();
  }

  private static String getCreateNormalizedHighwaysTable()
  {
    return "CREATE TABLE IF NOT EXISTS normalized_highways " +
        "( " +
        " id bigint PRIMARY KEY, " +
        " name text, " +
        " name_ru text, " +
        " name_old text " +
        ")";
  }

  private static String getCreateNodeGisTable()
  {
    return "CREATE TABLE " + NodeGisDao.NODE_TABLE_NAME + " ( " +
        "id bigint PRIMARY KEY, " +
        "point geometry" +
        ")";
  }

  private static String getCreateWayGisTable()
  {
    return "create table " + WayGisDao.WAY_TABLE_NAME + " ( " +
        "id bigint PRIMARY KEY, " +
        "type text, " +
        "geometry geometry " +
        ")";
  }

  private static String getCreateTagTable()
  {
    return "create table " + TagGisDao.TAG_TABLE_NAME + " ( " +
        "id bigint, " +
        "key text, " +
        "value text " +
        ")";
  }

  private static String getCreateRelationTable()
  {
    return "create table " + RelationGisDao.RELATION_TABLE_NAME + " ( " +
        "id bigint PRIMARY KEY, " +
        "bounding_box geometry, " +
        "landuse text, " +
        "nature text, " + // actually this is 'natural' tag
        "type text" +
        ")";
  }

  private static String getCreateRelationMembersTable()
  {
    return "CREATE TABLE " + RelationGisDao.RELATION_MEMBERS_TABLE_NAME + " ( " +
        "relation_id bigint references gis.relation(id), " +
        "member_id bigint, " +
        "type text, " +
        "role text " +
        ");";
  }

  private static String getCreateTileCountTable()
  {
    return "CREATE TABLE " + TileStatistics.TABLE_NAME + " ( " +
        "level smallint, " +
        "x int, " +
        "y int, " +
        "count bigint" +
        ");";
  }

  private static String getCreateVisitorsTable()
  {
    return "CREATE TABLE " + VisitorStatistics.TABLE_NAME + " ( " +
        "ip text, " +
        "last_visit_date timestamp, " +
        "tile_cnt bigint" +
        ");";
  }
}
