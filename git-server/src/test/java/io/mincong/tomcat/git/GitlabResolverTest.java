package io.mincong.tomcat.git;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests GitlabResolver
 *
 * @author Mincong Huang
 */
public class GitlabResolverTest {

  @Test
  public void hasRepository() {
    GitlabResolver resolver = new GitlabResolver();
    assertTrue(resolver.hasRepository("app1.git"));
  }
}
