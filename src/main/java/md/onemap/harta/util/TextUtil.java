package md.onemap.harta.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/**
 * Created by sergpank on 16.08.15.
 */
public class TextUtil {

  public static final int FONT_STYLE = Font.PLAIN;

  public static float getStringWidth(String text, String fontName, int fontSize, Graphics2D graphics) {
    Font font = new Font(fontName, Font.PLAIN, fontSize);
    return graphics.getFontMetrics(font).stringWidth(text) + text.length();
  }

  public static float getCharWidth(char c, String fontName, int fontSize, Graphics2D graphics) {
    Font font = new Font(fontName, Font.PLAIN, fontSize);
    return graphics.getFontMetrics(font).charWidth(c);
  }

  public static float getStringHeight(String fontName, int fontSize, Graphics2D graphics) {
    Font font = new Font(fontName, FONT_STYLE, fontSize);
    FontMetrics fontMetrics = graphics.getFontMetrics(font);
    return fontMetrics.getHeight() - fontMetrics.getAscent() + fontMetrics.getDescent();
  }
}
