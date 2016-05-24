package rest;

import base.AccountService;
import account.UserProfile;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 * Created by snach
 */
@Singleton
@Path("/user")
public class Users {

    @SuppressWarnings("unused")
    @Inject
    private main.Context context;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        final Collection<UserProfile> allUsers = context.get(AccountService.class).getAllUsers();
        return Response.status(Response.Status.OK).entity(allUsers.toArray(new UserProfile[allUsers.size()])).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserByID(@PathParam("id") long id) {
        final UserProfile user = context.get(AccountService.class).getUserByID(id);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            final String status = "{\n  \"id\": " + user.getUserID() + ",\n  " + "\"login\": \""
                    + user.getLogin() + "\",\n" + "  \"email\": \"" + user.getEmail() + "\" \n}";
            return Response.status(Response.Status.OK).entity(status).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(UserProfile user, @Context HttpHeaders headers) {
        if (context.get(AccountService.class).addUser(user)) {
            final String status = "{ \"id\": \"" + user.getUserID() + "\" }";
            return Response.status(Response.Status.OK).entity(status).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @POST
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editUser(UserProfile user, @PathParam("id") long id, @Context HttpHeaders headers, @Context HttpServletRequest request) {
        final String sessionID = request.getSession().getId();
        final UserProfile userSelf = context.get(AccountService.class).getUserBySession(sessionID);

        if ((user != null) && (userSelf != null) && (userSelf.getUserID() == id)) {
            final UserProfile userToEdit = context.get(AccountService.class).getUserByID(id);
            context.get(AccountService.class).editUser(userToEdit, user);
            final String status = "{ \"id\": \"" + id + "\" }";
            return Response.status(Response.Status.OK).entity(status).build();
        } else {
            final String status = "{ \"status\": 403, \"message\": \"Чужой юзер\" }";
            return Response.status(Response.Status.FORBIDDEN).entity(status).build();
        }
    }

    @DELETE
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("id") long id, @Context HttpHeaders headers, @Context HttpServletRequest request) {
        final String sessionID = request.getSession().getId();
        final UserProfile deleteUser = context.get(AccountService.class).getUserBySession(sessionID);

        if ((deleteUser != null) && (deleteUser.getUserID() == id)) {
            context.get(AccountService.class).deleteSession(sessionID);
            context.get(AccountService.class).deleteUser(id);
            return Response.status(Response.Status.OK).build();
        } else {
            final String status = "{ \"status\": \"403\", \"message\": \"Чужой юзер\" }";
            return Response.status(Response.Status.FORBIDDEN).entity(status).build();
        }
    }
}