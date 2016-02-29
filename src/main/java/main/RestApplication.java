package main;

import rest.Users;
import rest.Sessions;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * @author a.serebrennikova
 */
@ApplicationPath("api")
public class RestApplication extends Application {
    @Override
    public Set<Object> getSingletons() {
        final HashSet<Object> objects = new HashSet<>();
        AccountService accountService = new AccountService();
        objects.add(new Users(accountService));
        objects.add(new Sessions(accountService));
        return objects;
    }
}
