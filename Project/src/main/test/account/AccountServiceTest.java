package account;

import main.cnf.Config;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
/**
 * Created by Snach on 27.03.16.
 */
public class AccountServiceTest {
    private AccountServiceImpl accountService;

    @Before
    public void setupAccountService(){

        final Config serverConfig = new Config(true);
        serverConfig.connectToDB();
        accountService = new AccountServiceImpl(serverConfig.getConfiguration());
    }

    @Test
    public void testAddUser() throws Exception {
        final boolean result = accountService.addUser(new UserProfile("test", "testpass","test@mail.com"));
        assertTrue(result);
    }

    @Test
    public void testAddUserWithSameLoginFail(){
        accountService.addUser(new UserProfile("test", "testpass1","1@mail.com"));
        final boolean result = accountService.addUser(new UserProfile("test", "testpass2","2@mail.com"));
        assertFalse(result);
    }

    @Test
    public void testAddUserWithSameEmailFail(){
        accountService.addUser(new UserProfile("test1", "testpass1","1@mail.com"));
        final boolean result = accountService.addUser(new UserProfile("test2", "testpass2","1@mail.com"));
        assertFalse(result);
    }

    @Test
    public void testGetUserByID(){
        final UserProfile user = new UserProfile("test1", "testpass1","1@mail.com");
        accountService.addUser(user);
        final UserProfile findUser = accountService.getUserByID(user.getUserID());
        assertFalse(findUser == null);
        assertEquals("test1", findUser.getLogin());
    }

    @Test
    public void testGetUserByLogin(){
        final UserProfile user = new UserProfile("test1", "testpass1","1@mail.com");
        accountService.addUser(user);
        final UserProfile compareUser = accountService.getUserByLogin("test1");
        assertFalse(compareUser == null);
        assertEquals(compareUser.getLogin(),"test1");
    }

    @Test
    public void testGetUserByEmail(){
        final UserProfile user = new UserProfile("test1", "testpass1","1@mail.com");
        accountService.addUser(user);
        final UserProfile findUser = accountService.getUserByEmail(user.getEmail());

        assertFalse(findUser == null);
        assertEquals(findUser.getLogin(),"test1");
    }

    @Test
    public void testEditLoginUser(){
        final UserProfile oldUser = new UserProfile("test", "testpass1","1@mail.com");
        accountService.addUser(oldUser);
        final UserProfile newUser = new UserProfile("newTest","testpass1","1@mail.com");
        accountService.editUser(oldUser,newUser);

        assertTrue(accountService.getUserByLogin("newTest")!=null &&
                (accountService.getUserByLogin("test")==null));
    }

    @Test
    public void testEditPasswordUser(){
        final UserProfile oldUser = new UserProfile("test","testpass1","1@mail.com");
        accountService.addUser(oldUser);
        final UserProfile newUser = new UserProfile("test","testpass2","1@mail.com");
        accountService.editUser(oldUser,newUser);
        final UserProfile findNewUser = accountService.getUserByLogin("test");

        assertFalse(findNewUser == null);
        assertEquals("testpass2", findNewUser.getPassword());
    }

    @Test
    public void testEditEmailUser(){
        final UserProfile oldUser = new UserProfile("test","testpass1","1@mail.com");
        accountService.addUser(oldUser);
        final UserProfile newUser = new UserProfile("test","testpass1","2@mail.com");
        accountService.editUser(oldUser,newUser);
        final UserProfile findNewUser = accountService.getUserByLogin("test");

        assertFalse(findNewUser == null);
        assertEquals("2@mail.com", findNewUser.getEmail());
    }

    @Test
    public void testDeleteUser(){
        final UserProfile user = new UserProfile("test","testpass1","1@mail.com");
        accountService.addUser(user);
        accountService.deleteUser(user.getUserID());
        assertTrue(accountService.getUserByLogin("test") == null);
    }

    @Test
    public void testAddSession(){
        final UserProfile user = new UserProfile("test","testpass1","1@mail.com");
        accountService.addUser(user);
        accountService.addSession("testSessionID",user);
        assertTrue(accountService.isLoggedIn("testSessionID"));
    }

    @Test
    public void testDeleteSession(){
        final UserProfile user = new UserProfile("test","testpass1","1@mail.com");
        accountService.addUser(user);
        accountService.addSession("testSessionID",user);
        accountService.deleteSession("testSessionID");
        assertFalse(accountService.isLoggedIn("testSessionID"));
    }

    @Test
    public void testDeleteSessionUncorrectArg(){
        final UserProfile user = new UserProfile("test","testpass1","1@mail.com");
        accountService.addUser(user);
        accountService.addSession("testSessionID",user);
        accountService.deleteSession("differSessionID");
        assertTrue(accountService.isLoggedIn("testSessionID"));
    }

    @Test
    public void testCheckAuth(){
        final UserProfile user = new UserProfile("test","testpass1","1@mail.com");
        accountService.addUser(user);
        assertTrue(accountService.checkAuth("test","testpass1"));
    }

    @Test
    public void testCheckAuthFailLogin(){
        final UserProfile user = new UserProfile("test","testpass1","1@mail.com");
        accountService.addUser(user);
        assertFalse(accountService.checkAuth("differUser","testpass1"));
    }

    @Test
    public void testCheckAuthFailPassword(){
        final UserProfile user = new UserProfile("test","testpass1","1@mail.com");
        accountService.addUser(user);
        assertFalse(accountService.checkAuth("test","differPass"));
    }

    @Test
    public void testGetAllUsers() {
        final UserProfile user1 = new UserProfile("test", "testpass1", "1@mail.com");
        accountService.addUser(user1);
        final UserProfile user2 = new UserProfile("test2", "testpass2", "2@mail.com");
        accountService.addUser(user2);
        final UserProfile user3 = new UserProfile("test3", "testpass3", "3@mail.com");
        accountService.addUser(user3);
        final List<UserProfile> users = accountService.getAllUsers();
        assertTrue(users.get(0).getLogin().equals("test")
                && users.get(1).getLogin().equals("test2")
                && users.get(2).getLogin().equals("test3"));
    }


}
