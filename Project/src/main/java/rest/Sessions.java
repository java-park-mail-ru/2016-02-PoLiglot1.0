package rest;

import main.AccountService;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Snach
 */
@Singleton
@Path("/session")
public class Sessions {
    private AccountService accountService;

    public Sessions(AccountService accountService) {
        this.accountService = accountService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkSignInUser(@Context HttpServletRequest request) {
        final String sessionID = request.getSession().getId();
        if (accountService.isLoggedIn(sessionID)) {
            String status = "{ \"id\": \"" + accountService.getUserBySession(sessionID).getUserID() + "\" }";
            return Response.status(Response.Status.OK).entity(status).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signInUser(UserProfile user, @Context HttpHeaders headers, @Context HttpServletRequest request) {
        UserProfile onlineUser = accountService.getUserByLogin(user.getLogin());
        final String sessionID = request.getSession().getId();
        if (onlineUser != null && accountService.checkAuth(onlineUser.getLogin(), onlineUser.getPassword())
                && !accountService.isLoggedIn(sessionID)) {
            final String sessionId = request.getSession().getId();
            accountService.addSession(sessionId, onlineUser);
            String status = "{ \"id\": \"" + onlineUser.getUserID() + "\" }";
            return Response.status(Response.Status.OK).entity(status).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response logOut(@Context HttpServletRequest request) {
        accountService.deleteSession(request.getSession().getId());
        return Response.status(Response.Status.OK).build();
    }
}
