package main;

import base.AccountService;
import account.AccountServiceImpl;
import main.cnf.Config;
import game.firstlvl.ShuffleWordService;
import frontend.WebSocketGameServlet;
import frontend.WebSocketServiceImpl;
import game.GameMechanicsImpl;
import main.cnf.ReaderXMLData;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.server.handler.HandlerList;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import rest.Scoreboard;
import rest.Sessions;
import rest.Users;
import org.hibernate.HibernateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * created by snach
 */

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        final Config serverConfig = new Config(false);
        serverConfig.loadConfig();

        LOGGER.info("Starting at port: " + String.valueOf(serverConfig.getPort()));

        final Context context = new Context();

        final Server server = new Server(serverConfig.getPort());
        final ServletContextHandler contextHandler = new ServletContextHandler(server, "/api/", ServletContextHandler.SESSIONS);

        try {
            context.put(AccountService.class, new AccountServiceImpl(serverConfig.getConfiguration()));
        } catch (HibernateException e) {
            LOGGER.error("Fail to connect " + serverConfig.getConfiguration().getProperty("hibernate.connection.url"), e);
            System.exit(1);
        }

        context.put(ShuffleWordService.class, new ShuffleWordService(ReaderXMLData.readXML(), ReaderXMLData.getMaxWordId()));
        context.put(WebSocketServiceImpl.class, new WebSocketServiceImpl());
        context.put(GameMechanicsImpl.class, new GameMechanicsImpl(context.get(WebSocketServiceImpl.class)));

        final ResourceConfig config = new ResourceConfig(Users.class, Sessions.class, Scoreboard.class);
        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(context);
            }
        });

        final ServletHolder servletHolder = new ServletHolder(new ServletContainer(config));

        final HandlerList handlers = new HandlerList();

        final ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase("public_html");

        handlers.setHandlers(new Handler[]{resourceHandler, contextHandler});
        server.setHandler(handlers);

        contextHandler.addServlet(servletHolder, "/*");
        contextHandler.addServlet(new ServletHolder(new WebSocketGameServlet(context)), "/gameplay");

        server.start();
        context.get(GameMechanicsImpl.class).run();
    }
}