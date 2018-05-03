package io.mincong.tomcat;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/")
public class MyServlet extends HttpServlet {

  private static final Logger LOGGER = Logger.getLogger(MyServlet.class.getName());

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    String uri = getUri(request);
    LOGGER.info(uri);
    response.setContentType("text/html;charset=UTF-8");
    response.setCharacterEncoding("UTF-8");

    try {
      response.getWriter().write("{\"status\":" + HttpServletResponse.SC_OK + "}");
      response.setStatus(HttpServletResponse.SC_OK);
    } catch (IOException e) {
      LOGGER.severe(e.getMessage());
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  private String getUri(HttpServletRequest request) {
    return request.getScheme() + "://" +
        request.getServerName() +
        ("http".equals(request.getScheme()) && request.getServerPort() == 80
            || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? ""
            : ":" + request.getServerPort()) +
        request.getRequestURI() +
        (request.getQueryString() != null ? "?" + request.getQueryString() : "");
  }
}
