package md.harta.projector;

import org.junit.Assert;
import org.junit.Test;

public class SimpleProjectorTest {

  @Test
  public void testGetXYPoint_0_0() throws Exception {
    SimpleProjector projector = new SimpleProjector(1);
    Point xyPoint = projector.getXY(0, 0);
    Assert.assertEquals(new Point(180, 90), xyPoint);
  }

  @Test
  public void testGetXYPoint_10_10() throws Exception {
    SimpleProjector projector = new SimpleProjector(1);
    Point xyPoint = projector.getXY(10, 10);
    Assert.assertEquals(new Point(190, 80), xyPoint);
  }

  @Test
  public void testGetXYPoint_m10_10() throws Exception {
    SimpleProjector projector = new SimpleProjector(1);
    Point xyPoint = projector.getXY(-10, 10);
    Assert.assertEquals(new Point(190, 100), xyPoint);
  }

  @Test
  public void testGetXYPoint_m10_m10() throws Exception {
    SimpleProjector projector = new SimpleProjector(1);
    Point xyPoint = projector.getXY(-10, -10);
    Assert.assertEquals(new Point(170, 100), xyPoint);
  }

  @Test
  public void testGetXYPoint_10_m10() throws Exception {
    SimpleProjector projector = new SimpleProjector(1);
    Point xyPoint = projector.getXY(10, -10);
    Assert.assertEquals(new Point(170, 80), xyPoint);
  }

  @Test
   public void testGetXYPoint_90_180(){
    SimpleProjector projector = new SimpleProjector(2);
    Point xyPoint = projector.getXY(90, 180);
    Assert.assertEquals(new Point(720, 0), xyPoint);
  }

  @Test
  public void testGetXYPoint_m90_180(){
    SimpleProjector projector = new SimpleProjector(2);
    Point xyPoint = projector.getXY(-90, 180);
    Assert.assertEquals(new Point(720, 360), xyPoint);
  }

  @Test
  public void testGetXYPoint_m90_m180(){
    SimpleProjector projector = new SimpleProjector(2);
    Point xyPoint = projector.getXY(-90, -180);
    Assert.assertEquals(new Point(0, 360), xyPoint);
  }

  @Test
  public void testGetLonLat_0_0() throws Exception {
    SimpleProjector projector = new SimpleProjector(1);
    Point xyPoint = projector.getLonLat(180, 90);
    Assert.assertEquals(new Point(0, 0), xyPoint);
  }

  @Test
  public void testGetLonLat_10_10() throws Exception {
    SimpleProjector projector = new SimpleProjector(1);
    Point xyPoint = projector.getLonLat(190, 80);
    Assert.assertEquals(new Point(10, 10), xyPoint);
  }

  @Test
  public void testGetLonLat_m10_10() throws Exception {
    SimpleProjector projector = new SimpleProjector(1);
    Point xyPoint = projector.getLonLat(190, 100);
    Assert.assertEquals(new Point(-10, 10), xyPoint);
  }

  @Test
  public void testGetLonLat_m10_m10() throws Exception {
    SimpleProjector projector = new SimpleProjector(1);
    Point xyPoint = projector.getLonLat(170, 100);
    Assert.assertEquals(new Point(-10, -10), xyPoint);
  }

  @Test
  public void testGetLonLat_10_m10() throws Exception {
    SimpleProjector projector = new SimpleProjector(1);
    Point xyPoint = projector.getLonLat(170, 80);
    Assert.assertEquals(new Point(10, -10), xyPoint);
  }

  @Test
  public void testGetLonLat_90_180(){
    SimpleProjector projector = new SimpleProjector(2);
    Point xyPoint = projector.getLonLat(720, 0);
    Assert.assertEquals(new Point(90, 180), xyPoint);
  }

  @Test
  public void testGetLonLat_m90_180(){
    SimpleProjector projector = new SimpleProjector(2);
    Point xyPoint = projector.getLonLat(720, 360);
    Assert.assertEquals(new Point(-90, 180), xyPoint);
  }

  @Test
  public void testGetLonLat_m90_m180(){
    SimpleProjector projector = new SimpleProjector(2);
    Point xyPoint = projector.getLonLat(0, 360);
    Assert.assertEquals(new Point(-90, -180), xyPoint);
  }

  @Test
  public void testWidth180Height90(){
    SimpleProjector projector = new SimpleProjector(0.5);
    Assert.assertEquals(180, projector.getWidth(), 0.0);
    Assert.assertEquals(90, projector.getHeight(), 0.0);
  }

  @Test
  public void testWidth360Height180(){
    SimpleProjector projector = new SimpleProjector(1);
    Assert.assertEquals(360, projector.getWidth(), 0.0);
    Assert.assertEquals(180, projector.getHeight(), 0.0);
  }

  @Test
  public void testWidth720Height360(){
    SimpleProjector projector = new SimpleProjector(2);
    Assert.assertEquals(720, projector.getWidth(), 0.0);
    Assert.assertEquals(360, projector.getHeight(), 0.0);
  }
}