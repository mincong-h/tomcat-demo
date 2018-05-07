package io.mincong.tomcat.git;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.resolver.RepositoryResolver;

/**
 * GitLab resolver is a fake resolver. It asks GitLab server <tt>http://localhost:80</tt> if the
 * target project exists: if exists, it returns a fake repository stored in {@link
 * R#FAKE_REPO_PATH}, else it raise an exception.
 */
public class GitlabResolver implements RepositoryResolver<HttpServletRequest> {

  private static final Logger LOGGER = Logger.getLogger(GitlabResolver.class.getName());

  private Repository app2;

  @Override
  public Repository open(HttpServletRequest req, String name) throws RepositoryNotFoundException {
    if (!hasRepository(req.getPathInfo())) {
      LOGGER.severe("Wrong path: " + req.getPathInfo());
      throw new RepositoryNotFoundException("Failed to find repo");
    }

    if (app2 == null) {
      CloneCommand command =
          Git.cloneRepository()
              .setBare(true)
              .setRemote("origin")
              .setURI(R.BASE_PATH + R.APP2)
              .setDirectory(new File(R.DESKTOP_PATH));

      try {
        app2 = command.call().getRepository();
      } catch (GitAPIException e) {
        LOGGER.severe(e.getMessage());
        throw new RepositoryNotFoundException("Failed to find repo", e);
      }
    }
    return app2;
  }

  /**
   * Whether the project is present in GitLab
   *
   * @param name name of the repository with `.git` suffix
   */
  private boolean hasRepository(String name) {
    // Basic auth
    byte[] bytes = "root:localhost".getBytes(StandardCharsets.UTF_8);
    String credentials = new String(Base64.getEncoder().encode(bytes), StandardCharsets.UTF_8);

    // API
    if (!name.endsWith(".git")) {
      return false;
    }
    String projectId = name.substring(name.length() - 4);
    Response r =
        ClientBuilder.newClient()
            .target(R.GITLAB_API)
            .path("projects")
            .path(projectId)
            .request()
            .header(HttpHeaders.AUTHORIZATION, credentials)
            .get();

    LOGGER.info(r.readEntity(String.class));
    return r.getStatusInfo() == Status.OK;
  }
}
