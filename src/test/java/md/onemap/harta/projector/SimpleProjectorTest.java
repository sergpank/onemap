package md.onemap.harta.projector;

import md.onemap.harta.geometry.LatLonPoint;
import md.onemap.harta.geometry.XYPoint;
import org.junit.Assert;
import org.junit.Test;

public class SimpleProjectorTest {

  @Test
  public void testGetXYPoint_0_0() throws Exception {
    SimpleProjector projector = new SimpleProjector(1);
    XYPoint xyPoint = projector.getXY(0, 0);
    Assert.assertEquals(new XYPoint(180, 90), xyPoint);
  }

  @Test
  public void testGetXYPoint_10_10() throws Exception {
    SimpleProjector projector = new SimpleProjector(1);
    XYPoint xyPoint = projector.getXY(10, 10);
    Assert.assertEquals(new XYPoint(190, 80), xyPoint);
  }

  @Test
  public void testGetXYPoint_m10_10() throws Exception {
    SimpleProjector projector = new SimpleProjector(1);
    XYPoint xyPoint = projector.getXY(-10, 10);
    Assert.assertEquals(new XYPoint(190, 100), xyPoint);
  }

  @Test
  public void testGetXYPoint_m10_m10() throws Exception {
    SimpleProjector projector = new SimpleProjector(1);
    XYPoint xyPoint = projector.getXY(-10, -10);
    Assert.assertEquals(new XYPoint(170, 100), xyPoint);
  }

  @Test
  public void testGetXYPoint_10_m10() throws Exception {
    SimpleProjector projector = new SimpleProjector(1);
    XYPoint xyPoint = projector.getXY(10, -10);
    Assert.assertEquals(new XYPoint(170, 80), xyPoint);
  }

  @Test
   public void testGetXYPoint_90_180(){
    SimpleProjector projector = new SimpleProjector(2);
    XYPoint xyPoint = projector.getXY(90, 180);
    Assert.assertEquals(new XYPoint(720, 0), xyPoint);
  }

  @Test
  public void testGetXYPoint_m90_180(){
    SimpleProjector projector = new SimpleProjector(2);
    XYPoint xyPoint = projector.getXY(-90, 180);
    Assert.assertEquals(new XYPoint(720, 360), xyPoint);
  }

  @Test
  public void testGetXYPoint_m90_m180(){
    SimpleProjector projector = new SimpleProjector(2);
    XYPoint xyPoint = projector.getXY(-90, -180);
    Assert.assertEquals(new XYPoint(0, 360), xyPoint);
  }

  @Test
  public void testGetLonLat_0_0() throws Exception {
    SimpleProjector projector = new SimpleProjector(1);
    LatLonPoint point = projector.getLatLon(180, 90);
    Assert.assertEquals(new LatLonPoint(0, 0), point);
  }

  @Test
  public void testGetLonLat_10_10() throws Exception {
    SimpleProjector projector = new SimpleProjector(1);
    LatLonPoint point = projector.getLatLon(190, 80);
    Assert.assertEquals(new LatLonPoint(10, 10), point);
  }

  @Test
  public void testGetLonLat_m10_10() throws Exception {
    SimpleProjector projector = new SimpleProjector(1);
    LatLonPoint point = projector.getLatLon(190, 100);
    Assert.assertEquals(new LatLonPoint(-10, 10), point);
  }

  @Test
  public void testGetLonLat_m10_m10() throws Exception {
    SimpleProjector projector = new SimpleProjector(1);
    LatLonPoint point = projector.getLatLon(170, 100);
    Assert.assertEquals(new LatLonPoint(-10, -10), point);
  }

  @Test
  public void testGetLonLat_10_m10() throws Exception {
    SimpleProjector projector = new SimpleProjector(1);
    LatLonPoint point = projector.getLatLon(170, 80);
    Assert.assertEquals(new LatLonPoint(10, -10), point);
  }

  @Test
  public void testGetLonLat_90_180(){
    SimpleProjector projector = new SimpleProjector(2);
    LatLonPoint point = projector.getLatLon(720, 0);
    Assert.assertEquals(new LatLonPoint(90, 180), point);
  }

  @Test
  public void testGetLonLat_m90_180(){
    SimpleProjector projector = new SimpleProjector(2);
    LatLonPoint point = projector.getLatLon(720, 360);
    Assert.assertEquals(new LatLonPoint(-90, 180), point);
  }

  @Test
  public void testGetLonLat_m90_m180(){
    SimpleProjector projector = new SimpleProjector(2);
    LatLonPoint point = projector.getLatLon(0, 360);
    Assert.assertEquals(new LatLonPoint(-90, -180), point);
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