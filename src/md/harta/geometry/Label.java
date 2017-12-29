package md.harta.geometry;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.text.Font;
import md.harta.osm.Highway;

/**
 * Created by sergpank on 09.05.15.
 */
public class Label
{
  private String text;
  private Font font;
  private BoundsXY bounds;
  private XYPoint center;
  private Highway highway;

  public Label(String text, XYPoint center, String fontName, int fontSize)
  {
    this.text = text;
    this.font = new Font(fontName, fontSize);
    this.center = center;
    FontMetrics fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(font);
    float lineHeight = fontMetrics.getLineHeight();
    float lineWidth = fontMetrics.computeStringWidth(text);
    this.bounds = new BoundsXY(center.getX() - lineWidth / 2, center.getY() - lineHeight / 2,
                               center.getX() + lineWidth / 2, center.getY() + lineHeight / 2);
  }

  /**
   * @param height - should be negative
   */
  public void liftUp(double height)
  {
    bounds.liftUp(height);
    center.liftUp(height);
  }

  public String getText()
  {
    return text;
  }

  public Font getFont()
  {
    return font;
  }

  public BoundsXY getBounds()
  {
    return bounds;
  }

  public XYPoint getCenter()
  {
    return center;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (!(o instanceof Label)) return false;

    Label label = (Label) o;

    if (text != null ? !text.equals(label.text) : label.text != null) return false;
    if (font != null ? !font.equals(label.font) : label.font != null) return false;
    if (bounds != null ? !bounds.equals(label.bounds) : label.bounds != null) return false;
    return !(center != null ? !center.equals(label.center) : label.center != null);
  }

  @Override
  public int hashCode()
  {
    int result = text != null ? text.hashCode() : 0;
    result = 31 * result + (font != null ? font.hashCode() : 0);
    result = 31 * result + (bounds != null ? bounds.hashCode() : 0);
    result = 31 * result + (center != null ? center.hashCode() : 0);
    return result;
  }

  public void setHighway(Highway highway)
  {
    this.highway = highway;
  }

  public Highway getHighway()
  {
    return highway;
  }
}