package io.mincong.tomcat.git;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.http.HttpHeaders;

import static java.nio.charset.StandardCharsets.UTF_8;

/** @author Mincong Huang */
public class AuthFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    String auth = req.getHeader(HttpHeaders.AUTHORIZATION);
    if (auth != null && auth.startsWith("Basic ")) {
      byte[] encoded = auth.substring("Basic ".length()).getBytes(UTF_8);
      byte[] decoded = Base64.getDecoder().decode(encoded);
      String value = new String(decoded, UTF_8);
      int pos = value.indexOf(':');
      if (pos > 0) {
        String username = value.substring(0, pos);
        String password = value.substring(pos + 1);
        GenericPrincipal principal =
            new GenericPrincipal(username, password, Arrays.asList("A", "B"));
//        request.set
      }
    }
  }

  @Override
  public void destroy() {}
}
