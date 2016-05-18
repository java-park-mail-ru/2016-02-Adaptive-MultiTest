package frontend;

import base.AccountService;
import base.GameMechanics;
import base.GameUser;
import base.WebSocketService;
import base.dataSets.UserDataSet;
import org.jetbrains.annotations.Nullable;
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
    @SuppressWarnings("ConstantNamingConvention")
    private static final int unexpectedExitCode = 1001;
    @SuppressWarnings("ConstantNamingConvention")
    private static final int rightExitCode = 1000;

    private final long myId;
    @Nullable
    private Session session;
    @NotNull
    private final GameMechanics gameMechanics;
    @NotNull
    private final WebSocketService webSocketService;
    @NotNull
    private final AccountService accountService;

    private final ObjectMapper mapper;

    private final JsonNodeFactory factory;

    public GameWebSocket(long myId, @NotNull GameMechanics gameMechanics,
                         @NotNull WebSocketService webSocketService, @NotNull AccountService accountService) {
        this.myId = myId;
        this.gameMechanics = gameMechanics;
        this.webSocketService = webSocketService;
        this.accountService = accountService;
        mapper = new ObjectMapper();
        mapper.getJsonFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        factory = JsonNodeFactory.instance;
    }

    public long getMyId() {
        return myId;
    }

    public void startGame(@NotNull GameUser user, Coords red, Coords blue, String myColor) {
        try {
            final UserDataSet me = accountService.getUser(user.getMyId());
            final UserDataSet enemy = accountService.getUser(user.getEnemyId());

            final ObjectNode jsonStart = new ObjectNode(factory);
            jsonStart.put("status", "start");
            jsonStart.put("myName", me.getLogin());
            jsonStart.put("enemyName", enemy.getLogin());
            jsonStart.put("color", myColor);
            jsonStart.put("firstRed", mapper.valueToTree(red));
            jsonStart.put("firstBlue", mapper.valueToTree(blue));

            if (session != null && session.isOpen())
                session.getRemote().sendString(jsonStart.toString());
        } catch (IOException | WebSocketException e) {
            e.printStackTrace();
        }
    }

    public void sendPossibleCourses(PossibleCourses possibleCourses, Coords enemyMove) {
        try {
            final ObjectNode jsonPossibleCourses = mapper.valueToTree(possibleCourses);
            jsonPossibleCourses.put("enemyMove", mapper.valueToTree(enemyMove));
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
            if (session != null && session.isOpen()) {
                session.getRemote().sendString(jsonEndGame.toString());
                session.close();
            }
        } catch (IOException | WebSocketException e) {
            e.printStackTrace();
        }
    }

    public void sendError(String error) {
        try {
            final ObjectNode jsonError = new ObjectNode(factory);
            jsonError.put("status", "error");
            jsonError.put("errorMessage", error);
            if (session != null && session.isOpen())
                session.getRemote().sendString(jsonError.toString());
        } catch (IOException | WebSocketException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void reportUnexpectedEnemyExit() {
        sendError("unexpectedEnemyExit");
        onClose(rightExitCode, "");
        session.close();
    }

    @SuppressWarnings({"unused", "OverlyBroadCatchBlock"})
    @OnWebSocketMessage
    public void onMessage(String data) {
        try {
            final Coords coords = mapper.readValue(data, Coords.class);
            gameMechanics.move(coords, myId, false);
        } catch (IOException e) {
            sendError("invalid data");
        }
    }

    @SuppressWarnings({"ParameterHidesMemberVariable", "unused"})
    @OnWebSocketConnect
    public void onOpen(@NotNull Session session) {
        this.session = session;

        if(myId != -1) {
            webSocketService.addUser(this);
            gameMechanics.addUser(myId);
        } else {
            sendError("unauthorized");
            session.close();
        }
    }

    @SuppressWarnings({"unused", "UnusedParameters"})
    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        if (myId != -1) {
            if (statusCode == unexpectedExitCode)
                gameMechanics.exitUnexpectedly(myId);
            webSocketService.removeUser(this);
            gameMechanics.removeUser(myId);
        }
    }
}
