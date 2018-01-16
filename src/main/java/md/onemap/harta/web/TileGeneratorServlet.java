package md.onemap.harta.web;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.projector.MercatorProjector;
import md.onemap.harta.tile.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by sergpank on 16/01/2018.
 */
public class TileGeneratorServlet extends HttpServlet
{
  private static final Logger LOG = LoggerFactory.getLogger(TileGeneratorServlet.class);

  private GeneratorProperties props = new GeneratorProperties("properties/db-generator.properties");
  private TileGenerator tileGenerator = new TileGeneratorDB(props);

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    try
    {
      response.setContentType("image/png");

      String xParam = request.getParameter("x");
      String yParam = request.getParameter("y");
      String zParam = request.getParameter("z");

      if (xParam == null || yParam == null || zParam == null)
      {
        return;
      }

      int x = Integer.parseInt(xParam);
      int y = Integer.parseInt(yParam);
      int z = Integer.parseInt(zParam);

      MercatorProjector projector = new MercatorProjector(z);
      TileBoundsCalculator boundsCalculator = new TileBoundsCalculator(props.tileSize(), projector);
      BoundsLatLon tileBounds = boundsCalculator.getTileBounds(x, y, 0);

      BufferedImage tile = tileGenerator.generateTile(x, y, z, projector, tileBounds);
      ImageIO.write(tile, "PNG", response.getOutputStream());
    }
    catch (Throwable t)
    {
     LOG.error(t.getMessage());
    }
  }
}
