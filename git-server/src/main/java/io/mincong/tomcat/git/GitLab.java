package io.mincong.tomcat.git;

public final class GitLab {

  private static final String CLONE_URL = "http://loaclhost/root/";

  public static final String REST_API = "http://localhost/api/v3/";

  private GitLab() {
    // Utility class, do not instantiate
  }

  /**
   * Gets the GitLab URL for cloning the target repository.
   *
   * @param name name of the target repository, ends without `.git` suffix
   * @return the GitLab URL
   */
  public static String getCloneUrl(String name) {
    return CLONE_URL + name + ".git";
  }
}
