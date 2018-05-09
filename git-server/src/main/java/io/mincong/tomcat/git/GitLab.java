package io.mincong.tomcat.git;

public final class GitLab {

  public static final String REST_API = "http://localhost/api/v3/";
  public static final String CLONE_URL = "http://loaclhost/root/";
  public static final String USER = "foo";
  public static final String CREDENTIALS = USER + ":password";

  private GitLab() {
    // Utility class, do not instantiate
  }

  /**
   * Gets the GitLab URL for cloning the target repository.
   *
   * @param name name of the repository with `.git` suffix
   * @return the GitLab URL
   */
  public static String getCloneUrl(String name) {
    return CLONE_URL + name;
  }

  /**
   * Gets the GitLab project identifier
   *
   * @param name name of the repository with `.git` suffix
   * @return the HTML escaped project id
   */
  public static String getProjectId(String name) {
    return USER + "%2F" + name.substring(0, name.length() - ".git".length());
  }
}
