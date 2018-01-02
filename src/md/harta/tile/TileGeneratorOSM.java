package md.harta.tile;

import md.harta.loader.AbstractLoader;
import md.harta.loader.OsmLoader;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by sergpank on 02.01.2018.
 */
public class TileGeneratorOSM extends TileGenerator
{
  private static Logger LOG = LoggerFactory.getLogger(TileGeneratorOSM.class);

  public static void main(String[] args)
  {
    DOMConfigurator.configure("log4j.xml");

    GeneratorProperties properties = new GeneratorProperties("properties/osm-generator.properties");
    TileGeneratorOSM generator = new TileGeneratorOSM(properties);
    generator.generate();
  }

  public TileGeneratorOSM(GeneratorProperties properties)
  {
    super(properties);
  }

  @Override
  public void generate()
  {
    AbstractLoader loader = new OsmLoader();
    loader.load(props.source());

    LOG.info("Area bounds: " + loader.getBounds());

    LocalDateTime generationStart = LocalDateTime.now();

    generateLevel(loader);

    LOG.info("{} seconds", Duration.between(generationStart, LocalDateTime.now()).getSeconds());
  }
}
