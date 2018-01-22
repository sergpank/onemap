package md.onemap.harta.properties;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class OsmExporterProperties
{
  private static final Properties props = new Properties();

  static
  {
    try
    {
      props.load(new FileReader("properties/osm-exporter.properties"));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public static String getDbName()
  {
    return props.getProperty("database.name");
  }

  public static String getOsmPath()
  {
    return props.getProperty("osm.file");
  }
}
