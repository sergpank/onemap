package md.harta.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import md.harta.db.DbHelper;

/**
 * Created by sergpank on 30.06.15.
 */
public class DatabaseCreator
{
  public static final String INSERT_SQL = "INSERT INTO highways_gis " +
      "(highway_id, highway_name, highway_type, highway_nodes, min_lat, max_lat, min_lon, max_lon)" +
      " VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

  public static final String DROP_TABLE = "DROP TABLE %s";

  public static void createDb(String dbName)
  {
    try (Connection con = createDbIfNotExists(dbName))
    {
      createTable(con, getCreateNodesTable(), "nodes");
      createTable(con, getCreateHighwaysTable(), "highways");
      createTable(con, getCreateBuildingsTable(), "buildings");
      createTable(con, getCreateBordersTable(), "borders");

      con.createStatement().execute("CREATE EXTENSION IF NOT EXISTS Postgis;");

      createTable(con, getCreateHighwaysGisTable(), "highways_gis");
      createTable(con, getCreateBuildingsGisTable(), "buildings_gis");
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
    if (!tableExists(con, tableName))
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

  private static boolean tableExists(Connection con, String tableName)
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
          System.out.printf("Dtabase \"%s\" does not exist.\n", dbName);
          try (Statement stmt = connection.createStatement())
          {
            stmt.execute("CREATE DATABASE " + dbName);
            System.out.printf("Dtabase \"%s\" is created.\n", dbName);
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
        "  border_id bigserial NOT NULL," +
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
        "building_id bigint, " +
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
           "highway_type text, " +
           "highway_nodes bigint[], " +
           "min_lat double precision, " +
           "max_lat double precision, " +
           "min_lon double precision, " +
           "max_lon double precision," +
           "CONSTRAINT highways_pkey PRIMARY KEY (highway_id) )";
  }
  
  private static String getCreateBuildingsGisTable()
  {
    return "CREATE TABLE IF NOT EXISTS buildings_gis " +
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
  
  private static String getCreateHighwaysGisTable()
  {
    return "CREATE TABLE IF NOT EXISTS highways_gis " +
        "( " +
        "  highway_id bigint NOT NULL, " +
        "  highway_name text, " +
        "  highway_type text, " +
        "  highway_geometry geometry, " +
        "  CONSTRAINT highways_gis_pkey PRIMARY KEY (highway_id) " +
        ")";
  }

  private static String createLeisureTable()
  {
    return "CREATE TABLE IF NOT EXISTS leisure (" +
        "leisure_id bigserial NOT NULL, " +
        ""
  }

  private static String getCreateNodesTable()
  {
    return "CREATE TABLE IF NOT EXISTS nodes (" +
        "node_id bigserial NOT NULL, " +
        "lat double precision, " +
        "lon double precision, " +
        "CONSTRAINT nodes_pkey PRIMARY KEY (node_id) )";
  }

  public static void main(String[] args)
  {
    createDb("debug");
  }
}
