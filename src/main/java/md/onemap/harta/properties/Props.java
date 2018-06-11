package md.onemap.harta.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by sergpank on 04/12/2017.
 */
public class Props
{
  private static final Logger LOG = LoggerFactory.getLogger(Props.class);

  private static Props instance;

  private Integer startLevel;
  private Integer endLevel;
  private String outputDir;
  private Integer tileSize;

  private String osmFile;

  private String dbUrl;
  private String dbLogin;
  private String dbPassword;
  private String dbName;

  private boolean cacheEnabled;
  private String cacheDir;

  private final boolean debugTileNumber;
  private final boolean debugTileBorder;

  private static Props inst()
  {
    if (instance == null)
    {
      instance = new Props("properties/application.properties");
    }
    return instance;
  }

  /**
   * @param propsPath Properties file is expected to be located at [src/main/resources/properties/<file_name></file_name>].
   *                  But we receive its path as [properties/<file_name>].
   */
  private Props(String propsPath)
  {
    Properties props = new Properties();
    try
    {
      String resourcePath = getClass().getClassLoader().getResource(propsPath).getFile();
      LOG.info("Loading properties from file: " + new File(resourcePath).getAbsolutePath());

      props.load(new FileReader(resourcePath));

      startLevel = Integer.parseInt(props.getProperty("level.start"));
      endLevel = Integer.parseInt(props.getProperty("level.end"));
      tileSize = Integer.parseInt(props.getProperty("tile.size"));
      outputDir = props.getProperty("output.dir");

      osmFile = props.getProperty("osm.file");

      dbUrl = props.getProperty("db.url");
      dbLogin = props.getProperty("db.login");
      dbPassword = props.getProperty("db.password");
      dbName = props.getProperty("db.name");

      cacheEnabled = Boolean.valueOf(props.getProperty("cache.enabled"));
      cacheDir = props.getProperty("cache.dir");

      debugTileNumber = Boolean.parseBoolean(props.getProperty("debug.tile.number"));
      debugTileBorder = Boolean.parseBoolean(props.getProperty("debug.tile.border"));

      LOG.info("Loaded properties: " + toString());
    }
    catch (IOException e)
    {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public static String outputDir()
  {
    return inst().outputDir;
  }

  public static int startLevel()
  {
    return inst().startLevel;
  }

  public static int endLevel()
  {
    return inst().endLevel;
  }

  public static int tileSize()
  {
    return inst().tileSize;
  }

  public static String osmFile()
  {
    return inst().osmFile;
  }

  public static String dbUrl()
  {
    return inst().dbUrl;
  }

  public static String dbLogin()
  {
    return inst().dbLogin;
  }

  public static String dbPassword()
  {
    return inst().dbPassword;
  }

  public static String dbName()
  {
    return inst().dbName;
  }

  public static boolean cacheEnabled()
  {
    return inst().cacheEnabled;
  }

  public static String cacheDir()
  {
    return inst().cacheDir;
  }

  public static boolean debugTileNumber()
  {
    return inst().debugTileNumber;
  }

  public static boolean debugTileBorder()
  {
    return inst().debugTileBorder;
  }

  @Override public String toString()
  {
    return "Props{" +
            "startLevel=" + startLevel +
            ", endLevel=" + endLevel +
            ", outputDir='" + outputDir + '\'' +
            ", tileSize=" + tileSize +
            ", osmFile='" + osmFile + '\'' +
            ", dbUrl='" + dbUrl + '\'' +
            ", dbLogin='" + dbLogin + '\'' +
            ", dbPassword='" + dbPassword + '\'' +
            ", dbName='" + dbName + '\'' +
            ", cacheEnabled='" + cacheEnabled + '\'' +
            ", cacheDir='" + cacheDir + '\'' +
            ", debugTileNumber=" + debugTileNumber +
            ", debugTileBorder=" + debugTileBorder +
            '}';
  }
}
