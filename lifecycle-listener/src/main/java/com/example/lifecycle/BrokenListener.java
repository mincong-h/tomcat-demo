package com.example.lifecycle;

import java.util.logging.Logger;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.StandardContext;

/**
 * A broken listener that makes a context failed during its start event. Use system property
 * <tt>break.context</tt> to define which context you want to break. Example:
 *
 * <pre>
 * -Dbreak.context=demo
 * </pre>
 *
 * will break the web application "demo".
 *
 * <p>Don't forget to register this listener to <tt>context.xml</tt>.
 *
 * @author Mincong Huang
 */
@SuppressWarnings("unused")
public class BrokenListener implements LifecycleListener {

  private static final Logger LOGGER = Logger.getLogger(BrokenListener.class.getName());

  @Override
  public void lifecycleEvent(LifecycleEvent event) {
    Lifecycle lifecycle = event.getLifecycle();
    String type = event.getType();

    if (lifecycle instanceof StandardContext && Lifecycle.START_EVENT.equals(type)) {
      StandardContext context = (StandardContext) lifecycle;
      String expected = System.getProperty("break.context");
      String actual = context.getName();

      if (expected != null && expected.equals(actual)) {
        LOGGER.severe("Fail context '" + actual + "'.");
        throw new IllegalStateException("Context '" + actual + "' is broken.");
      } else {
        LOGGER.info("Skip context '" + actual + "'.");
      }
    }
  }
}
