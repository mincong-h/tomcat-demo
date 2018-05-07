package io.mincong.tomcat.git;

import java.io.File;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.resolver.RepositoryResolver;

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

  /** @param name name of the repository. */
  private boolean hasRepository(String name) {
    // TODO Send request to GitLab asking if the target repository name exist.
    return ('/' + R.APP2).equals(name);
  }
}
