package md.onemap.harta.geometry;

import md.onemap.harta.db.gis.entity.Node;

import java.util.List;

/**
 * Created by sergpank on 09.05.15.
 */
public class Label
{
  private String text;
  private BoundsXY bounds;
  private XYPoint center;
  private float height;
  private float width;
  private List<Node> nodes;

  public Label(String text, XYPoint center, float height, float width, List<Node> nodes)
  {
    this.text = text;
    this.center = center;

    this.height = height;
    this.width = width; // +text.length() because distance between characters should be 1 pixel.
    this.bounds = new BoundsXY(center.getX() - width / 2, center.getY() - height / 2,
        center.getX() + width / 2, center.getY() + height / 2);

    this.nodes = nodes;
  }

  /**
   * @param height - should be negative
   */
  public void liftUp(double height)
  {
    bounds.liftUp(height);
    center.liftUp(height);
  }

  public float getHeight()
  {
    return height;
  }

  public float getWidth()
  {
    return width;
  }

  public String getText()
  {
    return text;
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
    return !(center != null ? !center.equals(label.center) : label.center != null);
  }

  @Override
  public int hashCode()
  {
    int result = text != null ? text.hashCode() : 0;
    result = 31 * result + (center != null ? center.hashCode() : 0);
    return result;
  }

  public List<Node> getNodes()
  {
    return nodes;
  }
}