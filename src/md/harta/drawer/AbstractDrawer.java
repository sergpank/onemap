package md.harta.drawer;

import java.awt.Color;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import md.harta.geometry.CanvasPolygon;
import md.harta.geometry.Label;
import md.harta.geometry.XYPoint;
import md.harta.tile.TileCutter;

/**
 * Created by sergpank on 25.05.15.
 */
public abstract class AbstractDrawer
{
  public abstract void setStrokeColor(Color color);

  public abstract void setFillColor(Color color);

  public abstract void drawLine(XYPoint start, XYPoint end);

  public abstract void drawPolyLine(CanvasPolygon polygon, int width);

  public abstract void setFont(String name, double size);

  public abstract void fillText(String text, double xCenter, double yCenter);

  public abstract void paintLabel(Label label, int x, int y, TileCutter tileCutter);

  public abstract void setLineWidth(int i);

  public abstract void fillPolygon(double[] xPoints, double[] yPoints);

  public abstract void fillOval(double xCenter, double yCenter, double width, double height);

  public abstract void setAAEnabled(boolean enabled);

  public abstract FontRenderContext getFontRenderContext();

  public abstract void rotate(double angleRad);

  public abstract void rotate(double angleRad, double xCenter, double yCenter);

  public abstract void translate(int xShift, int yShift);

  public abstract void fill(Shape shape);
}
