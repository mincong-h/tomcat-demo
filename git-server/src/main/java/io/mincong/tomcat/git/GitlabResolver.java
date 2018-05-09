package io.mincong.tomcat.git;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
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
 * GitLab resolver resolves HTTP request against a GitLab server backend. It checks project
 * existence in GitLab server: if exists, it clones and returns the repository from GitLab,
 * else it raise an exception.
 *
 * <p>The Git bare repositories, cloned from GitLab, are stored in the following directory:
 *
 * <pre>
 * /Users/mincong/Desktop/server/${name}.git
 * </pre>
 *
 * <p>GitLab server is running on <tt>http://localhost:80</tt>
 *
 * @author Mincong Huang
 */
public class GitlabResolver implements RepositoryResolver<HttpServletRequest> {

  private static final Logger LOGGER = Logger.getLogger(GitlabResolver.class.getName());

  private Map<String, Repository> repositoryMap = new HashMap<>();

  @Override
  public Repository open(HttpServletRequest req, String name) throws RepositoryNotFoundException {
    // Debug
    String msg = "(req, name)=(\"" + req.getPathInfo() + "\",\"" + name + "\")";
    LOGGER.info(msg);

    if (!hasRepository(req.getPathInfo())) {
      LOGGER.severe("Wrong path: " + req.getPathInfo());
      throw new RepositoryNotFoundException("Failed to find repo");
    }
    Repository repo;
    if (repositoryMap.containsKey(name)) {
      repo = repositoryMap.get(name);
    } else {
      try {
        repo = clone(name);
      } catch (GitAPIException e) {
        LOGGER.severe(e.getMessage());
        throw new RepositoryNotFoundException("Failed to find repo", e);
      }
      repositoryMap.put(name, repo);
    }
    return repo;
  }

  private Repository clone(String name) throws GitAPIException {
    CloneCommand command =
        Git.cloneRepository()
            .setBare(true)
            .setRemote("origin")
            .setURI(GitLab.getCloneUrl(name))
            .setDirectory(R.getRepositoryDir(name));
    return command.call().getRepository();
  }

  /**
   * Whether the project is present in GitLab
   *
   * @param name name of the repository with `.git` suffix
   */
  private boolean hasRepository(String name) {
    // Basic auth
    byte[] bytes = R.FAKE_CREDENTIALS.getBytes(StandardCharsets.UTF_8);
    String credentials = new String(Base64.getEncoder().encode(bytes), StandardCharsets.UTF_8);

    // API
    if (!name.endsWith(".git")) {
      return false;
    }
    String projectId = name.substring(name.length() - 4);
    Response r =
        ClientBuilder.newClient()
            .target(GitLab.REST_API)
            .path("projects")
            .path(projectId)
            .request()
            .header(HttpHeaders.AUTHORIZATION, credentials)
            .get();

    String content = r.readEntity(String.class);
    LOGGER.info(content);
    return r.getStatusInfo() == Status.OK;
  }
}
