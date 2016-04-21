package base.dataSets;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

/**
 * Created by Sasha on 27.03.16.
 */
@SuppressWarnings({"DefaultFileTemplate", "NullableProblems"})
@Entity
@Table(name = "User")
public class UserDataSet {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "login")
    @NotNull
    private String login;

    @Column(name="password")
    @NotNull
    private String password;

    @Column(name="email")
    @NotNull
    private String email;

    @Column(name="score")
    @NotNull
    private int score;

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

    public int getScore() { return score; }

    public void setScore(int score) { this.score = score; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        //noinspection QuestionableName
        final UserDataSet that = (UserDataSet) o;

        if (id != that.id) return false;
        if (!login.equals(that.login)) return false;
        // noinspection SimplifiableIfStatement
        if (!password.equals(that.password)) return false;
        return email.equals(that.email);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + login.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + email.hashCode();
        return result;
    }
}
