package rest;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author a.serebrennikova
 */
public class UserProfile {
    private static final AtomicLong ID_GENETATOR = new AtomicLong(0);
    @NotNull
    private String login;
    @NotNull
    private String password;
    @NotNull
    private String email;

    private long id;

    public UserProfile() {
        this.id = ID_GENETATOR.getAndIncrement();
        login = "";
        password = "";
        email = "";
    }

    public UserProfile(@NotNull String login, @NotNull String password) {
        this.id = ID_GENETATOR.getAndIncrement();
        this.login = login;
        this.password = password;
        email = "";
    }

    public UserProfile(@NotNull String login, @NotNull String password, @NotNull String email) {
        this.id = ID_GENETATOR.getAndIncrement();
        this.login = login;
        this.password = password;
        this.email = email;
    }

    @NotNull
    public String getLogin() {
        return login;
    }

    public void setLogin(@NotNull String login) {
        this.login = login;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    public long getId() { return id; }

    public String getEmail() { return email; }

    public void setEmail(@NotNull String email) { this.email = email; }
}
