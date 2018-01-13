package md.onemap.harta.drawer;

import java.awt.Shape;
import java.awt.font.FontRenderContext;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import md.onemap.harta.geometry.CanvasPolygon;
import md.onemap.harta.geometry.Label;
import md.onemap.harta.geometry.XYPoint;
import md.onemap.harta.tile.TileCutter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by sergpank on 25.05.15.
 */
public class FxDrawer extends AbstractDrawer
{
  private final GraphicsContext gc;

  public FxDrawer(GraphicsContext gc)
  {
    this.gc = gc;
  }

  @Override
  public void setStrokeColor(java.awt.Color color)
  {
    gc.setStroke(new Color(color.getRed() / 255., color.getGreen() / 255., color.getBlue() / 255., color.getAlpha() / 255.));
//    gc.setStroke(Color.RED);
  }

  @Override
  public void setFillColor(java.awt.Color color)
  {
    gc.setFill(new Color(color.getRed() / 255., color.getGreen() / 255., color.getBlue() / 255., color.getAlpha() / 255.));
//    gc.setFill(Color.BLACK);
  }

  @Override
  public void drawLine(XYPoint start, XYPoint end)
  {
    gc.strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
  }

  @Override
  public void setFont(String name, double size)
  {
    gc.setFont(new Font(name, size));
  }

  @Override
  public void fillText(String text, double xCenter, double yCenter)
  {
    gc.fillText(text, xCenter, yCenter);
  }

  @Override
  public void paintLabel(Label label, int x, int y, TileCutter tileCutter)
  {
    throw new NotImplementedException();
  }

  @Override
  public void setLineWidth(int i)
  {
    gc.setLineWidth(i);
  }

  @Override
  public void fillPolygon(double[] xPoints, double[] yPoints)
  {
    gc.setLineWidth(1);
    gc.fillPolygon(xPoints, yPoints, yPoints.length);
  }

  @Override
  public void fillOval(double xCenter, double yCenter, double width, double height)
  {
    gc.fillOval(xCenter, yCenter, width, height);
  }

  @Override
  public void drawPolyLine(CanvasPolygon polygon, int width)
  {
    gc.strokePolyline(polygon.getxPoints(), polygon.getyPoints(), polygon.getPointsNumber());
  }

  @Override
  public void setAAEnabled(boolean enabled)
  {
    throw new NotImplementedException();
  }

  @Override
  public FontRenderContext getFontRenderContext()
  {
    throw new NotImplementedException();
  }

  @Override
  public void rotate(double angleRad)
  {
    throw new NotImplementedException();
  }

  @Override
  public void rotate(double angleRad, double xCenter, double yCenter)
  {
    throw new NotImplementedException();
  }

  @Override
  public void translate(int xShift, int yShift)
  {
    throw new NotImplementedException();
  }

  @Override
  public void fill(Shape shape)
  {
    throw new NotImplementedException();
  }
}
