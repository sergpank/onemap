package md.onemap.harta.tile;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.loader.AbstractLoader;
import md.onemap.harta.projector.AbstractProjector;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class AbstractTileDrawer
{
  protected int tileSize;

  public AbstractTileDrawer(int tileSize)
  {
    this.tileSize = tileSize;
  }

  public abstract BufferedImage drawTile(int level, int x, int y, AbstractProjector projector,
                                AbstractLoader loader, BoundsLatLon tileBounds);

  protected Graphics2D createGraphics(BufferedImage bi)
  {
    Graphics2D graphics = bi.createGraphics();

    graphics.setPaint(Palette.BACKGROUND_COLOR);
    graphics.fillRect(0, 0, tileSize, tileSize);
    return graphics;
  }

  protected void drawTileNumber(int x, int y, int level, Graphics2D graphics)
  {
    String levelLabel = level + "";
    String xyLabel = String.format("(%d; %d)", x, y);

    Font font = new Font(Font.MONOSPACED, Font.BOLD, 14);
    FontMetrics fontMetrics = graphics.getFontMetrics(font);

    int levelWidth = fontMetrics.stringWidth(levelLabel);
    int xyWidth = fontMetrics.stringWidth(xyLabel);
    int h = fontMetrics.getHeight();

    graphics.setColor(Color.RED);
    graphics.drawString(levelLabel, tileSize / 2 - levelWidth / 2, tileSize / 2 + h / 2);
    graphics.drawString(xyLabel, tileSize / 2 - xyWidth / 2, tileSize / 2 + h / 2 * 3 + 8);
  }

  protected void drawTileBorder(Graphics2D graphics)
  {
    graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    graphics.drawLine(1,            1,            tileSize - 1, 1);
    graphics.drawLine(1,            1,            1, tileSize - 1);
  }
}
