package rest;

import base.AccountService;
import account.AccountServiceImpl;
import account.UserProfile;
import main.cnf.Config;
import main.Context;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Application;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by Snach on 28.03.16.
 */
public class SessionsTest extends JerseyTest {
    @Override
    protected Application configure() {
        final Context context = new Context();

        final Config serverConfig = new Config(true);
        serverConfig.connectToDB();

        context.put(AccountService.class, new AccountServiceImpl(serverConfig.getConfiguration()));

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

    @SuppressWarnings("ConstantNamingConvention")
    private static final int OK = 200;
    private static final int UNAUTHORIZED = 401;
    private static final int BAD_REQUEST = 400;


    @Test
    public void testSignInUser() {
        final UserProfile user = new UserProfile("test1", "testpass1", "1@mail.com");
        target("user").request("application/json").put(Entity.json(user), String.class);
        final String jsonSession = target("session").request("application/json").put(Entity.json(user), String.class);
        assertEquals("{ \"id\": \"1\" }", jsonSession);
    }

    @Test
    public void testSignInUserFail() {
        final UserProfile user = new UserProfile("test1", "testpass1", "1@mail.com");
        final UserProfile userNonExist = new UserProfile("test123", "testpass123", "123@mail.com");
        target("user").request("application/json").put(Entity.json(user), String.class);
        final Response session = target("session").request("application/json").put(Entity.json(userNonExist));
        assertEquals(session.getStatus(), BAD_REQUEST);
    }

    @Test
    public void testSignInWithWrongPassword() {
        final UserProfile user = new UserProfile("test1", "testpass1", "1@mail.com");
        final UserProfile userWithWrongPassword = new UserProfile("test1", "wrongPass", "1@mail.com");
        target("user").request("application/json").put(Entity.json(user), String.class);
        final Response session = target("session").request("application/json").put(Entity.json(userWithWrongPassword));
        assertEquals(session.getStatus(), BAD_REQUEST);
    }

    @Test
    public void testCheckSignInUser() {
        final UserProfile user = new UserProfile("test1", "testpass1", "1@mail.com");
        target("user").request("application/json").put(Entity.json(user), String.class);
        target("session").request("application/json").put(Entity.json(user), String.class);
        final String jsonCheckSignIn = target("session").request().get(String.class);
        assertEquals("{ \"id\": \"1\" }", jsonCheckSignIn);
    }

    @Test
    public void testCheckSignInUserFail() {
        final UserProfile user = new UserProfile("test1", "testpass1", "1@mail.com");
        target("user").request("application/json").put(Entity.json(user), String.class);
        final Response checkSignIn = target("session").request().get();
        assertEquals(checkSignIn.getStatus(), UNAUTHORIZED);
    }

    @Test
    public void testLogOut() {
        final UserProfile user = new UserProfile("test1", "testpass1", "1@mail.com");
        target("user").request("application/json").put(Entity.json(user), String.class);
        target("session").request("application/json").put(Entity.json(user), String.class);
        final String jsonCheckSignIn = target("session").request().get(String.class);
        assertEquals("{ \"id\": \"1\" }", jsonCheckSignIn);
        final Response logOut = target("session").request().delete();
        assertEquals(logOut.getStatus(), OK);
        final Response checkSignInAfterLogOut = target("session").request().get();
        assertEquals(checkSignInAfterLogOut.getStatus(), UNAUTHORIZED);
    }
}
