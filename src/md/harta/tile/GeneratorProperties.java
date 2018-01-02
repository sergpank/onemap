package md.harta.tile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by sergpank on 04/12/2017.
 */
public class GeneratorProperties
{
  private static final Logger LOG = LoggerFactory.getLogger(GeneratorProperties.class);

  private Integer startLevel;
  private Integer endLevel;
  private String outputDir;
  private String source;

  public GeneratorProperties(String propsPath)
  {
    Properties props = new Properties();
    try
    {
      LOG.info("Loading properties from file: " + new File(propsPath).getAbsolutePath());
      props.load(new FileReader(propsPath));
      startLevel = Integer.parseInt(props.getProperty("level.start"));
      endLevel = Integer.parseInt(props.getProperty("level.end"));
      outputDir = props.getProperty("output.dir");
      source = props.getProperty("source");
      LOG.info("Loaded properties: " + toString());
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

  public String source()
  {
    return source;
  }

  public int startLevel()
  {
    return startLevel;
  }

  public int endLevel()
  {
    return endLevel;
  }

  @Override
  public String toString()
  {
    return "GeneratorProperties{" +
        "startLevel=" + startLevel +
        ", endLevel=" + endLevel +
        ", outputDir='" + outputDir + '\'' +
        ", source='" + source + '\'' +
        '}';
  }
}
