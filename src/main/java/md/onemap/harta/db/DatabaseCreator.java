package md.onemap.harta.db;

import md.onemap.harta.db.gis.*;
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

  public static void createPlainDb(String dbName)
  {
    try (Connection con = createDbIfNotExists(dbName))
    {
      createTable(getCreateNodesTable(), "nodes");
      createTable(getCreateHighwaysTable(), "highways");
      createTable(getCreateBuildingsTable(), "buildings");
      createTable(getCreateBordersTable(), "borders");
      createTable(getCreateLeisureTable(), "leisure");
      createTable(getCreateWaterwayTable(), "waterway");
      createTable(getCreateLanduseTable(), "landuse");

      createTable(getCreateNormalizedHighwaysTable(), "normalized_highways");

      con.createStatement().execute("CREATE SCHEMA IF NOT EXISTS statistics;");
      createTable(getCreateTileCountTable(), "statistics.tile_calls_count");
      createTable(getCreateVisitorsTable(), "statistics.visitors");
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public static void createGisDb(String dbName)
  {
    try (Connection con = createDbIfNotExists(dbName))
    {
      createTable(getCreateNormalizedHighwaysTable(), "normalized_highways");

      con.createStatement().execute("CREATE EXTENSION IF NOT EXISTS Postgis;");
      con.createStatement().execute("CREATE SCHEMA IF NOT EXISTS gis;");

//      createTable(getCreateHighwaysGisTable(), HighwayGisDao.TABLE_NAME);
//      createTable(getCreateBuildingsGisTable(), BuildingGisDao.TABLE_NAME);
//      createTable(getCreateBordersGisTable(), "gis.borders");
//      createTable(getCreateLeisureGisTable(), LeisureGisDao.TABLE_NAME);
//      createTable(getCreateWaterwayGisTable(), WaterwayGisDao.TABLE_NAME);
//      createTable(getCreateLanduseGisTable(), LanduseGisDao.TABLE_NAME);
//      createTable(getCreateNaturalGisTable(), NatureGisDao.TABLE_NAME);

      con.createStatement().execute("CREATE SCHEMA IF NOT EXISTS statistics;");
      createTable(getCreateTileCountTable(), "statistics.tile_calls_count");
      createTable(getCreateVisitorsTable(), "statistics.visitors");

      createTable(getCreateWayGisTable(), WayGisDao.WAY_TABLE_NAME);
      createTable(getCreateTagTable(), WayGisDao.TAG_TABLE_NAME);
      con.createStatement().execute("CREATE INDEX ON " + WayGisDao.TAG_TABLE_NAME + " (id)");
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

  private static String getCreateNodesTable()
  {
    return "CREATE TABLE IF NOT EXISTS nodes (" +
        "node_id bigint NOT NULL, " +
        "lat double precision, " +
        "lon double precision, " +
        "CONSTRAINT nodes_pkey PRIMARY KEY (node_id) )";
  }

  private static String getCreateBordersTable()
  {
    return "CREATE TABLE IF NOT EXISTS borders (" +
        "  border_id bigint NOT NULL," +
        "  name text," +
        "  min_level integer," +
        "  max_level integer," +
        "  border_nodes bigint[]," +
        "  min_lat double precision," +
        "  max_lat double precision," +
        "  min_lon double precision," +
        "  max_lon double precision," +
        "  CONSTRAINT borders_pkey PRIMARY KEY (border_id) )";
  }

  private static String getCreateBuildingsTable()
  {
    return "CREATE TABLE IF NOT EXISTS buildings ( " +
        "building_id bigint NOT NULL, " +
        "housenumber text, " +
        "street text, " +
        "height text, " +
        "design text, " +
        "levels integer, " +
        "building_nodes bigint[], " +
        "min_lat double precision, " +
        "max_lat double precision, " +
        "min_lon double precision, " +
        "max_lon double precision," +
        "CONSTRAINT buildings_pkey PRIMARY KEY (building_id) )";
  }

  private static String getCreateHighwaysTable()
  {
    return "CREATE TABLE IF NOT EXISTS highways ( " +
        "highway_id bigint, " +
        "highway_name text, " +
        "highway_name_ru text, " +
        "highway_name_old text, " +
        "highway_type text, " +
        "highway_nodes bigint[], " +
        "min_lat double precision, " +
        "max_lat double precision, " +
        "min_lon double precision, " +
        "max_lon double precision," +
        "CONSTRAINT highways_pkey PRIMARY KEY (highway_id) )";
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

  private static String getCreateLeisureTable()
  {
    return "CREATE TABLE IF NOT EXISTS leisure (" +
        "leisure_id bigint NOT NULL, " +
        "leisure_type text, " +
        "leisure_name text, " +
        "leisure_nodes bigint[], " +
        "min_lat double precision, " +
        "max_lat double precision, " +
        "min_lon double precision, " +
        "max_lon double precision," +
        "CONSTRAINT leisure_pkey PRIMARY KEY (leisure_id) )";
  }

  private static String getCreateWaterwayTable()
  {
    return "CREATE TABLE IF NOT EXISTS waterways (" +
        "waterway_id bigint NOT NULL, " +
        "waterway_type text, " +
        "waterway_name text, " +
        "waterway_name_ru text, " +
        "waterway_nodes bigint[], " +
        "min_lat double precision, " +
        "max_lat double precision, " +
        "min_lon double precision, " +
        "max_lon double precision," +
        "CONSTRAINT waterway_pkey PRIMARY KEY (waterway_id) )";
  }

  private static String getCreateLanduseTable()
  {
    return "CREATE TABLE IF NOT EXISTS landuse (" +
        "landuse_id bigint NOT NULL, " +
        "landuse_type text, " +
        "landuse_nodes bigint[], " +
        "min_lat double precision, " +
        "max_lat double precision, " +
        "min_lon double precision, " +
        "max_lon double precision," +
        "CONSTRAINT landuse_pkey PRIMARY KEY (landuse_id) )";
  }

  private static String getCreateBordersGisTable()
  {
    return "CREATE TABLE IF NOT EXISTS gis.borders (" +
        "  id bigint NOT NULL," +
        "  name text," +
        "  geometry geometry," +
        "  CONSTRAINT borders_gis_pkey PRIMARY KEY (id) )";
  }

  private static String getCreateBuildingsGisTable()
  {
    return "CREATE TABLE IF NOT EXISTS " + BuildingGisDao.TABLE_NAME +
        " ( " +
        "  id bigint NOT NULL, " +
        "  housenumber text, " +
        "  street text, " +
        "  height text, " +
        "  design text, " +
        "  levels integer, " +
        "  geometry geometry, " +
        "  CONSTRAINT buildings_gis_pkey PRIMARY KEY (id)" +
        ")";
  }

  private static String getCreateHighwaysGisTable()
  {
    return "CREATE TABLE IF NOT EXISTS " + HighwayGisDao.TABLE_NAME +
        "( " +
        "  id bigint NOT NULL, " +
        "  name text, " +
        "  name_ru text, " +
        "  name_old text, " +
        "  type text, " +
        "  geometry geometry, " +
        "  CONSTRAINT highways_gis_pkey PRIMARY KEY (id) " +
        ")";
  }

  private static String getCreateLeisureGisTable()
  {
    return "CREATE TABLE IF NOT EXISTS " + LeisureGisDao.TABLE_NAME + " (" +
        "id bigint NOT NULL, " +
        "type text, " +
        "name text, " +
        "name_ru text, " +
        "name_old text, " +
        "geometry geometry, " +
        "CONSTRAINT leisure_gis_pkey PRIMARY KEY (id) )";
  }

  private static String getCreateLanduseGisTable()
  {
    return "CREATE TABLE IF NOT EXISTS " + LanduseGisDao.TABLE_NAME + " (" +
        "id bigint NOT NULL, " +
        "type text, " +
        "name text, " +
        "name_ru text, " +
        "name_old text, " +
        "geometry geometry, " +
        "CONSTRAINT landuse_gis_pkey PRIMARY KEY (id) )";
  }

  private static String getCreateWaterwayGisTable()
  {
    return "CREATE TABLE IF NOT EXISTS " + WaterwayGisDao.TABLE_NAME + "(" +
        "id bigint NOT NULL, " +
        "type text, " +
        "name text, " +
        "name_ru text, " +
        "name_old text, " +
        "geometry geometry, " +
        "CONSTRAINT waterway_gis_pkey PRIMARY KEY (id) )";
  }

  private static String getCreateNaturalGisTable()
  {
    return "CREATE TABLE "+ NatureGisDao.TABLE_NAME +" (" +
        "id bigint, " +
        "type text, " +
        "name text, " +
        "name_ru text, " +
        "name_old text, " +
        "name_local text, " +
        "geometry geometry, " +
        "CONSTRAINT natural_gis_pkey PRIMARY KEY (id)" +
        ")";
  }

  private static String getCreateWayGisTable()
  {
    return "create table " + WayGisDao.WAY_TABLE_NAME + " ( " +
        "id bigint, " +
        "type text, " +
        "geometry geometry, " +
        "CONSTRAINT way_gis_pkey PRIMARY KEY (id)" +
        ")";
  }



  private static String getCreateTagTable()
  {
    return "create table " + WayGisDao.TAG_TABLE_NAME + " ( " +
        "id bigint references gis.way(id), " +
        "key text, " +
        "value text " +
        ")";
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
