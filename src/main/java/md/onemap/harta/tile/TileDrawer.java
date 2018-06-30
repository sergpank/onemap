package md.onemap.harta.tile;

import md.onemap.harta.drawer.AbstractDrawer;
import md.onemap.harta.drawer.AwtDrawer;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.loader.AbstractLoader;
import md.onemap.harta.osm.*;
import md.onemap.harta.painter.*;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.properties.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;

/**
 * Created by sergpank on 04/12/2017.
 */
public class TileDrawer
{
  private static final Logger LOG = LoggerFactory.getLogger(TileDrawer.class);
  private int tileSize;

  public TileDrawer(int tileSize)
  {
    this.tileSize = tileSize;
  }

  public BufferedImage drawTile(int level, int x, int y, AbstractProjector projector,
                                AbstractLoader loader, BoundsLatLon tileBounds)
  {
    BufferedImage bi = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = bi.createGraphics();

    graphics.setPaint(Palette.BACKGROUND_COLOR);
    graphics.fillRect(0, 0, tileSize, tileSize);

    AbstractDrawer drawer = new AwtDrawer(graphics);
    drawer.setAAEnabled(true);

    BoundsXY boundsXY = tileBounds.toXY(projector);

    Collection<Waterway> waterways = loader.getWaterways(level, tileBounds);
    Collection<Natural> nature = loader.getNature(level, tileBounds);
    Collection<Highway> highways = loader.getHighways(level, tileBounds);
    Collection<Building> buildings = loader.getBuildings(level, tileBounds);
    Collection<Leisure> leisure = loader.getLeisure(level, tileBounds);
    Collection<Landuse> landuse = loader.getLanduse(level, tileBounds);

    new LandusePainter(projector,  boundsXY).draw(drawer, landuse, level);
    new LeisurePainter(projector,  boundsXY).draw(drawer, leisure, level);
    new WaterwayPainter(projector, boundsXY).draw(drawer, waterways, level);
    new NaturePainter(projector, boundsXY).drawWater(drawer, nature, level);
    new BuildingPainter(projector, boundsXY).draw(drawer, buildings, level);
    new HighwayPainter(projector,  boundsXY).draw(drawer, highways, level);

    if (Props.debugTileNumber())
    {
      drawTileNumber(x, y, level, graphics);
    }

    if (Props.debugTileBorder())
    {
      drawTileBorder(graphics);
    }

    return bi;
  }

  private void drawTileNumber(int x, int y, int level, Graphics2D graphics)
  {
    String levelLabel = level + "";
    String xyLabel = String.format("(%d; %d)", x, y);

    Font font = new Font("Calibri", Font.BOLD, 14);
    FontMetrics fontMetrics = graphics.getFontMetrics(font);

    int levelWidth = fontMetrics.stringWidth(levelLabel);
    int xyWidth = fontMetrics.stringWidth(xyLabel);
    int h = fontMetrics.getHeight();

    graphics.setColor(Color.RED);
    graphics.drawString(levelLabel, tileSize / 2 - levelWidth / 2, tileSize / 2 + h / 2);
    graphics.drawString(xyLabel, tileSize / 2 - xyWidth / 2, tileSize / 2 + h / 2 * 3 + 8);
  }

  private void drawTileBorder(Graphics2D graphics)
  {
    graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    graphics.drawLine(0,            0,            tileSize - 1, 0);
    graphics.drawLine(0,            0,            0, tileSize - 1);
  }
}
