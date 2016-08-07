package md.harta.drawer;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import md.harta.geometry.CanvasPolygon;
import md.harta.geometry.Label;
import md.harta.geometry.XYPoint;
import md.harta.tile.TileCutter;
import md.harta.tile.TilePalette;

/**
 * Created by sergpank on 25.05.15.
 */
public class TileDrawer extends AbstractDrawer
{
  private final Graphics2D graphics;

  public TileDrawer(Graphics2D graphics)
  {
    this.graphics = graphics;
  }

  @Override
  public void setStrokeColor(Color color)
  {
    graphics.setPaint(color);
  }

  @Override
  public void setFillColor(Color color)
  {
    graphics.setPaint(color);
  }

  @Override
  public void drawLine(XYPoint start, XYPoint end)
  {
    graphics.drawLine((int)start.getX(), (int)start.getY(), (int)end.getX(), (int)end.getY());
  }

  @Override
  public void drawPolyLine(CanvasPolygon polygon, int width)
  {
    graphics.setStroke(new BasicStroke(width));
    graphics.drawPolyline(polygon.getIntXPoints(), polygon.getIntYPoints(), polygon.getPointsNumber());
//    int[] xPoints = polygon.getIntXPoints();
//    int[] yPoints = polygon.getIntYPoints();
//    for (int i = 1; i < polygon.getPointsNumber(); i++)
//    {
//      graphics.drawLine(xPoints[i - 1], yPoints[i-1], xPoints[i], yPoints[i]);
//    }
//    graphics.drawLine(xPoints[polygon.getPointsNumber() - 1], yPoints[polygon.getPointsNumber() - 1], xPoints[0], yPoints[0]);
  }

  @Override
  public void setFont(String name, double size)
  {
    graphics.setFont(new Font(name, Font.PLAIN, (int) size));
  }

  @Override
  public void fillText(String text, double xCenter, double yCenter)
  {
    if (text != null)
    {
      graphics.setFont(new Font(TilePalette.BUILDING_FONT_NAME, Font.BOLD, TilePalette.BUILDING_FONT_SIZE));
      graphics.drawString(text, (float) xCenter, (float) yCenter);
    }
  }

  @Override
  public void paintLabel(Label label, int x, int y, TileCutter tileCutter)
  {
    Font font = new Font(label.getFont().getName(), Font.BOLD, (int) label.getFont().getSize());
    GlyphVector gv = font.createGlyphVector(graphics.getFontRenderContext(), label.getText());

    graphics.setStroke(new BasicStroke(1f));
    graphics.translate(label.getCenter().getX(), label.getCenter().getY());
    //System.out.printf("%s :: %f ; %f ; %f px\n", label.getText(), label.getLabelCenter().getX(), label.getLabelCenter().getY(), label.getFont().getSize());

    for (int i = 0; i < gv.getNumGlyphs(); i++)
    {
      Shape glyph = gv.getGlyphOutline(i);
      graphics.setPaint(TilePalette.FONT_COLOR);
      graphics.fill(glyph); // Fill the shape
      //graphics.setPaint(Color.WHITE);
      //graphics.draw(glyph); // And draw the outline
    }
    graphics.translate(-label.getCenter().getX(), -label.getCenter().getY());
  }

  @Override
  public void setLineWidth(int lineWidth)
  {
    graphics.setStroke(new BasicStroke(lineWidth));
  }

  @Override
  public void fillPolygon(double[] xPoints, double[] yPoints)
  {
    int[] intXPoints = new int[yPoints.length];
    int[] intYPoints = new int[yPoints.length];
    for (int i = 0; i < yPoints.length; i++)
    {
      intXPoints[i] = (int)xPoints[i];
      intYPoints[i] = (int)yPoints[i];
    }
    graphics.fillPolygon(intXPoints, intYPoints, intYPoints.length);
  }

  @Override
  public void fillOval(double xCenter, double yCenter, double width, double height)
  {
    graphics.fillOval((int) xCenter, (int) yCenter, (int) width, (int) height);
  }

  @Override
  public void setAAEnabled(boolean enabled)
  {
    if (enabled)
    {
      graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }
    else
    {
      graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
      graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
      graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
    }
  }

  @Override
  public FontRenderContext getFontRenderContext()
  {
    return graphics.getFontRenderContext();
  }

  @Override
  public void rotate(double angleRad)
  {
    graphics.rotate(angleRad);
  }

  @Override
  public void rotate(double angleRad, double xCenter, double yCenter)
  {
    graphics.rotate(angleRad, xCenter, yCenter);
  }

  @Override
  public void translate(int xShift, int yShift)
  {
    graphics.translate(xShift, yShift);
  }

  @Override
  public void fill(Shape shape)
  {
    graphics.fill(shape);
  }
}
