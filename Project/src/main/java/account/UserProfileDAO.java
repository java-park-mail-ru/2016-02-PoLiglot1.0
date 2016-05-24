package account;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


/**
 * Created by Snach on 27.03.16.
 */
public class UserProfileDAO {
    final Session session;
    private static final int TOP_MAN = 10;

    public UserProfileDAO(Session session) {
        this.session = session;
    }

    public UserProfile readUserByID(long id) {
        final Criteria criteria = session.createCriteria(UserProfile.class);
        return (UserProfile) criteria.add(Restrictions.eq("id", id)).uniqueResult();
    }

    @Nullable
    public UserProfile readUserByLogin(String login) {
        final Criteria criteria = session.createCriteria(UserProfile.class);
        return (UserProfile) criteria.add(Restrictions.eq("login", login)).uniqueResult();
    }

    public UserProfile readUserByEmail(String email) {
        final Criteria criteria = session.createCriteria(UserProfile.class);
        return (UserProfile) criteria.add(Restrictions.eq("email", email)).uniqueResult();
    }

    public boolean addUser(UserProfile user) {
        if (this.readUserByLogin(user.getLogin()) != null || this.readUserByEmail(user.getEmail()) != null)
            return false;
        if (user.getLogin().isEmpty() || user.getPassword().isEmpty() || user.getEmail().isEmpty())
            return false;

        session.save(user);
        return true;
    }

    public void editUser(@NotNull UserProfile oldUser, UserProfile newUser) {
        if (!newUser.getLogin().isEmpty() && this.readUserByLogin(newUser.getLogin()) == null) {
            readUserByID(oldUser.getUserID()).setLogin(newUser.getLogin());
            session.flush();
        }
        if (!newUser.getEmail().isEmpty() && this.readUserByEmail(newUser.getEmail()) == null) {
            readUserByID(oldUser.getUserID()).setEmail(newUser.getEmail());
            session.flush();
        }
        if (!newUser.getPassword().isEmpty()) {
            readUserByID(oldUser.getUserID()).setPassword(newUser.getPassword());
            session.flush();
        }
    }

    public void deleteUser(long userID) {
        final UserProfile user = readUserByID(userID);
        session.delete(user);
    }

    @SuppressWarnings("unchecked")
    public List<UserProfile> readAll() {
        final Criteria criteria = session.createCriteria(UserProfile.class);
        return (List<UserProfile>) criteria.list();
    }

    public void editUserScore(@NotNull String login, @NotNull Integer newScore) {

        final UserProfile user = this.readUserByLogin(login);
        if (user != null) {
            user.setScore(newScore);
            session.flush();
        }
    }

    @SuppressWarnings("unchecked")
    public List<UserProfile> readTop() {
        final Criteria criteria = session.createCriteria(UserProfile.class);
        criteria.addOrder(Order.desc("score"));
        criteria.setMaxResults(TOP_MAN);
        return (List<UserProfile>) criteria.list();
    }

}
