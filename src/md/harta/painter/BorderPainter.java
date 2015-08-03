package md.harta.painter;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.scene.text.Font;
import md.harta.drawer.AbstractDrawer;
import md.harta.geometry.Bounds;
import md.harta.geometry.CanvasPolygon;
import md.harta.geometry.Label;
import md.harta.geometry.XYPoint;
import md.harta.osm.Border;
import md.harta.projector.AbstractProjector;
import md.harta.tile.TileCutter;
import md.harta.tile.TilePalette;
import md.harta.util.BoxIntersector;

/**
 * Created by sergpank on 05.05.15.
 */
public class BorderPainter extends AbstractPainter
{
  public static final String FONT_NAME = "Tahoma";
  public static final int FONT_SIZE = 14;

  public BorderPainter(AbstractProjector projector, Bounds bounds)
  {
    super(projector, bounds);
  }

  public void drawBorders(AbstractDrawer drawer, Collection<Border> borders, int x, int y, TileCutter tileCutter)
  {
    List<Label> labels = new ArrayList<>();

    drawer.setStrokeColor(TilePalette.BORDER_COLOR);
    for (Border border : borders)
    {
      CanvasPolygon polygon = createPolygon(border);
      Bounds bounds = new Bounds(projector, border.getBounds());
      double width = bounds.getxMax() - bounds.getxMin();

      shiftPoints(this.bounds.getxMin(), polygon.getxPoints());
      shiftPoints(this.bounds.getyMin(), polygon.getyPoints());
      drawer.drawPolyLine(polygon);
      labels.add(createRegionLabel(polygon, border.getName(), width));
    }

    paintLabels(drawer, labels, x, y, tileCutter);
  }

  private void paintLabels(AbstractDrawer drawer, List<Label> labels, int x, int y, TileCutter tileCutter)
  {
    drawer.setFillColor(TilePalette.FONT_COLOR);
    for (int i = 0; i < labels.size() - 1; i++)
    {
      Label label = labels.get(i);
      for (int j = i + 1; j < labels.size();)
      {
        Label lbl = labels.get(j);
        if (BoxIntersector.intersectXY(label.getBounds(), lbl.getBounds()))
        {
          labels.remove(j);
        }
        else
        {
          j++;
        }
      }
    }
    for (Label label : labels)
    {
//      drawer.setFont(label.getFont().getName(), label.getFont().getSize());
//      drawer.fillText(label.getText(), label.getLabelCenter().getX(), label.getLabelCenter().getY());
      drawer.paintLabel(label, x, y, tileCutter);
    }
  }

  private Label createRegionLabel(CanvasPolygon polygon, String label, double regionWidth)
  {
    Font font = null;
    XYPoint center = null;
    if (label != null)
    {
      font = getFont(label, regionWidth);
      center = getLabelCenter(polygon, label, font.getName(), font.getSize());
    }
    //System.out.printf("%s - %d\n", label, (int) font.getSize());
    return new Label(label, center, font.getName(), (int) font.getSize());
  }

  private Font getFont(String label, double regionWidth)
  {
//    FontMetrics fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(new Font(FONT_NAME, FONT_SIZE));
//    float labelWidth = fontMetrics.computeStringWidth(label);
//    return calcFontSize(label, regionWidth, FONT_SIZE, labelWidth > regionWidth);
    return new Font(FONT_NAME, FONT_SIZE);
  }

  private Font calcFontSize(String label, double regionWidth, int fontSize, boolean labelWasWider)
  {
    if (labelWasWider)
    {
      fontSize--;
      Font font = new Font(FONT_NAME, fontSize);
      FontMetrics fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(font);
      float labelWidth = fontMetrics.computeStringWidth(label);
      if (labelWidth <= regionWidth)
      {
        return font;
      }
      else
      {
        if (fontSize >= 25)
        {
          return font;
        }
        return calcFontSize(label, regionWidth, fontSize, true);
      }
    }
    else
    {
      fontSize++;
      Font font = new Font(FONT_NAME, fontSize);
      FontMetrics fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(font);
      float labelWidth = fontMetrics.computeStringWidth(label);
      if (labelWidth >= regionWidth)
      {
        return font;
      }
      else
      {
        if (fontSize >= 25)
        {
          return font;
        }
        return calcFontSize(label, regionWidth, fontSize, false);
      }
    }
  }
}
