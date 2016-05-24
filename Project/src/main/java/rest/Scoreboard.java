package rest;


import account.LoginAndScore;
import base.AccountService;
import main.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Snach on 15.04.16.
 */

@Singleton
@Path("/scoreboard")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Scoreboard {

    @SuppressWarnings("unused")
    @Inject
    private Context context;

    @GET
    public Response getScoreboard() {
        final AccountService accountService = context.get(AccountService.class);
        try {
            final List<LoginAndScore> topUsers = accountService.getTopUsers().stream()
                    .map(user -> new LoginAndScore(user.getLogin(), user.getScore()))
                    .collect(Collectors.toList());

            return Response.status(Response.Status.OK).entity(topUsers.toArray(new LoginAndScore[topUsers.size()])).build();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_IMPLEMENTED).build();
        }
    }

}
