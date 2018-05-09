package io.mincong.tomcat.git;

import java.io.File;

public final class R {

  /**
   * Repositories path is the root path in which all the Git repositories of the git-server are
   * stored.
   */
  public static final String REPOSITORIES_PATH = "/Users/mincong/Desktop/server/";

  private R() {
    // Utility class, do not instantiate
  }

  /**
   * Gets the repository directory path in which the target repository is stored.
   *
   * @param name name of the repository with `.git` suffix
   * @return the repository directory
   */
  public static File getRepositoryDir(String name) {
    return new File(R.REPOSITORIES_PATH, name);
  }
}
