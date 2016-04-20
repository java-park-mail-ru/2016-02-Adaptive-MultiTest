package frontend;

import base.GameMechanics;
import base.GameUser;
import base.WebSocketService;
import com.sun.istack.internal.Nullable;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.jetbrains.annotations.NotNull;
import mechanics.Coords;
import mechanics.PossibleCourses;

import java.io.IOException;

/**
 * Created by Sasha on 17.04.16.
 */
@WebSocket
public class GameWebSocket {
    @NotNull
    private final String myName;
    @Nullable
    private Session session;
    @NotNull
    private final GameMechanics gameMechanics;
    @NotNull
    private final WebSocketService webSocketService;

    private final ObjectMapper mapper;

    private final JsonNodeFactory factory;

    public GameWebSocket(@NotNull String myName, @NotNull GameMechanics gameMechanics, @NotNull WebSocketService webSocketService) {
        this.myName = myName;
        this.gameMechanics = gameMechanics;
        this.webSocketService = webSocketService;
        mapper = new ObjectMapper();
        mapper.getJsonFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        factory = JsonNodeFactory.instance;
    }

    @NotNull
    public String getMyName() {
        return myName;
    }

    public void startGame(@NotNull GameUser user, Coords red, Coords blue) {
        try {
            final ObjectNode jsonStart = new ObjectNode(factory);
            jsonStart.put("status", "start");
            jsonStart.put("enemyName", user.getEnemyName() == null ? "" : user.getEnemyName());
            jsonStart.put("redx", red.getX());
            jsonStart.put("redy", red.getY());
            jsonStart.put("bluex", blue.getX());
            jsonStart.put("bluey", blue.getY());

            if (session != null && session.isOpen())
                session.getRemote().sendString(jsonStart.toString());
        } catch (IOException | WebSocketException e) {
            e.printStackTrace();
        }
    }

    public void sendPossibleCourses(PossibleCourses possibleCourses) {
        try {
            final ObjectNode jsonPossibleCourses = mapper.valueToTree(possibleCourses);
            jsonPossibleCourses.put("status", "move");

            if (session != null && session.isOpen())
                session.getRemote().sendString(jsonPossibleCourses.toString());
        } catch (IOException | WebSocketException e) {
            e.printStackTrace();
        }
    }

    public void waitEnemyMove() {
        try {
            final ObjectNode jsonWait = new ObjectNode(factory);
            jsonWait.put("status", "wait");

            if (session != null && session.isOpen())
                session.getRemote().sendString(jsonWait.toString());
        } catch (IOException | WebSocketException e) {
            e.printStackTrace();
        }
    }

    public void gameOver(boolean win) {
        try {
            final ObjectNode jsonEndGame = new ObjectNode(factory);
            jsonEndGame.put("status", "finish");
            jsonEndGame.put("win", win);
            if (session != null && session.isOpen())
                session.getRemote().sendString(jsonEndGame.toString());
        } catch (IOException | WebSocketException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unused", "OverlyBroadCatchBlock"})
    @OnWebSocketMessage
    public void onMessage(String data) {
        try {
            final Coords coords = mapper.readValue(data, Coords.class);
            gameMechanics.move(coords, myName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"ParameterHidesMemberVariable", "unused"})
    @OnWebSocketConnect
    public void onOpen(@NotNull Session session) {
        this.session = session;
        webSocketService.addUser(this);
        gameMechanics.addUser(myName);
    }

    @SuppressWarnings({"unused", "UnusedParameters"})
    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        webSocketService.removeUser(this);
        gameMechanics.removeUser(myName);
    }
}
