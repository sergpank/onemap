package md.onemap.harta.tile;

import md.onemap.harta.loader.PostgresLoader;
import md.onemap.harta.properties.TileGeneratorProperties;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by sergpank on 02.01.2018.
 */
public class TileGeneratorDB extends TileGenerator
{
  private static Logger LOG = LoggerFactory.getLogger(TileGeneratorDB.class);

  public static void main(String[] args)
  {
    DOMConfigurator.configure("log4j.xml");

    TileGeneratorProperties properties = new TileGeneratorProperties("properties/tile-generator-db.properties");
    TileGeneratorDB generator = new TileGeneratorDB(properties);
    generator.generate();
  }

  public TileGeneratorDB(TileGeneratorProperties props) {
    super(props, new PostgresLoader(props.source()));
  }

  @Override
  public void generate()
  {
    LOG.info("Dataset bounds: " + loader.getBounds());

    LocalDateTime generationStart = LocalDateTime.now();

    generateLevels();

    LOG.info("{} seconds", Duration.between(generationStart, LocalDateTime.now()).getSeconds());
  }
}
