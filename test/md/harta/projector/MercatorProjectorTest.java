package md.harta.projector;

import org.junit.Assert;
import org.junit.Test;

public class MercatorProjectorTest {

  public static final int EARTH_RADIUS = 6371;

  @Test
  public void testGetXYPoint_0_0() throws Exception {
    MercatorProjector projector = new MercatorProjector(EARTH_RADIUS, 85);
    Point xyPoint = projector.getXY(0, 0);
    Assert.assertEquals(20015.0868, xyPoint.getX(), 0.0001);
    Assert.assertEquals(19949.5207, xyPoint.getY(), 0.0001);
  }

  @Test
  public void testGetXYPoint_10_10() throws Exception {
    MercatorProjector projector = new MercatorProjector(EARTH_RADIUS, 85);
    Point xyPoint = projector.getXY(10, 10);

    Assert.assertEquals(21127.03606, xyPoint.getX(), 0.0001);
    Assert.assertEquals(18831.88282, xyPoint.getY(), 0.0001);
  }

  @Test
  public void testGetXYPoint_m10_10() throws Exception {
    MercatorProjector projector = new MercatorProjector(EARTH_RADIUS, 85);
    Point xyPoint = projector.getXY(-10, 10);

    Assert.assertEquals(21127.03606, xyPoint.getX(), 0.0001);
    Assert.assertEquals(21067.15874, xyPoint.getY(), 0.0001);
  }

  @Test
  public void testGetXYPoint_m10_m10() throws Exception {
    MercatorProjector projector = new MercatorProjector(EARTH_RADIUS, 85);
    Point xyPoint = projector.getXY(-10, -10);

    Assert.assertEquals(18903.13753, xyPoint.getX(), 0.0001);
    Assert.assertEquals(21067.15874, xyPoint.getY(), 0.0001);
  }


  @Test
  public void testGetXYPoint_10_m10() throws Exception {
    MercatorProjector projector = new MercatorProjector(EARTH_RADIUS, 85);
    Point xyPoint = projector.getXY(10, -10);

    Assert.assertEquals(18903.13753, xyPoint.getX(), 0.0001);
    Assert.assertEquals(18831.88282, xyPoint.getY(), 0.0001);
  }

  @Test
  public void testGetLonLat_0_0() throws Exception {
    MercatorProjector projector = new MercatorProjector(EARTH_RADIUS, 85);
    Point xyPoint = projector.getLonLat(20015.0868, 19949.5207);
    Assert.assertEquals(0, xyPoint.getY(), 0.0001);
    Assert.assertEquals(0, xyPoint.getX(), 0.0001);
  }

  @Test
  public void testGetLonLat_10_10() throws Exception {
    MercatorProjector projector = new MercatorProjector(EARTH_RADIUS, 85);
    Point xyPoint = projector.getLonLat(21127.03606, 18831.88282);

    Assert.assertEquals(10, xyPoint.getY(), 0.0001);
    Assert.assertEquals(10, xyPoint.getX(), 0.0001);
  }

  @Test
  public void testLonLat_m10_10() throws Exception {
    MercatorProjector projector = new MercatorProjector(EARTH_RADIUS, 85);
    Point xyPoint = projector.getLonLat(21127.03606, 21067.15874);

    Assert.assertEquals(-10, xyPoint.getX(), 0.0001);
    Assert.assertEquals(10, xyPoint.getY(), 0.0001);
  }

  @Test
  public void testGetLonLat_m10_m10() throws Exception {
    MercatorProjector projector = new MercatorProjector(EARTH_RADIUS, 85);
    Point xyPoint = projector.getLonLat(18903.13753, 21067.15874);

    Assert.assertEquals(-10, xyPoint.getY(), 0.0001);
    Assert.assertEquals(-10, xyPoint.getX(), 0.0001);
  }

  @Test
  public void testGetLonLat_10_m10() throws Exception {
    MercatorProjector projector = new MercatorProjector(EARTH_RADIUS, 85);
    Point xyPoint = projector.getLonLat(18903.13753, 18831.88282);

    Assert.assertEquals(10, xyPoint.getX(), 0.0001);
    Assert.assertEquals(-10, xyPoint.getY(), 0.0001);
  }

  @Test
  public void testWidthRadius100(){
    MercatorProjector projector = new MercatorProjector(100, 85);
    Assert.assertEquals(628.3185307, projector.getWidth(), 0.0001);
    Assert.assertEquals(626.2602663, projector.getHeight(), 0.0001);
  }

  @Test
  public void testWidthRadius200(){
    MercatorProjector projector = new MercatorProjector(200, 85);
    Assert.assertEquals(1256.637061, projector.getWidth(), 0.0001);
    Assert.assertEquals(1252.520533, projector.getHeight(), 0.0001);
  }

  @Test
  public void testWidthEarthRadius(){
    MercatorProjector projector = new MercatorProjector(6371, 85);
    Assert.assertEquals(40030.17359, projector.getWidth(), 0.0001);
    Assert.assertEquals(39899.04157, projector.getHeight(), 0.0001);
  }
}