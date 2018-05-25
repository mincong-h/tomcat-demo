package io.mincong.tomcat.git;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jgit.http.server.GitServlet;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.resolver.ReceivePackFactory;

@WebServlet(
  urlPatterns = "/*",
  initParams = {
    /*
     * Parameter `base-path` allows to recursively export all Git
     * repositories within a directory. This directory must not
     * itself be a Git repository.
     */
    @WebInitParam(name = "base-path", value = "/Users/mincong/Desktop/server"),
    /*
     * Parameter `export-all` defines whether export all the
     * directories below. If set to `yes`, `true`, `1`, or `on`,
     * export all. Else, set to `no`, `false`, `0`, or `off`.
     *
     * If set to false, any directory below it which has a file named
     * `git-daemon-export-ok` will be published.
     *
     * See org.eclipse.jgit.util.StringUtils#toBooleanOrNull
     * See org.eclipse.jgit.transport.resolver.FileResolver#FileResolver(File, boolean)
     */
    @WebInitParam(name = "export-all", value = "true")
  }
)
public class GitHttpServlet extends GitServlet {
  public GitHttpServlet() {
    super.setReceivePackFactory(new AnonymousReceivePackFactory());
  }

  private static class AnonymousReceivePackFactory
      implements ReceivePackFactory<HttpServletRequest> {
    @Override
    public ReceivePack create(HttpServletRequest req, Repository db) {
      ReceivePack pack = new ReceivePack(db);
      pack.setRefLogIdent(new PersonIdent("Anonymous", "anonymous@localhost"));
      return pack;
    }
  }
}
