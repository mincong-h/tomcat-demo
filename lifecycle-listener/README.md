# Lifecycle Listener

This modules contains a list of customized [LifecycleListener][LifecycleListener]:

- **AfterStartListener** aborts the server via different ways. (not documented)
- **BrokenListener** aborts a target context. (not documented)
- **MyListener** for basic event capturing. Useful for understanding Tomcat's lifecycle.
- **StateCheckListener** for checking all event states and the relationship of
  engine, host, and context.
- **StrictStateCheckListener** aborts Tomcat startup when any context fails to start.

## Definition

If you have implemented a Java object that needs to know when this **Context**
is started or stopped, you can declare it by nesting a **Listener** element
inside this element. The class name you specify must implement the
`org.apache.catalina.LifecycleListener` interface, and the class must be
packaged in a jar and placed in the `$CATALINA_HOME/lib` directory. It will be
notified about the occurrence of the corresponding lifecycle events.
Configuration of such a listener looks like this:

```xml
<Context>
  ...
  <Listener className="com.mycompany.mypackage.MyListener" />
  ...
</Context>
```

You can also register a listener for a server component via `server.xml`:

```xml
<Server>
  ...
  <Listener className="com.mycompany.mypackage.MyListener" />
  ...
</Server>
```

## Installation

Compile the project:

```
$ mvn clean install
```

Copy the JAR file into Tomcat Catalina's library:

```
$ cp target/lifecycle-listener.jar ~/apache-tomcat-8.5.28/lib
```

Go to Tomcat server:

```
$ cd ~/apache-tomcat-8.5.28
```

## MyListener

### Server Lifecycle

Here's a demo for showing how to integrate `MyListener` as a server-level
listener:

- Register listener to server component via configuration file `conf/server.xml`:

  ```xml
  <Server>
    <Listener className="com.example.lifecycle.MyListener" />
    ...
  </Server>
  ```

- Start and stop Tomcat server:

      $ bin/startup.sh
      $ bin/shutdown.sh

- Verify MyListener has been called:

      $ grep MyListener logs/catalina.out
      03-Mar-2018 08:48:52.696 INFO [main] com.example.lifecycle.MyListener.lifecycleEvent StandardServer:before_init
      03-Mar-2018 08:48:52.790 INFO [main] com.example.lifecycle.MyListener.lifecycleEvent StandardServer:after_init
      03-Mar-2018 08:48:52.790 INFO [main] com.example.lifecycle.MyListener.lifecycleEvent StandardServer:before_start
      03-Mar-2018 08:48:52.802 INFO [main] com.example.lifecycle.MyListener.lifecycleEvent StandardServer:configure_start
      03-Mar-2018 08:48:52.873 INFO [main] com.example.lifecycle.MyListener.lifecycleEvent StandardServer:start
      03-Mar-2018 08:48:53.959 INFO [main] com.example.lifecycle.MyListener.lifecycleEvent StandardServer:after_start
      03-Mar-2018 08:51:00.981 INFO [main] com.example.lifecycle.MyListener.lifecycleEvent StandardServer:before_stop
      03-Mar-2018 08:51:00.982 INFO [main] com.example.lifecycle.MyListener.lifecycleEvent StandardServer:stop
      03-Mar-2018 08:51:00.982 INFO [main] com.example.lifecycle.MyListener.lifecycleEvent StandardServer:configure_stop
      03-Mar-2018 08:51:01.129 INFO [main] com.example.lifecycle.MyListener.lifecycleEvent StandardServer:after_stop
      03-Mar-2018 08:51:01.129 INFO [main] com.example.lifecycle.MyListener.lifecycleEvent StandardServer:before_destroy
      03-Mar-2018 08:51:01.133 INFO [main] com.example.lifecycle.MyListener.lifecycleEvent StandardServer:after_destroy

### Full Lifecycle

In the previous section, we took a look at server-level lifecycle events. Now, I
will show you the complete lifecycle for starting Tomcat and web applications.
In order to do this, we need to register the listener to both server component
via `server.xml` and context component via `context.xml`. Finally, analyse the
log as I did previously.

Register listener to all elements in `server.xml`:

```xml
<Server>
  <Listener className="com.example.lifecycle.MyListener" />
  ...
  <Service>
    <Listener className="com.example.lifecycle.MyListener" />
    ...
    <Engine>
      <Listener className="com.example.lifecycle.MyListener" />
      ...
      <Host>
        <Listener className="com.example.lifecycle.MyListener" />
        ...
      </Host>
    </Engine>
  </Service>
</Server>
```

Register listener to all elements in `context.xml`:

```xml
<Context>
  <Listener className="com.example.lifecycle.MyListener" />
  ...
</Context>
```

Check the results:

