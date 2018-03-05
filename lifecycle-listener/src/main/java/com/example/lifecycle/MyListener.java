package com.example.lifecycle;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.catalina.Container;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;

/**
 * A simple listener listening to all the events.
 *
 * <ul>
 *   <li>Register it to server component (<tt>server.xml</tt>) to listen server events
 *   <li>Register it to context component (<tt>context.xml</tt>) to listen context events
 * </ul>
 *
 * <p>When registering to server component, be aware of the scope: it varies depending on which
 * element the listener is nested. It can be Server, Service, Engine, or Host.
 *
 * @author Mincong Huang
 */
@SuppressWarnings("unused")
public class MyListener implements LifecycleListener {

  private static final Logger LOGGER = Logger.getLogger(MyListener.class.getName());

  public void lifecycleEvent(LifecycleEvent event) {
    Lifecycle lifecycle = event.getLifecycle();
    String name = lifecycle.getClass().getSimpleName();

    if (lifecycle instanceof Container) {
      Container container = (Container) lifecycle;
      name += '[' + container.getName() + ']';
    }

    if (LOGGER.isLoggable(Level.INFO)) {
      LOGGER.info(name + ':' + event.getType());
    }
  }
}
