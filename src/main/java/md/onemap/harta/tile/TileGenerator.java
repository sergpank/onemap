package md.onemap.harta.tile;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.loader.AbstractLoader;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.projector.MercatorProjector;
import md.onemap.harta.properties.Props;
import md.onemap.harta.util.TimePrettyPrint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sergpank on 23.05.15.
 */
public abstract class TileGenerator
{
  private static final Logger LOG = LogManager.getLogger();

  private Map<Integer, TileBoundsCalculator> tileBoundsCache = new HashMap<>();

  protected AbstractLoader loader;

  private final int startLevel;
  private final int endLevel;
  private final int tileSize;

  public TileGenerator(AbstractLoader loader)
  {
    this.startLevel = Props.startLevel();
    this.endLevel = Props.endLevel();
    this.tileSize = Props.tileSize();
    this.loader = loader;
  }

  public abstract void generate();

  protected void generateLevels()
  {
    LOG.info("Output directory: {}", new File(Props.outputDir()).getAbsolutePath());

    for (int level = startLevel; level <= endLevel; level++)
    {
      AbstractProjector projector = new MercatorProjector(level);

      TileCutter tileCutter = new TileCutter(projector, tileSize, level, loader.getBounds());
      tileCutter.cut();

      generateLevelTiles(level, projector, tileCutter);
    }
  }

  private void generateLevelTiles(int level, AbstractProjector projector, TileCutter tileCutter)
  {
    long tileCnt = 0;
    long start = System.currentTimeMillis();
    double totalTiles = (tileCutter.getMaxTileXindex() - tileCutter.getMinTileXindex() + 1)
        * (tileCutter.getMaxTileYindex() - tileCutter.getMinTileYindex() + 1);

    short logStep = 10;
    long logTileStep = (long) totalTiles / logStep; // 10%

    for (int y = tileCutter.getMinTileYindex(); y <= tileCutter.getMaxTileYindex(); y++)
    {
      for (int x = tileCutter.getMinTileXindex(); x <= tileCutter.getMaxTileXindex(); x++)
      {
        BufferedImage tile = generateTile(x, y, level, projector, tileCutter.getTileBounds(x, y));
        writeTile(tile, level, x, y);

        tileCnt++;
        if (logTileStep > 0 && tileCnt % logTileStep == 0)
        {
          LOG.info(String.format("level %2d -> %3.2f %%; %d of %.0f tiles", level, tileCnt / totalTiles * 100, tileCnt, totalTiles));
        }
      }
    }

    long end = System.currentTimeMillis();
    LOG.info("level {} -> {}; {} tiles; {} ms per tile", level, TimePrettyPrint.prettyPrint(end - start), tileCnt, (end - start) / tileCnt);
  }

  public BufferedImage generateTile(int x, int y, int level, AbstractProjector projector, BoundsLatLon tileBounds)
  {
    AbstractTileDrawer tileDrawer = new GeneralizedTileDrawer(tileSize);
    BufferedImage tile = tileDrawer.drawTile(level, x, y, projector, loader, tileBounds);

    return tile;
  }

  public BufferedImage generateTileCached(int x, int y, int level)
  {
    BufferedImage tile = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
    File tileFile = null;

    if (Props.cacheEnabled())
    {
      String tileName = getTileName(level, x, y);
      tileFile = new File(Props.cacheDir(), tileName);
      LOG.info("Look for cache @: {}", tileFile.getAbsolutePath());
    }

    if (tileFile != null && tileFile.exists())
    {
      LOG.info("Getting tile from cache: {}", tileFile.getAbsolutePath());
      try
      {
        tile = ImageIO.read(tileFile);
      }
      catch (IOException e)
      {
        LOG.error("Unable to read file: {}", tileFile.getAbsolutePath());
      }
    }
    else
    {
      TileBoundsCalculator boundsCalculator = getTileBoundsCalculator(level);
      BoundsLatLon tileBounds = boundsCalculator.getTileBounds(x, y);

      tile = generateTile(x, y, level, boundsCalculator.getProjector(), tileBounds);
      if (Props.cacheEnabled())
      {
        writeTile(tile, level, x, y);
      }
    }

    return tile;
  }

  private void writeTile(BufferedImage bi, int level, int x, int y)
  {
    try
    {
      String tileName = getTileName(level, x, y);
      File tileFile = new File(Props.cacheDir(), tileName);

      LOG.info("Generating tile: {}", tileFile.getAbsolutePath());
      tileFile.mkdirs();

      ImageIO.write(bi, "PNG", tileFile);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  private String getTileName(int level, int x, int y)
  {
    return String.format("%d/tile_%d_%d_%d.png", level, level, y, x);
  }

  private synchronized TileBoundsCalculator getTileBoundsCalculator(int level)
  {
    TileBoundsCalculator calculator = tileBoundsCache.get(level);
    if (calculator == null)
    {
      LOG.info("New Bounds Calculator for level {}", level);
      AbstractProjector projector = new MercatorProjector(level);
      calculator = new TileBoundsCalculator(Props.tileSize(), projector);
      tileBoundsCache.put(level, calculator);
    }
    return calculator;
  }
}
