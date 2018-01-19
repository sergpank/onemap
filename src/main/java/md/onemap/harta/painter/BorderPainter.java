package md.onemap.harta.painter;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.scene.text.Font;
import md.onemap.harta.drawer.AbstractDrawer;
import md.onemap.harta.geometry.*;
import md.onemap.harta.osm.Border;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.tile.TileCutter;
import md.onemap.harta.tile.TilePalette;
import md.onemap.harta.util.BoxIntersector;
import md.onemap.harta.util.TextUtil;

/**
 * Created by sergpank on 05.05.15.
 */
public class BorderPainter extends AbstractPainter
{
  public static final String FONT_NAME = "Tahoma";
  public static final int FONT_SIZE = 14;

  public BorderPainter(AbstractProjector projector, BoundsXY bounds)
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
      BoundsXY bounds =border.getBounds().toXY(projector);
      double width = bounds.getXmax() - bounds.getXmin();

      shiftPoints(this.bounds.getXmin(), polygon.getxPoints());
      shiftPoints(this.bounds.getYmin(), polygon.getyPoints());
      drawer.drawPolyLine(polygon, 1);
      labels.add(createRegionLabel(polygon, border.getName()));
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

  private Label createRegionLabel(CanvasPolygon polygon, String label)
  {
    XYPoint center = null;
    float stringWidth = 0;
    float stringHeight = 0;
    if (label != null)
    {
      stringWidth = TextUtil.getStringWidth(label, FONT_NAME, FONT_SIZE);
      stringHeight = TextUtil.getStringHeight(FONT_NAME, FONT_SIZE);
      center = getLabelCenter(polygon, label, stringWidth, stringHeight);
    }
    //System.out.printf("%s - %d\n", label, (int) font.getSize());
    return new Label(label, center, stringHeight, stringWidth);
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
