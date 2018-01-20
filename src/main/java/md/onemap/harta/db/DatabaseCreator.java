package md.onemap.harta.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sergpank on 30.06.15.
 */
public class DatabaseCreator
{
  private static Logger log = LoggerFactory.getLogger(DatabaseCreator.class);

  public static final String DROP_TABLE = "DROP TABLE %s";

  public static void createDb(String dbName)
  {
    try (Connection con = createDbIfNotExists(dbName))
    {
      createTable(con, getCreateNodesTable(), "nodes");
      createTable(con, getCreateHighwaysTable(), "highways");
      createTable(con, getCreateBuildingsTable(), "buildings");
      createTable(con, getCreateBordersTable(), "borders");
      createTable(con, getCreateLeisureTable(), "leisure");
      createTable(con, getCreateWaterwayTable(), "waterway");
      createTable(con, getCreateLanduseTable(), "landuse");

      con.createStatement().execute("CREATE EXTENSION IF NOT EXISTS Postgis;");
      con.createStatement().execute("CREATE SCHEMA IF NOT EXISTS gis;");

      createTable(con, getCreateHighwaysGisTable(), "highways_gis");
      createTable(con, getCreateBuildingsGisTable(), "buildings_gis");
      createTable(con, getCreateBordersGisTable(), "borders_gis");
      createTable(con, getCreateLeisureGisTable(), "leisure_gis");
      createTable(con, getCreateWaterwayGisTable(), "waterway_gis");
      createTable(con, getCreateLanduseGisTable(), "landuse_gis");
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  private static void createTable(Connection con, String query, String tableName)
  {
    try (Statement statement = con.createStatement())
    {
      statement.execute(query);
      System.out.printf("Table \"%s\" is created.\n", tableName);
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  private static void reCreateTable(Connection con, String query, String tableName)
  {
    if (!isTableExists(con, tableName))
    {
      createTable(con, query, tableName);
    }
    else
    {
      try (Statement st = con.createStatement())
      {
        System.out.printf("\"%s\" table already exists.\n", tableName);
        st.execute(String.format(DROP_TABLE, tableName));
        st.execute(query);
        System.out.printf("Table \"%s\" is REcreated.\n", tableName);
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
      try (Statement statement = connection.createStatement())
      {
        ResultSet rs = statement.executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'");
        if (!rs.next())
        {
          log.info("Dtabase {} does not exist.\n", dbName);
          try (Statement stmt = connection.createStatement())
          {
            stmt.execute("CREATE DATABASE " + dbName);
            log.info("Dtabase {} is created.\n", dbName);
          }
        }
        else
        {
          System.out.println("Database already exists");
        }
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return DbHelper.getNewConnection(dbName);
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

  private static String getCreateBordersGisTable()
  {
    return "CREATE TABLE IF NOT EXISTS gis.borders_gis (" +
        "  border_id bigint NOT NULL," +
        "  name text," +
        "  border_geometry geometry," +
        "  CONSTRAINT borders_gis_pkey PRIMARY KEY (border_id) )";
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

  private static String getCreateBuildingsGisTable()
  {
    return "CREATE TABLE IF NOT EXISTS gis.buildings_gis " +
        "( " +
        "  building_id bigint NOT NULL, " +
        "  housenumber text, " +
        "  street text, " +
        "  height text, " +
        "  design text, " +
        "  levels integer, " +
        "  building_geometry geometry, " +
        "  CONSTRAINT buildings_gis_pkey PRIMARY KEY (building_id)" +
        ")";
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
  
  private static String getCreateHighwaysGisTable()
  {
    return "CREATE TABLE IF NOT EXISTS gis.highways_gis " +
        "( " +
        "  highway_id bigint NOT NULL, " +
        "  highway_name text, " +
        "  highway_name_ru text, " +
        "  highway_name_old text, " +
        "  highway_type text, " +
        "  highway_geometry geometry, " +
        "  CONSTRAINT highways_gis_pkey PRIMARY KEY (highway_id) " +
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

  private static String getCreateLeisureGisTable()
  {
    return "CREATE TABLE IF NOT EXISTS gis.leisure_gis (" +
        "leisure_id bigint NOT NULL, " +
        "leisure_type text, " +
        "leisure_name text, " +
        "leisure_geometry geometry, " +
        "CONSTRAINT leisure_gis_pkey PRIMARY KEY (leisure_id) )";
  }

  private static String getCreateWaterwayTable()
  {
    return "CREATE TABLE IF NOT EXISTS waterways (" +
        "waterway_id bigint NOT NULL, " +
        "waterway_type text, " +
        "waterway_name text, " +
        "waterway_nodes bigint[], " +
        "min_lat double precision, " +
        "max_lat double precision, " +
        "min_lon double precision, " +
        "max_lon double precision," +
        "CONSTRAINT waterway_pkey PRIMARY KEY (waterway_id) )";
  }

  private static String getCreateWaterwayGisTable()
  {
    return "CREATE TABLE IF NOT EXISTS gis.waterways_gis (" +
        "waterway_id bigint NOT NULL, " +
        "waterway_type text, " +
        "waterway_name text, " +
        "waterway_geometry geometry, " +
        "CONSTRAINT waterway_gis_pkey PRIMARY KEY (waterway_id) )";
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

  private static String getCreateLanduseGisTable()
  {
    return "CREATE TABLE IF NOT EXISTS gis.landuse_gis (" +
        "landuse_id bigint NOT NULL, " +
        "landuse_type text, " +
        "landuse_geometry geometry, " +
        "CONSTRAINT landuse_gis_pkey PRIMARY KEY (landuse_id) )";
  }

  private static String getCreateNodesTable()
  {
    return "CREATE TABLE IF NOT EXISTS nodes (" +
        "node_id bigint NOT NULL, " +
        "lat double precision, " +
        "lon double precision, " +
        "CONSTRAINT nodes_pkey PRIMARY KEY (node_id) )";
  }

  public static void main(String[] args)
  {
    createDb("gradina_botanica");
  }
}
