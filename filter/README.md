# Servlet and Filter

Demonstration of HTTP Servlet and Filter, and the chaining responsibility.

Open two terminals, one plays as server, the other as client.

- On the server side, run project as WAR using Maven:

  ```
  $ mvn clean install
  $ mvn cargo:run
  ```

- On the client side, send a HTTP request:

  ```
  $ curl -s http://localhost:8080/filter/ | jq
  {
    "status": 200
  }
  ```

Then observe the log created by Tomcat server.

For more detail, see
<https://mincong-h.github.io/2018/05/03/servlet-and-filter/>.
