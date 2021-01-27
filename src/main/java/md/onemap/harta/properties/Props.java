package md.onemap.harta.properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by sergpank on 04/12/2017.
 */
public class Props
{
  private static final Logger LOG = LogManager.getLogger();

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
  private String dbMaxPoolSize;

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

  public static void update()
  {
    // Update config without restarting web server.
    instance = null;
  }

  /**
   * @param propsPath Properties file is expected to be located at [src/main/resources/properties/<file_name></file_name>].
   *                  But we receive its path as [properties/<file_name>].
   */
  private Props(String propsPath)
  {
    Properties props = new Properties();
    loadFonts();
    try
    {
      File resourceFile = getResourceFile(propsPath);
      LOG.info("Loading properties from file: " + resourceFile.getAbsolutePath());

      props.load(new FileReader(resourceFile));

      startLevel = Integer.parseInt(props.getProperty("level.start"));
      endLevel = Integer.parseInt(props.getProperty("level.end"));
      tileSize = Integer.parseInt(props.getProperty("tile.size"));
      outputDir = props.getProperty("output.dir");

      osmFile = props.getProperty("osm.file");

      dbUrl = props.getProperty("db.url");
      dbLogin = props.getProperty("db.login");
      dbPassword = props.getProperty("db.password");
      dbName = props.getProperty("db.name");
      dbMaxPoolSize = props.getProperty("db.max.pool.size");

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

  private void loadFonts()
  {
    try
    {
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getResourceFile("fonts/Roboto-Thin.ttf")));
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getResourceFile("fonts/Roboto-Light.ttf")));
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getResourceFile("fonts/Roboto-Regular.ttf")));
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getResourceFile("fonts/Roboto-Medium.ttf")));
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getResourceFile("fonts/Roboto-Bold.ttf")));
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getResourceFile("fonts/Roboto-Black.ttf")));

      LOG.info("Roboto fonts are registered");
    }
    catch (IOException | FontFormatException e)
    {
      LOG.error("Unable to register Roboto fonts", e);
    }
  }

  private File getResourceFile(String propsPath)
  {
    String resourcePath = getClass()
        .getClassLoader()
        .getResource(propsPath)
        .getFile();
    return new File(resourcePath);
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

  public static String dbMaxPoolSize() {
    return inst().dbMaxPoolSize;
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

  @Override
  public String toString()
  {
    return "Props{" +
        "\n  startLevel=" + startLevel +
        "\n  endLevel=" + endLevel +
        "\n  outputDir='" + outputDir + '\'' +
        "\n  tileSize=" + tileSize +
        "\n  osmFile='" + osmFile + '\'' +
        "\n  dbUrl='" + dbUrl + '\'' +
        "\n  dbLogin='" + dbLogin + '\'' +
        "\n  dbPassword='" + dbPassword + '\'' +
        "\n  dbName='" + dbName + '\'' +
        "\n  dbPoolSize='" + dbMaxPoolSize + '\'' +
        "\n  cacheEnabled='" + cacheEnabled + '\'' +
        "\n  cacheDir='" + cacheDir + '\'' +
        "\n  debugTileNumber=" + debugTileNumber +
        "\n  debugTileBorder=" + debugTileBorder +
        "\n}";
  }
}
