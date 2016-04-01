package main;

import base.AccountService;
import accountService.AccountServiceImpl;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import rest.Sessions;
import rest.Users;

import java.util.HashSet;
import java.util.Set;

/**
 * @author a.serebrennikova
 */
@SuppressWarnings({"OverlyBroadThrowsClause", "WeakerAccess"})
public class Main {
    public static void main(String[] args) throws Exception {
        int port = -1;
        if (args.length == 1) {
            port = Integer.valueOf(args[0]);
        } else {
            System.err.println("Specify port");
            System.exit(1);
        }

        System.out.append("Starting at port: ").append(String.valueOf(port)).append('\n');

        final Server server = new Server(port);
        final ServletContextHandler contextHandler = new ServletContextHandler(server, "/api/", ServletContextHandler.SESSIONS);

        final Context context = new Context();
        context.put(AccountService.class, new AccountServiceImpl("MultiTest"));

        final Set<Class<?>> classes = new HashSet<>();
        classes.add(Users.class);
        classes.add(Sessions.class);
        final ResourceConfig config = new ResourceConfig(classes);
        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(context);
            }
        });

        final ServletHolder servletHolder = new ServletHolder(new ServletContainer(config));

        final ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("public_html");

        final HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resourceHandler, contextHandler});

        contextHandler.addServlet(servletHolder, "/*");
        server.setHandler(handlers);

        server.start();
        server.join();
    }
}