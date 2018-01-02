package md.harta.tile;

import md.harta.geometry.BoundsLatLon;
import md.harta.loader.AbstractLoader;
import md.harta.osm.Building;
import md.harta.osm.Highway;
import md.harta.projector.AbstractProjector;
import md.harta.projector.MercatorProjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Created by sergpank on 23.05.15.
 */
public abstract class TileGenerator
{
  public static final int TILE_SIZE = 256;

  private static Logger LOG = LoggerFactory.getLogger(TileGenerator.class);

  protected GeneratorProperties props;

  public TileGenerator(GeneratorProperties properties) {
    this.props = properties;
  }

  public abstract void generate();

  protected void generateLevel(AbstractLoader loader)
  {
    for (int level = props.startLevel(); level <= props.endLevel(); level++)
    {
      AbstractProjector projector = new MercatorProjector(level);

      TileCutter tileCutter = new TileCutter(projector, TILE_SIZE, level, loader.getBounds());
      tileCutter.cut();

      generateLevelTiles(loader, level, projector, tileCutter);
    }
  }

  protected void generateLevelTiles(AbstractLoader loader, int level, AbstractProjector projector, TileCutter tileCutter)
  {
    long tileCnt = 0;
    long start = System.currentTimeMillis();
    TileFileWriter tileWriter = new TileFileWriter(TILE_SIZE, props.outputDir(), props.source());

    for (int y = tileCutter.getMinTileYindex(); y <= tileCutter.getMaxTileYindex(); y++)
    {
      for (int x = tileCutter.getMinTileXindex(); x <= tileCutter.getMaxTileXindex(); x++)
      {
        BoundsLatLon tileBounds = tileCutter.getTileBounds(x, y, 0);

        Collection<Highway> highways = loader.getHighways(level, tileBounds);
        Collection<Building> buildings = loader.getBuildings(level, tileBounds);

        tileWriter.drawTile(level, x, y, tileBounds.toXY(projector), projector, highways, buildings);

        tileCnt++;
      }
    }

    long end = System.currentTimeMillis();
    LOG.info("level {} -> {} ms; {} tiles; {} ms per tile", level, (end - start), tileCnt, (end - start) / tileCnt);
  }
}
