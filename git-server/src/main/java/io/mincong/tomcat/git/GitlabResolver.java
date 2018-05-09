package io.mincong.tomcat.git;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.transport.resolver.RepositoryResolver;
import org.glassfish.jersey.logging.LoggingFeature;

/**
 * GitLab resolver resolves HTTP request against a GitLab server backend. It checks project
 * existence in GitLab server: if exists, it clones and returns the repository from GitLab, else it
 * raise an exception.
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

  private Feature loggingFeature = new LoggingFeature(LOGGER, Level.INFO, null, null);

  private static ConcurrentMap<String, Repository> repositoryMap = new ConcurrentHashMap<>();

  private File baseDir = new File(R.REPOSITORIES_PATH);

  /**
   * {@inheritDoc}
   *
   * @param name name of the repository `.git` suffix, as parsed out of the URL.
   */
  @Override
  public Repository open(HttpServletRequest req, String name) throws RepositoryNotFoundException {
    // Debug
    String msg = "(req, name)=(\"" + req.getPathInfo() + "\",\"" + name + "\")";
    LOGGER.info(msg);

    if (!hasRepository(name)) {
      LOGGER.severe("Wrong path: " + req.getPathInfo());
      throw new RepositoryNotFoundException("Failed to find repo");
    }
    try {
      repositoryMap.computeIfAbsent(name, this::clone);
    } catch (RuntimeGitException e) {
      throw new RepositoryNotFoundException(e.getMessage(), e.getCause());
    }
    return repositoryMap.get(name);
  }

  private static class RuntimeGitException extends RuntimeException {
    RuntimeGitException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  /**
   * Clones the repository from GitLab.
   *
   * @param name name of the repository with `.git` suffix
   */
  Repository clone(String name) {
    String msg = System.currentTimeMillis() + " cloning repository " + name;
    LOGGER.info(msg);
    try {
      CloneCommand command =
          Git.cloneRepository()
              .setBare(true)
              .setRemote("origin")
              .setURI(GitLab.getCloneUrl(name))
              .setDirectory(getRepositoryDir(name))
              .setCredentialsProvider(
                  new UsernamePasswordCredentialsProvider(GitLab.USER, GitLab.PASS));
      return command.call().getRepository();
    } catch (GitAPIException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw new RuntimeGitException(e.getMessage(), e.getCause());
    }
  }

  private File getRepositoryDir(String name) {
    return new File(getBaseDir(), name);
  }

  public File getBaseDir() {
    return baseDir;
  }

  public void setBaseDir(File baseDir) {
    this.baseDir = baseDir;
  }

  /**
   * Whether the project is present in GitLab
   *
   * @param name name of the repository with `.git` suffix
   * @see <a href="https://docs.gitlab.com/ee/api/projects.html">GitLab Projects API</a>
   * @see <a href="https://docs.gitlab.com/ee/api/projects.html#list-user-projects">GitLab Projects
   *     API - List user projects</a>
   */
  boolean hasRepository(String name) {
    // Basic auth
    byte[] bytes = GitLab.CREDENTIALS.getBytes(StandardCharsets.UTF_8);
    String credentials = new String(Base64.getEncoder().encode(bytes), StandardCharsets.UTF_8);

    // API
    if (!name.endsWith(".git")) {
      return false;
    }
    String projectId = GitLab.getProjectId(name);
    Response r =
        ClientBuilder.newClient()
            .target(GitLab.REST_API)
            .path("projects")
            .path(projectId)
            .register(loggingFeature)
            .request()
            .header(HttpHeaders.AUTHORIZATION, credentials)
            .get();

    String content = r.readEntity(String.class);
    LOGGER.info(content);
    return r.getStatus() == Status.OK.getStatusCode();
  }
}
