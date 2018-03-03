package com.example.lifecycle;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.catalina.Container;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Server;
import org.apache.catalina.Service;

/**
 * StrictStateCheckListener is an after-start server listener. It checks the state of all {@link
 * Container}, including engine, host, and context. The server start-up will be aborted if there's
 * any component failed to start (state different from STARTED).
 *
 * <p>Register this listener to your server component via <tt>server.xml</tt>:
 *
 * <pre>
 * &lt;Server&gt;
 *   ...
 *   &lt;Listener className="com.example.lifecycle.StrictStateCheckListener" /&gt;
 *   ...
 * &lt;/Server&gt;
 * </pre>
 *
 * @author Mincong Huang
 * @see com.example.lifecycle.StateCheckListener
 */
@SuppressWarnings("unused")
public class StrictStateCheckListener implements LifecycleListener {

  private static final Logger LOGGER = Logger.getLogger(StrictStateCheckListener.class.getName());

  private boolean hasFailures;

  private String containerState;

  @Override
  public void lifecycleEvent(LifecycleEvent event) {
    String type = event.getType();
    Lifecycle lifecycle = event.getLifecycle();

    if (lifecycle instanceof Server && type.equals(Lifecycle.AFTER_START_EVENT)) {
      Server server = (Server) lifecycle;
      hasFailures = false;

      // Check status of each container
      StringBuilder sb = new StringBuilder("Status:").append(System.lineSeparator());
      for (Service service : server.findServices()) {
        checkState(service.getContainer(), sb, "");
      }
      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.info(sb.toString());
      }
      if (hasFailures) {
        /*
         * The server will not be stopped by this listener. Indeed,
         * an exception is raised to trigger shutdown hook. See:
         * `org.apache.catalina.startup.Catalina`.
         */
        throw new IllegalStateException(containerState);
      }
    }
  }

  private void checkState(Container container, StringBuilder report, String indent) {
    if (!hasFailures && container.getState() != LifecycleState.STARTED) {
      hasFailures = true;
      containerState = stateOf(container);
    }
    report.append(indent).append(stateOf(container));
    for (Container child : container.findChildren()) {
      checkState(child, report, indent + "  ");
    }
  }

  private String stateOf(Container container) {
    String className = container.getClass().getSimpleName();
    String stateName = container.getStateName();
    return String.format("%s[%s]: %s%n", className, container.getName(), stateName);
  }
}
