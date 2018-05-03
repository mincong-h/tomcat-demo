package io.mincong.tomcat;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

@WebFilter("/")
public class MyFilter implements Filter {

  private static final Logger LOGGER = Logger.getLogger(MyFilter.class.getName());

  @Override
  public void init(FilterConfig filterConfig) {
    LOGGER.info("Initialized.");
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    LOGGER.info("Before...");
    chain.doFilter(request, response);
    LOGGER.info("After...");
  }

  @Override
  public void destroy() {
    LOGGER.info("Destroyed.");
  }
}
