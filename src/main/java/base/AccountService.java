package base;

import base.dataSets.UserDataSet;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author a.serebrennikova
 */
public interface AccountService {
    List<UserDataSet> getAllUsers();

    long addUser(UserDataSet user);

    UserDataSet getUser(long userId);

    UserDataSet getUserByLogin(String login);

    UserDataSet getUserByEmail(String login);

    long updateUser(UserDataSet updatedUser, long userId);

    void deleteUser(long userId);

    void addSession(String sessionId, UserDataSet user);

    boolean isAuthenticated(String sessionId);

    UserDataSet getUserBySession(String sessionId);

    boolean isValidUser(UserDataSet user);

    void deleteSession(String sessionId);
}
