package md.harta.tile;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by sergpank on 04/12/2017.
 */
public class GeneratorProperties
{
  private Integer startLevel;
  private Integer endLevel;
  private String outputDir;
  private String dbName;

  public GeneratorProperties(String propsPath)
  {
    Properties props = new Properties();
    try
    {
      props.load(new FileReader(propsPath));
      startLevel = Integer.parseInt(props.getProperty("level.start"));
      endLevel = Integer.parseInt(props.getProperty("level.end"));
      outputDir = props.getProperty("output.dir");
      dbName = props.getProperty("database.name");
    }
    catch (IOException e)
    {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public String outputDir()
  {
    return outputDir;
  }

  public String dbName()
  {
    return dbName;
  }

  public int startLevel()
  {
    return startLevel;
  }

  public int endLevel()
  {
    return endLevel;
  }
}
