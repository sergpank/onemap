package md.onemap.experiments;

import md.onemap.harta.tile.Palette;

import javax.swing.*;
import java.awt.*;
import java.awt.font.GlyphVector;

/** A demonstration of Java2D transformations */
public class TextBySymbolsPaint extends JPanel {
  static final int WIDTH = 600, HEIGHT = 600; // Size of our example

  public String getName() {
    return "md.onemap.experiments.TextBySymbolsPaint";
  }

  public int getWidth() {
    return WIDTH;
  }

  public int getHeight() {
    return HEIGHT;
  }

  /** Draw the example */
  public void paint(Graphics g1) {
    Graphics2D g = (Graphics2D) g1;

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    g.setPaint(Palette.BACKGROUND_COLOR);
    g.fillRect(0, 0, WIDTH, HEIGHT);

    g.setStroke(new BasicStroke(1.0f));

    drawTiltString(g);
//    drawTestStrings(g);
  }

  private void drawTiltString(Graphics2D g)
  {
    String s = "str-la Hanul Morii 14";
    Font font = new Font(Font.MONOSPACED, Font.PLAIN, 15);
    FontMetrics fontMetrics = g.getFontMetrics(font);
    int height = fontMetrics.getHeight();
    int width = fontMetrics.stringWidth(s);

    int xShift = (WIDTH - width) / 2;
    int yShift = (HEIGHT - height) / 2;

    System.out.printf("%d; %d\n", xShift, yShift);

    g.setPaint(Color.BLACK);
    g.rotate(Math.PI / 180 * 45, WIDTH / 2, HEIGHT / 2);
    g.translate(xShift, yShift);
    GlyphVector glyphVector = font.createGlyphVector(g.getFontRenderContext(), s);

    for (int i = 0; i < glyphVector.getNumGlyphs(); i++)
    {
      Shape outline = glyphVector.getGlyphOutline(i);
      g.fill(outline);
    }
  }

  private void drawTestStrings(Graphics2D g)
  {
    g.translate(10, 25);

    String[] fonts = new String[] {Palette.FONT_NAME, Font.DIALOG_INPUT, Font.MONOSPACED, Font.SANS_SERIF, Font.SERIF};

    for (int fontType = 0; fontType < 5; fontType++)
    {
      for (int fontSize = 17; fontSize > 9; fontSize--)
      {
        Font font = new Font(fonts[fontType], Font.PLAIN, fontSize); // a basic font
        GlyphVector gv = font.createGlyphVector(g.getFontRenderContext(), fontSize + " - " + fonts[fontType] + " : " + allChars());
        for (int i = 0; i < gv.getNumGlyphs(); i++)
        {
//          g.translate(-1, 0);
          Shape glyph = gv.getGlyphOutline(i);
          g.setPaint(Color.BLACK);
          g.fill(glyph); // Fill the shape
//          g.setPaint(Color.WHITE);
//          g.draw(glyph); // And draw the outline
        }
//        g.translate(gv.getNumGlyphs() * 1, 0);
        g.translate(0, 22);
      }
    }
  }

  public String allChars()
  {
    StringBuilder sb = new StringBuilder();
    for (char a = 'a', A = 'A'; a <= 'z'; a++, A++)
      sb.append(A).append(a);

    return sb.toString();
  }

  public static void main(String[] a) {
    JFrame f = new JFrame();
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setContentPane(new TextBySymbolsPaint());
    f.setSize(WIDTH, HEIGHT);
    f.setVisible(true);
  }
}