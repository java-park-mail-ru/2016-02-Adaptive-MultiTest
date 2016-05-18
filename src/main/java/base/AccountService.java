package base;

import base.dataSets.UserDataSet;

import java.util.List;

/**
 * @author a.serebrennikova
 */
public interface AccountService {
    List<UserDataSet> getAllUsers();

    long addUser(UserDataSet user);

    UserDataSet getUser(long userId);

    UserDataSet getUserByLogin(String login);

    @SuppressWarnings("unused")
    UserDataSet getUserByEmail(String login);

    long updateUser(UserDataSet updatedUser, long userId);

    void deleteUser(long userId);

    void addSession(String sessionId, UserDataSet user);

    boolean isAuthenticated(String sessionId);

    UserDataSet getUserBySession(String sessionId);

    boolean isValidUser(UserDataSet user);

    void deleteSession(String sessionId);

    List<UserDataSet> getTopPlayers();

    void setUserScore(long id);
}
