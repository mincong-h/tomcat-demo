package com.example.lifecycle;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.catalina.Container;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.catalina.Service;

/**
 * StateCheckListener is an after-start server listener. It checks the state of all {@link
 * Container}, including engine, host, and context.
 *
 * <p>Register this listener to your server component via <tt>server.xml</tt>:
 *
 * <pre>
 * &lt;Server&gt;
 *   ...
 *   &lt;Listener className="com.example.lifecycle.StateCheckListener" /&gt;
 *   ...
 * &lt;/Server&gt;
 * </pre>
 *
 * @author Mincong Huang
 */
@SuppressWarnings("unused")
public class StateCheckListener implements LifecycleListener {

  private static final Logger LOGGER = Logger.getLogger(StateCheckListener.class.getName());

  @Override
  public void lifecycleEvent(LifecycleEvent event) {
    String type = event.getType();
    Lifecycle lifecycle = event.getLifecycle();

    if (lifecycle instanceof Server && type.equals(Lifecycle.AFTER_START_EVENT)) {
      Server server = (Server) lifecycle;

      // Check status of each container
      StringBuilder sb = new StringBuilder("Status:").append(System.lineSeparator());
      for (Service service : server.findServices()) {
        checkState(service.getContainer(), sb, "");
      }
      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.info(sb.toString());
      }
    }
  }

  private void checkState(Container container, StringBuilder sb, String indent) {
    String className = container.getClass().getSimpleName();
    String stateName = container.getStateName();
    sb.append(String.format("%s%s[%s]: %s%n", indent, className, container.getName(), stateName));
    for (Container child : container.findChildren()) {
      checkState(child, sb, indent + "  ");
    }
  }
}
