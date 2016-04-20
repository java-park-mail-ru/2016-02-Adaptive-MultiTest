package frontend;

import frontend.GameWebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import helpers.Context;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * Created by Sasha on 17.04.16.
 */
@WebServlet(name = "WebSocketGameServlet", urlPatterns = {"/gameplay"})
public class WebSocketGameServlet extends WebSocketServlet {
    @Inject
    private final Context context;

    public WebSocketGameServlet(Context context) {
        this.context = context;
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.setCreator(new GameWebSocketCreator(context));
    }
}
