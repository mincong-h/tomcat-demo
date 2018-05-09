package io.mincong.tomcat.git;

import org.junit.Test;

import static org.junit.Assert.*;

public class GitLabTest {

  @Test
  public void getProjectId() {
    assertEquals(GitLab.USER + "%2Fapp", GitLab.getProjectId("app.git"));
  }
}
