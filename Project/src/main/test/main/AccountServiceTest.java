package main;

import rest.UserProfile;
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
        accountService = new AccountServiceImpl();
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
        UserProfile user = new UserProfile("test1", "testpass1","1@mail.com");
        accountService.addUser(user);
        assertEquals("test1", accountService.getUserByID(user.getUserID()).getLogin());
    }

    @Test
    public void testGetUserByLogin(){
        UserProfile user = new UserProfile("test1", "testpass1","1@mail.com");
        accountService.addUser(user);
        UserProfile compareUser = accountService.getUserByLogin("test1");
        assertEquals(compareUser.getLogin(),"test1");
    }

    @Test
    public void testGetUserByEmail(){
        UserProfile user = new UserProfile("test1", "testpass1","1@mail.com");
        accountService.addUser(user);
        assertEquals(accountService.getUserByEmail(user.getEmail()).getLogin(),"test1");
    }

    @Test
    public void testEditLoginUser(){
        UserProfile oldUser = new UserProfile("test", "testpass1","1@mail.com");
        accountService.addUser(oldUser);
        UserProfile newUser = new UserProfile("newTest","testpass1","1@mail.com");
        accountService.editUser(oldUser,newUser);

        assertTrue(accountService.getUserByLogin("newTest")!=null &&
                (accountService.getUserByLogin("test")==null));
    }

    @Test
    public void testEditPasswordUser(){
        UserProfile oldUser = new UserProfile("test","testpass1","1@mail.com");
        accountService.addUser(oldUser);
        UserProfile newUser = new UserProfile("test","testpass2","1@mail.com");
        accountService.editUser(oldUser,newUser);

        assertEquals("testpass2", accountService.getUserByLogin("test").getPassword());
    }

    @Test
    public void testEditEmailUser(){
        UserProfile oldUser = new UserProfile("test","testpass1","1@mail.com");
        accountService.addUser(oldUser);
        UserProfile newUser = new UserProfile("test","testpass1","2@mail.com");
        accountService.editUser(oldUser,newUser);

        assertEquals("2@mail.com", accountService.getUserByLogin("test").getEmail());
    }

    @Test
    public void testDeleteUser(){
        UserProfile user = new UserProfile("test","testpass1","1@mail.com");
        accountService.addUser(user);
        accountService.deleteUser(user.getUserID());
        assertTrue(accountService.getUserByLogin("test") == null);
    }

    @Test
    public void testAddSession(){
        UserProfile user = new UserProfile("test","testpass1","1@mail.com");
        accountService.addUser(user);
        accountService.addSession("testSessionID",user);
        assertTrue(accountService.isLoggedIn("testSessionID"));
    }

    @Test
    public void testDeleteSession(){
        UserProfile user = new UserProfile("test","testpass1","1@mail.com");
        accountService.addUser(user);
        accountService.addSession("testSessionID",user);
        accountService.deleteSession("testSessionID");
        assertFalse(accountService.isLoggedIn("testSessionID"));
    }

    @Test
    public void testDeleteSessionUncorrectArg(){
        UserProfile user = new UserProfile("test","testpass1","1@mail.com");
        accountService.addUser(user);
        accountService.addSession("testSessionID",user);
        accountService.deleteSession("differSessionID");
        assertTrue(accountService.isLoggedIn("testSessionID"));
    }

    @Test
    public void testCheckAuth(){
        UserProfile user = new UserProfile("test","testpass1","1@mail.com");
        accountService.addUser(user);
        assertTrue(accountService.checkAuth("test","testpass1"));
    }

    @Test
    public void testCheckAuthFailLogin(){
        UserProfile user = new UserProfile("test","testpass1","1@mail.com");
        accountService.addUser(user);
        assertFalse(accountService.checkAuth("differUser","testpass1"));
    }

    @Test
    public void testCheckAuthFailPassword(){
        UserProfile user = new UserProfile("test","testpass1","1@mail.com");
        accountService.addUser(user);
        assertFalse(accountService.checkAuth("test","differPass"));
    }

    @Test
    public void testGetAllUsers() {
        UserProfile user1 = new UserProfile("test", "testpass1", "1@mail.com");
        accountService.addUser(user1);
        UserProfile user2 = new UserProfile("test2", "testpass2", "2@mail.com");
        accountService.addUser(user2);
        UserProfile user3 = new UserProfile("test3", "testpass3", "3@mail.com");
        accountService.addUser(user3);
        List<UserProfile> users = accountService.getAllUsers();
        assertTrue(users.get(0).getLogin().equals("test")
                && users.get(1).getLogin().equals("test2")
                && users.get(2).getLogin().equals("test3"));
    }


}
