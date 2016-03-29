package base.dataSets;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

/**
 * Created by Sasha on 27.03.16.
 */
@SuppressWarnings({"SameParameterValue", "DefaultFileTemplate"})
@Entity
@Table(name = "User")
public class UserDataSet {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "login")
    private String login;

    @Column(name="password")
    private String password;

    @Column(name="email")
    private String email;

    public UserDataSet() {
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

    public void setId(long id) { this.id = id; }

    @NotNull
    public String getEmail() { return email; }

    public void setEmail(@NotNull String email) { this.email = email; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        UserDataSet other = (UserDataSet) obj;
        return this.id == other.getId() && this.login.equals(other.getLogin()) && this.email.equals(other.getEmail())
                && this.password.equals(other.getPassword());
    }

    @Override
    public int hashCode()
    {
        return 76+133*login.hashCode();
    }
}
