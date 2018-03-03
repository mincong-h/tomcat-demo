package com.example.lifecycle;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;

/**
 * After-start listener listens to "after_start" event of the "sever" lifecycle.
 *
 * <ul>
 *   <li>It stops the server if system property "stop" is present.
 *   <li>It raises an exception if system property "raise" is present.
 * </ul>
 *
 * <p>It is useful for comparing the differences between stopping the server and raising an
 * exception. Actually, raising an exception will break the server startup process, and trigger the
 * shutdown hook indirectly.
 *
 * @author Mincong Huang
 */
@SuppressWarnings("unused")
public class AfterStartListener implements LifecycleListener {

  private static final Logger LOGGER = Logger.getLogger(AfterStartListener.class.getName());

  private static final String STOP = "stop";

  private static final String RAISE = "raise";

  public void lifecycleEvent(LifecycleEvent event) {
    String type = event.getType();
    Lifecycle lifecycle = event.getLifecycle();

    if (lifecycle instanceof Server && Lifecycle.AFTER_START_EVENT.equals(type)) {
      boolean stop = Boolean.parseBoolean(System.getProperty(STOP, "false"));
      boolean raise = Boolean.parseBoolean(System.getProperty(RAISE, "false"));

      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.info("'" + STOP + "'=" + stop);
        LOGGER.info("'" + RAISE + "'=" + raise);
      }

      if (stop) {
        doStop((Server) lifecycle);
      }
      // Seems to be a better approach!
      if (raise) {
        throw new IllegalStateException("Some errors occurred.");
      }
    }
  }

  private void doStop(Server server) {
    LOGGER.warning("Stopping server ('" + STOP + "'=true)");
    try {
      server.stop();
      LOGGER.info("Server stopped.");
    } catch (LifecycleException e) {
      LOGGER.log(Level.SEVERE, "Failed to stop server.", e);
    }
  }
}
