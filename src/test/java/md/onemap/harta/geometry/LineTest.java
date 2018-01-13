package md.onemap.harta.geometry;

import org.junit.Assert;
import org.junit.Test;

public class LineTest {

  @Test
  public void testSlope45(){
    Line line = new Line(1, -1, 0);
    Assert.assertEquals(Math.PI / 4, line.getSlope(), 1e-8);
  }

  @Test
  public void testSlopeM45(){
    Line line = new Line(1, 1, 0);
    Assert.assertEquals(Math.PI / -4, line.getSlope(), 1e-8);
  }

  @Test
  public void testSlope90(){
    Line line = new Line(1, 0, 0);
    Assert.assertEquals(Math.PI / 2, line.getSlope(), 1e-8);
  }

  @Test
  public void testGetX(){
    Line line = new Line(1, -1, 0);
    Assert.assertEquals(2, line.getX(2), 1e-8);
    Assert.assertEquals(3, line.getX(3), 1e-8);
    Assert.assertEquals(-3, line.getX(-3), 1e-8);
  }

  @Test
  public void testGetY(){
    Line line = new Line(1, -1, 0);
    Assert.assertEquals(2, line.getY(2), 1e-8);
    Assert.assertEquals(3, line.getY(3), 1e-8);
    Assert.assertEquals(-3, line.getY(-3), 1e-8);
  }
}