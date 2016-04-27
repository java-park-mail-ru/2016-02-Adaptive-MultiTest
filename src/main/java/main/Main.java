package main;

import base.AccountService;
import accountService.AccountServiceImpl;
import base.GameMechanics;
import base.WebSocketService;
import frontend.WebSocketGameServlet;
import frontend.WebSocketServiceImpl;
import helpers.Context;
import mechanics.GameMechanicsImpl;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import frontend.rest.Scores;
import frontend.rest.Sessions;
import frontend.rest.Users;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @author a.serebrennikova
 */
@SuppressWarnings({"OverlyBroadThrowsClause", "WeakerAccess"})
public class Main {
    public static void main(String[] args) throws Exception {
        final String cfgPath = new File("").getAbsolutePath() + "/cfg/";
        //final URL cfgPath = Main.class.getResource("/cfg/");
        final Properties serverProperties = new Properties();
        final Properties dbProperties = new Properties();
        try {
            FileInputStream fis = new FileInputStream(cfgPath + "server.properties");
            serverProperties.load(fis);
            fis = new FileInputStream(cfgPath + "db.properties");
            dbProperties.load(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final String port = serverProperties.getProperty("port");
        final String host = serverProperties.getProperty("host");
        System.out.append("Starting at host: ").append(host).append(", port: ").append(port).append('\n');

        final Server server = new Server(Integer.valueOf(port));
        final ServletContextHandler contextHandler = new ServletContextHandler(server, "/api/", ServletContextHandler.SESSIONS);

        final String dbName = dbProperties.getProperty("main_db.name");
        final Context context = new Context();

        final AccountService accountService = new AccountServiceImpl(dbName);
        context.put(AccountService.class, accountService);
        final WebSocketService webSocketService = new WebSocketServiceImpl();
        context.put(WebSocketService.class, webSocketService);
        final GameMechanics gameMechanics = new GameMechanicsImpl(webSocketService, accountService);
        context.put(GameMechanics.class, gameMechanics);

        contextHandler.addServlet(new ServletHolder(new WebSocketGameServlet(context)), "/gameplay");

        final Set<Class<?>> classes = new HashSet<>();
        classes.add(Users.class);
        classes.add(Sessions.class);
        classes.add(Scores.class);
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

        gameMechanics.run();
    }
}