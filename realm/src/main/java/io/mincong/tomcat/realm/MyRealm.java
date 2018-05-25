package io.mincong.tomcat.realm;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;

public class MyRealm extends RealmBase {

  private static final Logger log = Logger.getLogger(MyRealm.class.getName());
  private String username;
  private String password;

  @Override
  public Principal authenticate(String username, String credentials) {

    this.username = username;
    this.password = credentials;
    log.info("Authentication is taking place with userid: " + username);
    /* authentication just check the username and password is same*/
    if (this.username.equals(this.password)) {
      return getPrincipal(username);
    } else {
      return null;
    }
  }

  @Override
  protected String getName() {
    return username;
  }

  @Override
  protected String getPassword(String username) {
    return password;
  }

  @Override
  protected Principal getPrincipal(String string) {
    List<String> roles = new ArrayList<>();
    roles.add("TomcatAdmin"); // Adding role "TomcatAdmin" role to the user
    log.info("Realm: " + this);
    Principal principal = new GenericPrincipal(username, password, roles);
    log.info("Principal: " + principal);
    return principal;
  }
}
