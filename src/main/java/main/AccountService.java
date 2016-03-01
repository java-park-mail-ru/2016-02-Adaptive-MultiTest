package main;

import org.eclipse.jetty.server.Authentication;
import org.jetbrains.annotations.Nullable;
import rest.UserProfile;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author a.serebrennikova
 */
public class AccountService {
    private Map<Long, UserProfile> users = new HashMap<>();
    private Map<String, UserProfile> sessions = new HashMap<>();

    public AccountService() {
        users.put(0L, new UserProfile("admin", "admin", "admin@admin"));
        users.put(1L, new UserProfile("guest", "12345", "guest@guest"));
    }

    public Collection<UserProfile> getAllUsers() { return users.values(); }

    public boolean addUser(UserProfile user) {
        if (getUserByLogin(user.getLogin()) != null) {
            return false;
        } else {
            users.put(user.getId(), user);
            return true;
        }
    }

    public UserProfile getUser(long userId) {
        return users.get(userId);
    }

    public void updateUser(UserProfile oldUser, UserProfile newUser) {
        if (!oldUser.getLogin().equals(newUser.getLogin()) && getUserByLogin(newUser.getLogin()) == null) {
            oldUser.setLogin(newUser.getLogin());
        }
        if (!oldUser.getEmail().equals(newUser.getEmail()) && getUserByEmail(newUser.getEmail()) == null) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (!oldUser.getPassword().equals(newUser.getPassword())) {
            oldUser.setPassword(newUser.getPassword());
        }
    }

    public void deleteUser(UserProfile user) {
        users.remove(user.getId());
    }

    @Nullable
    public UserProfile getUserByLogin(String login) {
        for(Map.Entry<Long,UserProfile> entry: users.entrySet()) {
            if (entry.getValue().getLogin().equals(login))
                return entry.getValue();
        }
        return null;
    }

    @Nullable
    public UserProfile getUserByEmail(String email) {
        for(Map.Entry<Long,UserProfile> entry: users.entrySet()) {
            if (entry.getValue().getEmail().equals(email))
                return entry.getValue();
        }
        return null;
    }

    public void addSession(String sessionId, UserProfile user) { sessions.put(sessionId, user); }

    public boolean isAuthenticated(String sessionId) { return sessions.containsKey(sessionId); }

    public UserProfile getUserBySession(String sessionId) { return sessions.get(sessionId); }

    public boolean isValidUser(UserProfile user) {
        UserProfile actualUser = getUserByLogin(user.getLogin());
        return (actualUser != null && actualUser.getPassword().equals(user.getPassword()));
    }

    public void deleteSession(String sessionId) { sessions.remove(sessionId); }

}
