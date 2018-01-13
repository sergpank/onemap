package md.onemap.harta.projector;

import md.onemap.harta.geometry.LatLonPoint;
import md.onemap.harta.geometry.XYPoint;
import org.junit.Assert;
import org.junit.Test;

public class CylindricalProjectorTest {

  @Test
  public void testGetXYPoint_0_0() throws Exception {
    CylindricalProjector projector = new CylindricalProjector(1, 85);
    XYPoint xyPoint = projector.getXY(0, 0);
    Assert.assertEquals(180.0, xyPoint.getX(), 0.0001);
    Assert.assertEquals(654.8937, xyPoint.getY(), 0.0001);
  }

  @Test
  public void testGetXYPoint_10_10() throws Exception {
    CylindricalProjector projector = new CylindricalProjector(1, 85);
    XYPoint xyPoint = projector.getXY(10, 10);
    Assert.assertEquals(190.0, xyPoint.getX(), 0.0001);
    Assert.assertEquals(644.7909, xyPoint.getY(), 0.0001);
  }

  @Test
  public void testGetXYPoint_m10_10() throws Exception {
    CylindricalProjector projector = new CylindricalProjector(1, 85);
    XYPoint xyPoint = projector.getXY(-10, 10);
    Assert.assertEquals(190.0, xyPoint.getX(), 0.0001);
    Assert.assertEquals(664.9965, xyPoint.getY(), 0.0001);
  }

  @Test
  public void testGetXYPoint_m10_m10() throws Exception {
    CylindricalProjector projector = new CylindricalProjector(1, 85);
    XYPoint xyPoint = projector.getXY(-10, -10);
    Assert.assertEquals(170.0, xyPoint.getX(), 0.0001);
    Assert.assertEquals(664.9965, xyPoint.getY(), 0.0001);
  }

  @Test
  public void testGetXYPoint_10_m10() throws Exception {
    CylindricalProjector projector = new CylindricalProjector(1, 85);
    XYPoint xyPoint = projector.getXY(10, -10);
    Assert.assertEquals(170.0, xyPoint.getX(), 0.0001);
    Assert.assertEquals(644.7909, xyPoint.getY(), 0.0001);
  }

  @Test
   public void testGetXYPoint_90_180(){
    CylindricalProjector projector = new CylindricalProjector(2, 85);
    XYPoint xyPoint = projector.getXY(90, 180);
    Assert.assertEquals(720, xyPoint.getX(), 0.0001);
    Assert.assertEquals(0, xyPoint.getY(), 0.0001);
  }

  @Test
  public void testGetXYPoint_m90_180(){
    CylindricalProjector projector = new CylindricalProjector(2, 85);
    XYPoint xyPoint = projector.getXY(-90, 180);
    Assert.assertEquals(720, xyPoint.getX(), 0.0001);
    Assert.assertEquals(2619.5750, xyPoint.getY(), 0.0001);
  }

  @Test
  public void testGetXYPoint_m90_m180(){
    CylindricalProjector projector = new CylindricalProjector(2, 85);
    XYPoint xyPoint = projector.getXY(-90, -180);
    Assert.assertEquals(0, xyPoint.getX(), 0.0001);
    Assert.assertEquals(2619.5750, xyPoint.getY(), 0.0001);
  }

  @Test
  public void testGetLonLat_0_0() throws Exception {
    CylindricalProjector projector = new CylindricalProjector(1, 85);
    LatLonPoint point = projector.getLatLon(180, 654.8937);
    Assert.assertEquals(0, point.getLat(), 0.0001);
    Assert.assertEquals(0, point.getLon(), 0.0001);
  }

  @Test
  public void testGetLonLat_10_10() throws Exception {
    CylindricalProjector projector = new CylindricalProjector(1, 85);
    LatLonPoint point = projector.getLatLon(190, 644.7909);
    Assert.assertEquals(10, point.getLat(), 0.0001);
    Assert.assertEquals(10, point.getLon(), 0.0001);
  }

  @Test
  public void testGetLonLat_m10_10() throws Exception {
    CylindricalProjector projector = new CylindricalProjector(1, 85);
    LatLonPoint point = projector.getLatLon(190, 664.9965);
    Assert.assertEquals(-10, point.getLat(), 0.0001);
    Assert.assertEquals(10, point.getLon(), 0.0001);
  }

  @Test
  public void testGetLonLat_m10_m10() throws Exception {
    CylindricalProjector projector = new CylindricalProjector(1, 85);
    LatLonPoint point = projector.getLatLon(170, 664.9965);
    Assert.assertEquals(-10, point.getLat(), 0.0001);
    Assert.assertEquals(-10, point.getLon(), 0.0001);
  }

  @Test
  public void testGetLonLat_10_m10() throws Exception {
    CylindricalProjector projector = new CylindricalProjector(1, 85);
    LatLonPoint point = projector.getLatLon(170, 644.7909);
    Assert.assertEquals(10, point.getLat(), 0.0001);
    Assert.assertEquals(-10, point.getLon(), 0.0001);
  }

  @Test
  public void testScale05(){
    CylindricalProjector projector = new CylindricalProjector(0.5, 85);
    Assert.assertEquals(180, projector.getWidth(), 0.0);
    Assert.assertEquals(654.8937, projector.getHeight(), 0.0001);
  }

  @Test
  public void testScale1(){
    CylindricalProjector projector = new CylindricalProjector(1, 85);
    Assert.assertEquals(360, projector.getWidth(), 0.0);
    Assert.assertEquals(1309.7875, projector.getHeight(), 0.0001);
  }

  @Test
  public void testScale2(){
    CylindricalProjector projector = new CylindricalProjector(2, 85);
    Assert.assertEquals(720, projector.getWidth(), 0.0);
    Assert.assertEquals(2619.5750, projector.getHeight(), 0.0001);
  }
}