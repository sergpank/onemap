package md.onemap.harta.tile;

import md.onemap.harta.loader.PostgisLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by sergpank on 02.01.2018.
 */
public class TileGeneratorGIS extends TileGenerator
{
  private static final Logger LOG = LogManager.getLogger();

  public static void main(String[] args)
  {
    TileGeneratorGIS generator = new TileGeneratorGIS();
    generator.generate();
  }

  public TileGeneratorGIS()
  {
    super(new PostgisLoader());
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
