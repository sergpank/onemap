package md.onemap.harta.web;

import md.onemap.harta.properties.Props;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UpdateConfigServlet extends HttpServlet
{
  private static final Logger LOG = LogManager.getLogger();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
  {
    LOG.info("Updating application properties ...");
    Props.update();
  }
}
