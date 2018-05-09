package io.mincong.tomcat.git;

import org.eclipse.jgit.lib.Repository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

/**
 * Tests GitlabResolver
 *
 * @author Mincong Huang
 */
public class GitlabResolverTest {

  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  @Test
  public void hasRepository() {
    GitlabResolver resolver = new GitlabResolver();
    assertTrue(resolver.hasRepository("app1.git"));
  }

  @Test
  public void cloneRepository() {
    GitlabResolver resolver = new GitlabResolver();
    resolver.setBaseDir(tempFolder.getRoot());
    try (Repository repository = resolver.clone("app1.git")) {
      // Do nothing
    }
  }
}