```
$ grep MyListener logs/catalina.out | cut -d ' ' -f 6-
StandardServer:before_init
StandardService:before_init
StandardEngine[Catalina]:before_init
StandardEngine[Catalina]:after_init
StandardService:after_init
StandardServer:after_init
StandardServer:before_start
StandardServer:configure_start
StandardServer:start
StandardService:before_start
StandardService:start
StandardEngine[Catalina]:before_start
StandardHost[localhost]:before_init
StandardHost[localhost]:after_init
StandardHost[localhost]:before_start
StandardContext[/docs]:before_start
StandardContext[/docs]:configure_start
StandardContext[/docs]:start
StandardContext[/docs]:after_start
StandardContext[/manager]:before_start
StandardContext[/manager]:configure_start
StandardContext[/manager]:start
StandardContext[/manager]:after_start
StandardContext[/examples]:before_start
StandardContext[/examples]:configure_start
StandardContext[/examples]:start
StandardContext[/examples]:after_start
StandardContext[]:before_start
StandardContext[]:configure_start
StandardContext[]:start
StandardContext[]:after_start
StandardContext[/host-manager]:before_start
StandardContext[/host-manager]:configure_start
StandardContext[/host-manager]:start
StandardContext[/host-manager]:after_start
StandardHost[localhost]:start
StandardHost[localhost]:after_start
StandardEngine[Catalina]:start
StandardEngine[Catalina]:after_start
StandardService:after_start
StandardServer:after_start
StandardServer:before_stop
StandardServer:stop
StandardServer:configure_stop
StandardService:before_stop
StandardService:stop
StandardEngine[Catalina]:before_stop
StandardEngine[Catalina]:stop
StandardHost[localhost]:before_stop
StandardHost[localhost]:stop
StandardContext[]:before_stop
StandardContext[]:stop
StandardContext[]:configure_stop
StandardContext[]:after_stop
StandardContext[/examples]:before_stop
StandardContext[/examples]:stop
StandardContext[/examples]:configure_stop
StandardContext[/examples]:after_stop
StandardContext[/host-manager]:before_stop
StandardContext[/host-manager]:stop
StandardContext[/host-manager]:configure_stop
StandardContext[/host-manager]:after_stop
StandardContext[/manager]:before_stop
StandardContext[/manager]:stop
StandardContext[/manager]:configure_stop
StandardContext[/manager]:after_stop
StandardContext[/docs]:before_stop
StandardContext[/docs]:stop
StandardContext[/docs]:configure_stop
StandardContext[/docs]:after_stop
StandardHost[localhost]:after_stop
StandardEngine[Catalina]:after_stop
StandardService:after_stop
StandardServer:after_stop
StandardServer:before_destroy
StandardService:before_destroy
StandardEngine[Catalina]:before_destroy
StandardHost[localhost]:before_destroy
StandardContext[]:before_destroy
StandardContext[]:after_destroy
StandardContext[/examples]:before_destroy
StandardContext[/examples]:after_destroy
StandardContext[/host-manager]:before_destroy
StandardContext[/host-manager]:after_destroy
StandardContext[/manager]:before_destroy
StandardContext[/manager]:after_destroy
StandardContext[/docs]:before_destroy
StandardContext[/docs]:after_destroy
StandardHost[localhost]:after_destroy
StandardEngine[Catalina]:after_destroy
StandardService:after_destroy
StandardServer:after_destroy
```

## StateCheckListener

`StateCheckListener` is an after-start server listener. It checks the state of
all [`Container`][Container], including engine, host, and context. Register
this listener to your server component via `server.xml`:

```xml
<Listener className="com.example.lifecycle.StateCheckListener" />
```

After [installation](#installation), run your Tomcat server in foreground:

```
$ bin/catalina.sh run
```

And you will see the following state-check:

```
04-Mar-2018 14:36:55.131 INFO [main] com.example.lifecycle.StateCheckListener.lifecycleEvent Status:
StandardEngine[Catalina]: STARTED
  StandardHost[localhost]: STARTED
    StandardContext[]: STARTED
      StandardWrapper[default]: STARTED
      StandardWrapper[jsp]: STARTED
    StandardContext[/examples]: STARTED
      StandardWrapper[RequestInfoExample]: STARTED
      StandardWrapper[async3]: STARTED
      StandardWrapper[numberwriter]: STARTED
      StandardWrapper[async2]: STARTED
      StandardWrapper[async1]: STARTED
      StandardWrapper[jsp]: STARTED
      StandardWrapper[async0]: STARTED
      StandardWrapper[bytecounter]: STARTED
      StandardWrapper[ServletToJsp]: STARTED
      StandardWrapper[RequestParamExample]: STARTED
      StandardWrapper[RequestHeaderExample]: STARTED
      StandardWrapper[default]: STARTED
      StandardWrapper[CookieExample]: STARTED
      StandardWrapper[CompressionFilterTestServlet]: STARTED
      StandardWrapper[HelloWorldExample]: STARTED
      StandardWrapper[SessionExample]: STARTED
      StandardWrapper[stock]: STARTED
    StandardContext[/host-manager]: STARTED
      StandardWrapper[default]: STARTED
      StandardWrapper[jsp]: STARTED
      StandardWrapper[HostManager]: STARTED
      StandardWrapper[HTMLHostManager]: STARTED
    StandardContext[/manager]: STARTED
      StandardWrapper[Status]: STARTED
      StandardWrapper[default]: STARTED
      StandardWrapper[jsp]: STARTED
      StandardWrapper[JMXProxy]: STARTED
      StandardWrapper[HTMLManager]: STARTED
      StandardWrapper[Manager]: STARTED
    StandardContext[/docs]: STARTED
      StandardWrapper[default]: STARTED
      StandardWrapper[jsp]: STARTED

04-Mar-2018 14:36:55.131 INFO [main] org.apache.catalina.startup.Catalina.start Server startup in 1329 ms
```

## StrictStateCheckListener

StrictStateCheckListener is an after-start server listener. It checks the state
of all [Container][Container], including engine, host, and context. The server
start-up will be aborted if there's any component failed to start (state
different from STARTED). It is an implementation answering to Stack Overflow
question: [Abort Tomcat start-up][16369619].

