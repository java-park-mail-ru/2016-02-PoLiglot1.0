package rest;

import main.AccountService;
import main.AccountServiceImpl;

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
            String status = "{\n  \"id\": " + user.getUserID() + ",\n  " + "\"login\": \""
                    + user.getLogin() + "\",\n" + "  \"email\": \"" + user.getEmail() + "\" \n}";
            return Response.status(Response.Status.OK).entity(status).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(UserProfile user, @Context HttpHeaders headers) {
        if (context.get(AccountService.class).addUser(user)) {
            String status = "{ \"id\": \"" + user.getUserID() + "\" }";
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
        String sessionID = request.getSession().getId();
        UserProfile userSelf = context.get(AccountService.class).getUserBySession(sessionID);
        UserProfile bufUser = new UserProfile("admin", "admin", "admin@email.ru");
        if ((user != null) && (userSelf.getUserID() == id)) {
            UserProfile userToEdit = context.get(AccountService.class).getUserByID(id);
            context.get(AccountService.class).editUser(userToEdit, user);
            String status = "{ \"id\": \"" + id + "\" }";
            return Response.status(Response.Status.OK).entity(status).build();
        } else {
            String status = "{ \"status\": 403, \"message\": \"Чужой юзер\" }";
            return Response.status(Response.Status.FORBIDDEN).entity(status).build();
        }
    }

    @DELETE
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("id") long id, @Context HttpHeaders headers, @Context HttpServletRequest request) {
        String sessionID = request.getSession().getId();
        UserProfile deleteUser = context.get(AccountService.class).getUserByID(id);
        if (context.get(AccountService.class).getUserBySession(sessionID).equals(deleteUser) &&
                context.get(AccountService.class).isLoggedIn(sessionID)) {
            context.get(AccountService.class).deleteSession(sessionID);
            context.get(AccountService.class).deleteUser(id);
            return Response.status(Response.Status.OK).build();
        } else {
            String status = "{ \"status\": \"403\", \"message\": \"Чужой юзер\" }";
            return Response.status(Response.Status.FORBIDDEN).entity(status).build();
        }
    }
}