package md.harta.util;

import com.sun.javafx.tk.Toolkit;
import javafx.scene.text.Font;
import md.harta.geometry.GeometryUtil;
import md.harta.geometry.XYPoint;

/**
 * Created by sergpank on 16.08.15.
 */
public class TextUtil
{
  public static float getStringWidth(String text, String fontName, int fontSize)
  {
    Font font = new Font(fontName, fontSize);
    return Toolkit.getToolkit().getFontLoader().getFontMetrics(font).computeStringWidth(text);
  }

  public static float getStringHeight(String fontName, int fontSize)
  {
    Font font = new Font(fontName, fontSize);
    return Toolkit.getToolkit().getFontLoader().getFontMetrics(font).getLineHeight();
  }

  public static int getFontSizeForRoad(String fontName, int roadWidth)
  {
    int fontSize = 0;

    double width = GeometryUtil.getDistanceBetweenPoints(new XYPoint(0, 0), new XYPoint(0, roadWidth));

    while (getStringHeight(fontName, fontSize) < width)
    {
      fontSize++;
    }

    if (fontSize < 12)
      return 12;
    else if (fontSize > 24)
      return 24;
    else
      return --fontSize;
  }
}
