package rest;

import main.AccountService;
import javax.inject.Inject;
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

    @Inject
    private main.Context context;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkSignInUser(@Context HttpServletRequest request) {
        final String sessionID = request.getSession().getId();
        if (context.get(AccountService.class).isLoggedIn(sessionID)) {
            String status = "{ \"id\": \"" + context.get(AccountService.class).getUserBySession(sessionID).getUserID() + "\" }";
            return Response.status(Response.Status.OK).entity(status).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signInUser(UserProfile user, @Context HttpHeaders headers, @Context HttpServletRequest request) {
        UserProfile onlineUser = context.get(AccountService.class).getUserByLogin(user.getLogin());
        final String sessionID = request.getSession().getId();
        if (onlineUser != null && context.get(AccountService.class).checkAuth(user.getLogin(), user.getPassword())
                && !context.get(AccountService.class).isLoggedIn(sessionID)) {
            final String sessionId = request.getSession().getId();
            context.get(AccountService.class).addSession(sessionId, onlineUser);
            String status = "{ \"id\": \"" + onlineUser.getUserID() + "\" }";
            return Response.status(Response.Status.OK).entity(status).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response logOut(@Context HttpServletRequest request) {
        context.get(AccountService.class).deleteSession(request.getSession().getId());
        return Response.status(Response.Status.OK).build();
    }
}
