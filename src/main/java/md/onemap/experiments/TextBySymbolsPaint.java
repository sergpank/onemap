package md.onemap.experiments;

import javax.swing.*;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * A demonstration of Java2D transformations
 */
public class TextBySymbolsPaint extends JPanel
{
  static int WIDTH = 1140, HEIGHT = 560; // Size of our example

  public String getName()
  {
    return "md.onemap.experiments.TextBySymbolsPaint";
  }

  /**
   * Draw the example
   */
  public void paint(Graphics g1)
  {
    Graphics2D g = (Graphics2D) g1;

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    g.setPaint(new Color(128, 128, 128));
    g.fillRect(0, 0, this.getWidth(), this.getHeight());

    System.out.println(this.getWidth() + " x " + this.getHeight());

    g.setStroke(new BasicStroke(1.0f));

    drawTiltString(g);
//    drawTestStrings(g);
  }

  private void drawTiltString(Graphics2D g)
  {
    String str = "AaBcCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz1234567890";
    int font_size = 20;
    Font textFont = new Font("Roboto Medium", Font.PLAIN, font_size);

    String[] fonts = new String[]{
        "Roboto Thin",
        "Roboto Light",
        "Roboto",
        "Roboto Medium",
        "Roboto Bold",
        "Roboto Black"
    };

    g.translate(0, 20);
    g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

    for (String fontName : fonts)
    {
      Font font = new Font(fontName, Font.PLAIN, font_size);

      GlyphVector glyphVector = font.createGlyphVector(g.getFontRenderContext(), fontName);

      g.setPaint(Color.WHITE);
      g.draw(glyphVector.getOutline());

      g.setPaint(Color.BLACK);
      g.fill(glyphVector.getOutline());

      g.translate(300, 0);
      glyphVector = font.createGlyphVector(g.getFontRenderContext(), "Roboto - 1234567890");
      g.fill(glyphVector.getOutline());

      g.translate(-300, 20);
    }

    g.translate(0, -fonts.length * 20);

    g.translate(140, -10);
    g.rotate(Math.PI / 4);

    for (int i = 0; i < str.length(); i++)
    {
      String chr = String.valueOf(str.charAt(i));

      if (chr.equals("Q"))
      {
        g.rotate(- Math.PI / 2);
        g.translate(20, 0);
      }

      GlyphVector letterGlyph = textFont.createGlyphVector(g.getFontRenderContext(), chr);
      Shape character = letterGlyph.getGlyphOutline(0);
      double glyphWidth = letterGlyph.getLogicalBounds().getWidth();

      g.setPaint(Color.WHITE);
      g.draw(character);

      g.setPaint(Color.BLACK);
      g.fill(character);

      g.translate(glyphWidth, 0.0);
    }
  }

  private void drawTestStrings(Graphics2D g)
  {
    g.translate(10, 25);
    g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

    String[] fonts = new String[] {"Roboto", "Roboto Medium", "Roboto Bold", "Roboto Black"};

    for (int i = fonts.length - 1; i >= 0; i--)
    {
      g.translate(0, 10);
      for (int fontSize = 25; fontSize > 13; fontSize--)
      {
        String fontName = fonts[i];
        Font font = new Font(fontName, Font.PLAIN, fontSize); // a basic font
        GlyphVector gv = font.createGlyphVector(g.getFontRenderContext(), fontSize + " - " + fontName + " : " + allChars());

        g.setPaint(Color.WHITE);
        g.draw(gv.getOutline());

        g.setPaint(Color.BLACK);
        g.fill(gv.getOutline());

        g.translate(0, fontSize + 1);
      }
    }
  }

  public String allChars()
  {
    StringBuilder sb = new StringBuilder();
    for (char a = 'a', A = 'A'; a <= 'z'; a++, A++)
    {
      sb.append(A).append(a);
    }

    for (int i = 0; i < 10; i++)
    {
      sb.append(i);
    }

    return sb.toString();
  }

  public static void main(String[] a)
  {
    try
    {
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("src/main/resources/fonts/Roboto-Thin.ttf")));
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("src/main/resources/fonts/Roboto-Light.ttf")));
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("src/main/resources/fonts/Roboto-Regular.ttf")));
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("src/main/resources/fonts/Roboto-Medium.ttf")));
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("src/main/resources/fonts/Roboto-Bold.ttf")));
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("src/main/resources/fonts/Roboto-Black.ttf")));

      String[] availableFonts = ge.getAvailableFontFamilyNames();

      Arrays.stream(availableFonts).forEach(System.out::println);
    }
    catch (IOException | FontFormatException e)
    {
      e.printStackTrace();
    }

    JFrame f = new JFrame();
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setContentPane(new TextBySymbolsPaint());
    f.setSize(WIDTH, HEIGHT);
    f.setVisible(true);
  }
}