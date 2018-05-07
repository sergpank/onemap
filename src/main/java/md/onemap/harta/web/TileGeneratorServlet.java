package md.onemap.harta.web;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.projector.MercatorProjector;
import md.onemap.harta.properties.Props;
import md.onemap.harta.tile.TileBoundsCalculator;
import md.onemap.harta.tile.TileGenerator;
import md.onemap.harta.tile.TileGeneratorGIS;
import md.onemap.harta.util.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by sergpank on 16/01/2018.
 */
public class TileGeneratorServlet extends HttpServlet
{
  private static final Logger LOG = LoggerFactory.getLogger(TileGeneratorServlet.class);

  private TileGenerator tileGenerator = new TileGeneratorGIS();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    try
    {
      response.setContentType("image/png");

      String xParam = request.getParameter("x");
      String yParam = request.getParameter("y");
      String zParam = request.getParameter("z");

      LOG.info("Requested tile zxy: {} - {}:{}", zParam, xParam, yParam);

      if (xParam == null || yParam == null || zParam == null)
      {
        return;
      }

      int x = Integer.parseInt(xParam);
      int y = Integer.parseInt(yParam);
      int z = Integer.parseInt(zParam);

      MercatorProjector projector = new MercatorProjector(z);
      TileBoundsCalculator boundsCalculator = new TileBoundsCalculator(Props.tileSize(), projector);
      BoundsLatLon tileBounds = boundsCalculator.getTileBounds(x, y, 0);

      Stopwatch.start();
      BufferedImage tile = tileGenerator.generateTileCached(x, y, z, projector, tileBounds);
      Stopwatch.stop();

      LOG.info("Tile {} generation: {}", z + "-" + x + ":" + y, Stopwatch.pretty());

      ImageIO.write(tile, "PNG", response.getOutputStream());
    }
    catch (Throwable t)
    {
      String collect = ((Set<Map.Entry>) request.getParameterMap().entrySet())
          .stream()
          .map(m -> m.getKey() + "::" + Arrays.toString((String[]) m.getValue()))
          .collect(Collectors.joining("; "));
      StringWriter sw = new StringWriter();
      t.printStackTrace(new PrintWriter(sw));
      LOG.error("\"{}\" - \"{}\"", t.getClass().getName(), t.getMessage());
      LOG.error("Request parameters: {}", collect);
      LOG.error(sw.toString());
    }
  }
}
