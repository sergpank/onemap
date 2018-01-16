package md.onemap.harta.tile;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.loader.AbstractLoader;
import md.onemap.harta.osm.Building;
import md.onemap.harta.osm.Highway;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.projector.MercatorProjector;
import md.onemap.harta.util.TimePrettyPrint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;

/**
 * Created by sergpank on 23.05.15.
 */
public abstract class TileGenerator
{
  public static final int TILE_SIZE = 512;

  private static Logger LOG = LoggerFactory.getLogger(TileGenerator.class);

  protected GeneratorProperties props;

  public TileGenerator(GeneratorProperties properties) {
    this.props = properties;
  }

  public abstract void generate();

  protected void generateLevels(AbstractLoader loader)
  {
    LOG.info("Output directory: {}", new File(props.outputDir()).getAbsolutePath());

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
    TileFileWriter tileWriter = new TileFileWriter(TILE_SIZE, props.outputDir());
    double totalTiles = (tileCutter.getMaxTileXindex() - tileCutter.getMinTileXindex() + 1)
                    * (tileCutter.getMaxTileYindex() - tileCutter.getMinTileYindex() + 1);

    short logStep = 10;
    long logTileStep = (long)totalTiles / logStep; // 10%

    for (int y = tileCutter.getMinTileYindex(); y <= tileCutter.getMaxTileYindex(); y++)
    {
      for (int x = tileCutter.getMinTileXindex(); x <= tileCutter.getMaxTileXindex(); x++)
      {
        BoundsLatLon tileBounds = tileCutter.getTileBounds(x, y, 0);

        Collection<Highway> highways = loader.getHighways(level, tileBounds);
        Collection<Building> buildings = loader.getBuildings(level, tileBounds);

        tileWriter.drawTile(level, x, y, tileBounds.toXY(projector), projector, highways, buildings);

        tileCnt++;
        if (logTileStep > 0 && tileCnt % logTileStep == 0) {
          LOG.info(String.format("level %2d -> %3.2f %%; %d of %.0f tiles", level, tileCnt / totalTiles * 100, tileCnt, totalTiles));
        }
      }
    }

    long end = System.currentTimeMillis();
    LOG.info("level {} -> {}; {} tiles; {} ms per tile", level, TimePrettyPrint.prettyPrint(end - start), tileCnt, (end - start) / tileCnt);
  }
}
