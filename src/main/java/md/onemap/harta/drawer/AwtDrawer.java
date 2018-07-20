package md.onemap.harta.drawer;

import md.onemap.harta.geometry.CanvasPolygon;
import md.onemap.harta.geometry.Label;
import md.onemap.harta.geometry.XYPoint;
import md.onemap.harta.tile.Palette;
import md.onemap.harta.tile.TileCutter;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;

/**
 * Created by sergpank on 25.05.15.
 */
public class AwtDrawer extends AbstractDrawer
{
  private final Graphics2D graphics;

  public AwtDrawer(Graphics2D graphics)
  {
    this.graphics = graphics;
    setAAEnabled(true);
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
  public void drawPolyLine(CanvasPolygon polygon, int width, boolean dashed)
  {
    BasicStroke stroke = dashed
        ? new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3f, new float[]{width * 2f, width}, 0.0f)
        : new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    graphics.setStroke(stroke);
    graphics.drawPolyline(polygon.getIntXPoints(), polygon.getIntYPoints(), polygon.getPointsNumber());
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
      graphics.setFont(new Font(Palette.BUILDING_FONT_NAME, Font.PLAIN, Palette.BUILDING_FONT_SIZE));
      graphics.drawString(text, (float) xCenter, (float) yCenter);
    }
  }

  @Override
  public void paintLabel(Label label, int x, int y, TileCutter tileCutter)
  {
    Font font = new Font(Palette.HIGHWAY_FONT_NAME, Font.BOLD, Palette.HIGHWAY_FONT_SIZE);
    GlyphVector gv = font.createGlyphVector(graphics.getFontRenderContext(), label.getText());

    graphics.setStroke(new BasicStroke(1f));
    graphics.translate(label.getCenter().getX(), label.getCenter().getY());
    //System.out.printf("%s :: %f ; %f ; %f px\n", label.getText(), label.getLabelCenter().getX(), label.getLabelCenter().getY(), label.getFont().getSize());

    for (int i = 0; i < gv.getNumGlyphs(); i++)
    {
      Shape glyph = gv.getGlyphOutline(i);
      graphics.setPaint(Palette.FONT_COLOR);
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
