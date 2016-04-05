package rest;

import main.AccountService;
import main.AccountServiceImpl;
import main.Context;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Application;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Snach on 28.03.16.
 */
public class UsersTest extends JerseyTest {
    @Override
    protected Application configure() {
        final Context context = new Context();
        context.put(AccountService.class, new AccountServiceImpl());

        final ResourceConfig config = new ResourceConfig(Users.class, Sessions.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpSession session = mock(HttpSession.class);
        //noinspection AnonymousInnerClassMayBeStatic
        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(context);
                bind(request).to(HttpServletRequest.class);
                bind(session).to(HttpSession.class);
                when(request.getSession()).thenReturn(session);
                when(session.getId()).thenReturn("session");
            }
        });

        return config;
    }

    private AccountServiceImpl accountService;

    @SuppressWarnings("ConstantNamingConvention")
    private static final int OK = 200;
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;

    @Before
    public void setupAccountService(){
        accountService = new AccountServiceImpl();
    }

    @Test
    public void testGetUserByID() {
        accountService.addUser(new UserProfile("test1", "testpass1","1@mail.com"));
        final String json = target("user").path("1").request().get(String.class);
        assertEquals("{\n  \"id\": 1,\n  \"login\": \"test1\",\n  \"email\": \"1@mail.com\" \n}", json);
    }

    @Test
    public void testGetUserByIncorrectId() {
        accountService.addUser(new UserProfile("test1", "testpass1","1@mail.com"));
        accountService.addUser(new UserProfile("test2", "testpass2","2@mail.com"));
        final String json = target("user").path("2").request().get(String.class);
        assertNotEquals("{\n  \"id\": 1,\n  \"login\": \"test1\",\n  \"email\": \"1@mail.com\" \n}", json);
    }

    @Test
    public void testGetUserByIdFail() {
        accountService.addUser(new UserProfile("test1", "testpass1","1@mail.com"));
        assertEquals(target("user").path("2").request().get().getStatus(), UNAUTHORIZED);
    }

    @Test
    public void testCreateUser() {
        UserProfile user = new UserProfile("test1", "testpass1","1@mail.com");
        final String json = target("user").request("application/json").put(Entity.json(user), String.class);
        assertEquals("{ \"id\": \"1\" }", json);
    }

    @Test
    public void testCreateNullUser() {
        UserProfile user = new UserProfile();
        final Response resp = target("user").request("application/json").put(Entity.json(user));
        assertEquals(resp.getStatus(), FORBIDDEN);
    }


    @Test
    public void testCreateUserWithSameLoginFail() {
        UserProfile user1 = new UserProfile("test", "testpass1","1@mail.com");
        UserProfile user2 = new UserProfile("test", "testpass2","2@mail.com");
        final Response resp1 = target("user").request("application/json").put(Entity.json(user1));
        final Response resp2 = target("user").request("application/json").put(Entity.json(user2));
        assertEquals(resp2.getStatus(), FORBIDDEN);
    }

    @Test
    public void testCreateUserWithSameEmailFail() {
        UserProfile user1 = new UserProfile("test1", "testpass1","1@mail.com");
        UserProfile user2 = new UserProfile("test2", "testpass2","1@mail.com");
        final Response resp1 = target("user").request("application/json").put(Entity.json(user1));
        final Response resp2 = target("user").request("application/json").put(Entity.json(user2));
        assertEquals(resp2.getStatus(), FORBIDDEN);
    }

    @Test
    public void testEditUser() {
        UserProfile user = new UserProfile("test1", "testpass1","1@mail.com");
        final String json = target("user").request("application/json").put(Entity.json(user), String.class);
        assertEquals("{ \"id\": \"1\" }", json);

        target("session").request("application/json").put(Entity.json(user), String.class);
        final String jsonCheckSignIn = target("session").request().get(String.class);
        assertEquals("{ \"id\": \"1\" }", jsonCheckSignIn);

        UserProfile userEdit = new UserProfile("test1Edit", "testpass1Edit","1Edit@mail.com");
        final String jsonEditUser = target("user").path("1").request().post(Entity.json(userEdit),String.class);
        assertEquals("{ \"id\": \"1\" }", jsonEditUser);

        final String userInfo = target("user").path("1").request().get(String.class);
        assertNotEquals("{\n  \"id\": 1,\n  \"login\": \"test1\",\n  \"email\": \"1@mail.com\" \n}", userInfo);
        assertEquals("{\n  \"id\": 1,\n  \"login\": \"test1Edit\",\n  \"email\": \"1Edit@mail.com\" \n}", userInfo);
    }

    @Test
    public void testEditUserWithUnauthorizedUser() {
        UserProfile user = new UserProfile("test1", "testpass1","1@mail.com");
        final String json = target("user").request("application/json").put(Entity.json(user), String.class);
        assertEquals("{ \"id\": \"1\" }", json);

        final Response checkSignIn = target("session").request().get();
        assertEquals(checkSignIn.getStatus(), UNAUTHORIZED);

        UserProfile userEdit = new UserProfile("test1Edit", "testpass1Edit","1Edit@mail.com");
        final Response editUser = target("user").path("1").request().post(Entity.json(userEdit));
        assertEquals(editUser.getStatus(), FORBIDDEN);
    }

    @Test
    public void testEditOtherUser() {
        UserProfile user = new UserProfile("test1", "testpass1","1@mail.com");
        final String json = target("user").request("application/json").put(Entity.json(user), String.class);
        assertEquals("{ \"id\": \"1\" }", json);

        target("session").request("application/json").put(Entity.json(user), String.class);
        final String jsonCheckSignIn = target("session").request().get(String.class);
        assertEquals("{ \"id\": \"1\" }", jsonCheckSignIn);

        UserProfile userEdit = new UserProfile("test1Edit", "testpass1Edit","1Edit@mail.com");
        final Response editUser = target("user").path("2").request().post(Entity.json(userEdit));
        assertEquals(editUser.getStatus(), FORBIDDEN);

        final String userInfo = target("user").path("1").request().get(String.class);
        assertEquals("{\n  \"id\": 1,\n  \"login\": \"test1\",\n  \"email\": \"1@mail.com\" \n}", userInfo);
    }

    @Test
    public void testDeleteUser() {
        UserProfile user = new UserProfile("test1", "testpass1","1@mail.com");
        final String json = target("user").request("application/json").put(Entity.json(user), String.class);
        assertEquals("{ \"id\": \"1\" }", json);

        target("session").request("application/json").put(Entity.json(user), String.class);
        final String jsonCheckSignIn = target("session").request().get(String.class);
        assertEquals("{ \"id\": \"1\" }", jsonCheckSignIn);

        final Response deleteUser = target("user").path("1").request().delete();
        assertEquals(deleteUser.getStatus(),OK);

        final Response checkSignInAfterDeleteUser = target("session").request().get();
        assertEquals(checkSignInAfterDeleteUser.getStatus(), UNAUTHORIZED);
    }

    @Test
    public void testDeleteOtherUser() {
        UserProfile user = new UserProfile("test1", "testpass1","1@mail.com");
        final String json = target("user").request("application/json").put(Entity.json(user), String.class);
        assertEquals("{ \"id\": \"1\" }", json);

        target("session").request("application/json").put(Entity.json(user), String.class);
        final String jsonCheckSignIn = target("session").request().get(String.class);
        assertEquals("{ \"id\": \"1\" }", jsonCheckSignIn);

        final Response deleteUser = target("user").path("2").request().delete();
        assertEquals(deleteUser.getStatus(), FORBIDDEN);
    }

    @Test
    public void testDeleteWithUnauthorizedUser() {
        UserProfile user = new UserProfile("test1", "testpass1","1@mail.com");
        final String json = target("user").request("application/json").put(Entity.json(user), String.class);
        assertEquals("{ \"id\": \"1\" }", json);

        final Response checkSignIn = target("session").request().get();
        assertEquals(checkSignIn.getStatus(), UNAUTHORIZED);

        final Response deleteUser = target("user").path("1").request().delete();
        assertEquals(deleteUser.getStatus(), FORBIDDEN);
    }
}