Here're steps for reproduction:

- Register `BrokenListener` and `StrickStateCheckListener` to server component
  via `server.xml`, where
  - `BrokenListener` creates a (hacked) context failure using system property
    `break.context=${context}`
  - `StrickStateCheckListener` aborts the server startup
- Add Java option `break.context=/examples` to make the context `/examples`
  failed:

  ```bash
  JAVA_OPTS="$JAVA_OPTS -Dbreak.context=/examples"
  ```

- Start the server in foreground:

  ```
  $ bin/catalina.sh run
  ```

As you can see, the server is aborted because context `/examples` failed to
start.

```
04-Mar-2018 15:41:35.509 INFO [main] com.example.lifecycle.StrictStateCheckListener.lifecycleEvent Status:
StandardEngine[Catalina]: STARTED
  StandardHost[localhost]: STARTED
    StandardContext[]: STARTED
      StandardWrapper[default]: STARTED
      StandardWrapper[jsp]: STARTED
    StandardContext[/examples]: FAILED  <<----------------- Hacked by BrokenListener
      StandardWrapper[RequestInfoExample]: STARTED
      StandardWrapper[async3]: STARTED
      StandardWrapper[numberwriter]: STARTED
      StandardWrapper[async2]: STARTED
      StandardWrapper[async1]: STARTED
      StandardWrapper[jsp]: STARTED
      StandardWrapper[async0]: STARTED
      StandardWrapper[bytecounter]: STARTED
      StandardWrapper[ServletToJsp]: STARTED
      StandardWrapper[RequestParamExample]: STARTED
      StandardWrapper[RequestHeaderExample]: STARTED
      StandardWrapper[default]: STARTED
      StandardWrapper[CookieExample]: STARTED
      StandardWrapper[CompressionFilterTestServlet]: STARTED
      StandardWrapper[HelloWorldExample]: STARTED
      StandardWrapper[SessionExample]: STARTED
      StandardWrapper[stock]: STARTED
    StandardContext[/host-manager]: STARTED
      StandardWrapper[default]: STARTED
      StandardWrapper[jsp]: STARTED
      StandardWrapper[HostManager]: STARTED
      StandardWrapper[HTMLHostManager]: STARTED
    StandardContext[/manager]: STARTED
      StandardWrapper[Status]: STARTED
      StandardWrapper[default]: STARTED
      StandardWrapper[jsp]: STARTED
      StandardWrapper[JMXProxy]: STARTED
      StandardWrapper[HTMLManager]: STARTED
      StandardWrapper[Manager]: STARTED
    StandardContext[/docs]: STARTED
      StandardWrapper[default]: STARTED
      StandardWrapper[jsp]: STARTED

04-Mar-2018 15:41:35.510 SEVERE [main] org.apache.catalina.startup.Catalina.start The required Server component failed to start so Tomcat is unable to start.
 org.apache.catalina.LifecycleException: Failed to start component [StandardServer[8005]]
	at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:167)
	at org.apache.catalina.startup.Catalina.start(Catalina.java:681)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:564)
	at org.apache.catalina.startup.Bootstrap.start(Bootstrap.java:353)
	at org.apache.catalina.startup.Bootstrap.main(Bootstrap.java:493)
Caused by: java.lang.IllegalStateException: StandardContext[/examples]: FAILED

	at com.example.lifecycle.StrictStateCheckListener.lifecycleEvent(StrictStateCheckListener.java:63)
	at org.apache.catalina.util.LifecycleBase.fireLifecycleEvent(LifecycleBase.java:94)
	at org.apache.catalina.util.LifecycleBase.setStateInternal(LifecycleBase.java:395)
	at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:160)
	... 7 more
```

## References

- [The LifeCycle Listener Component][listeners]
- [The Context Container: Lifecycle Listeners][1]

[1]: http://tomcat.apache.org/tomcat-8.5-doc/config/context.html#Lifecycle_Listeners
[Container]: http://tomcat.apache.org/tomcat-8.5-doc/api/org/apache/catalina/Container.html
[LifecycleListener]: http://tomcat.apache.org/tomcat-8.5-doc/api/org/apache/catalina/LifecycleListener.html
[16369619]: https://stackoverflow.com/questions/16369619/abort-tomcat-start-up
[listeners]: http://tomcat.apache.org/tomcat-8.5-doc/config/listeners.html
