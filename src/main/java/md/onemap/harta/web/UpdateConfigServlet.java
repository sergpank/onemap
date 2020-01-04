package md.onemap.harta.web;

import md.onemap.harta.properties.Props;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UpdateConfigServlet extends HttpServlet
{
  private static final Logger LOG = LoggerFactory.getLogger(UpdateConfigServlet.class);

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
  {
    LOG.info("Updating application properties ...");
    Props.update();
  }
}
