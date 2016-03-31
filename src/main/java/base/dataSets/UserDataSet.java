package base.dataSets;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

/**
 * Created by Sasha on 27.03.16.
 */
@SuppressWarnings({"DefaultFileTemplate"})
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

    @SuppressWarnings("RedundantNoArgConstructor")
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

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        final UserDataSet other = (UserDataSet) obj;
        if (this.id != other.id) return false;
        if (!this.login.equals(other.login)) return false;
        if (!this.email.equals(other.email)) return false;
        if (!this.password.equals(other.password)) return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        return 76+133*login.hashCode();
    }
}
